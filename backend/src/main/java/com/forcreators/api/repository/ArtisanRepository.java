package com.forcreators.api.repository;

import com.forcreators.api.domain.Artisan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtisanRepository extends JpaRepository<Artisan, Long> {
}
