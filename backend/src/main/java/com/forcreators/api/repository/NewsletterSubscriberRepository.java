package com.forcreators.api.repository;

import com.forcreators.api.domain.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {
    boolean existsByEmailIgnoreCase(String email);
}
