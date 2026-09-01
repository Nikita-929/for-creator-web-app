package com.forcreators.api.service;

import com.forcreators.api.domain.ContactMessage;
import com.forcreators.api.domain.CustomOrderRequest;
import com.forcreators.api.domain.JournalPost;
import com.forcreators.api.domain.NewsletterSubscriber;
import com.forcreators.api.domain.RequestStatus;
import com.forcreators.api.dto.ApiDtos.*;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ContactMessageRepository;
import com.forcreators.api.repository.CustomOrderRequestRepository;
import com.forcreators.api.repository.JournalPostRepository;
import com.forcreators.api.repository.NewsletterSubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
public class ContentService {

    private static final Logger log = LoggerFactory.getLogger(ContentService.class);

    private final CustomOrderRequestRepository customOrders;
    private final ContactMessageRepository contacts;
    private final NewsletterSubscriberRepository newsletter;
    private final JournalPostRepository journal;
    private final DtoMapper mapper;
    private final JavaMailSender mailSender;
    private final String adminEmail;

    public ContentService(
            CustomOrderRequestRepository customOrders,
            ContactMessageRepository contacts,
            NewsletterSubscriberRepository newsletter,
            JournalPostRepository journal,
            DtoMapper mapper,
            JavaMailSender mailSender,
            @Value("${app.admin.notify-email}") String adminEmail
    ) {
        this.customOrders = customOrders;
        this.contacts = contacts;
        this.newsletter = newsletter;
        this.journal = journal;
        this.mapper = mapper;
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
    }

    @Transactional
    public CustomOrderResponse submitCustom(CustomOrderRequestDto request) {
        CustomOrderRequest entity = CustomOrderRequest.builder()
                .name(AuthService.sanitize(request.name()))
                .contact(AuthService.sanitize(request.contact()))
                .description(AuthService.sanitize(request.description()))
                .budgetRange(AuthService.sanitize(request.budgetRange()))
                .status(RequestStatus.NEW)
                .build();
        CustomOrderRequest saved = customOrders.save(entity);
        notifyAdmin("New custom order request from " + saved.getName(), saved.getDescription());
        return mapper.toCustom(saved);
    }

    @Transactional(readOnly = true)
    public List<CustomOrderResponse> listCustom() {
        return customOrders.findAll().stream().map(mapper::toCustom).toList();
    }

    @Transactional
    public CustomOrderResponse updateCustomStatus(Long id, RequestStatus status) {
        CustomOrderRequest entity = customOrders.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        entity.setStatus(status);
        return mapper.toCustom(customOrders.save(entity));
    }

    @Transactional
    public MessageResponse contact(ContactRequest request) {
        contacts.save(ContactMessage.builder()
                .name(AuthService.sanitize(request.name()))
                .email(request.email())
                .message(AuthService.sanitize(request.message()))
                .build());
        notifyAdmin("Contact form: " + request.name(), request.message());
        return new MessageResponse("Thanks — we will get back to you shortly.");
    }

    @Transactional
    public MessageResponse subscribe(NewsletterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (!newsletter.existsByEmailIgnoreCase(email)) {
            newsletter.save(NewsletterSubscriber.builder().email(email).build());
        }
        return new MessageResponse("You are on the list. Welcome to For Creators.");
    }

    @Transactional(readOnly = true)
    public List<JournalResponse> journal() {
        return journal.findAll().stream().map(mapper::toJournal).toList();
    }

    @Transactional(readOnly = true)
    public JournalResponse journalBySlug(String slug) {
        return mapper.toJournal(journal.findBySlug(slug).orElseThrow(() -> new NotFoundException("Post not found")));
    }

    @Transactional
    public JournalResponse createJournal(JournalRequest request) {
        String slug = StringUtils.hasText(request.slug())
                ? request.slug()
                : request.title().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        JournalPost post = JournalPost.builder()
                .title(AuthService.sanitize(request.title()))
                .slug(slug)
                .excerpt(AuthService.sanitize(request.excerpt()))
                .body(AuthService.sanitize(request.body()))
                .coverImage(request.coverImage())
                .build();
        return mapper.toJournal(journal.save(post));
    }

    @Transactional
    public void deleteJournal(Long id) {
        journal.delete(journal.findById(id).orElseThrow(() -> new NotFoundException("Post not found")));
    }

    private void notifyAdmin(String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("[For Creators] " + subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.info("Mail not sent (configure SMTP for production): {} — {}", subject, ex.getMessage());
        }
    }
}
