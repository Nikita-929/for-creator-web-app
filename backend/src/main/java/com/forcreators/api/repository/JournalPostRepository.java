package com.forcreators.api.repository;

import com.forcreators.api.domain.JournalPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalPostRepository extends JpaRepository<JournalPost, Long> {
    Optional<JournalPost> findBySlug(String slug);
}
