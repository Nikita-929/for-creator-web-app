package com.forcreators.api.web;

import com.forcreators.api.dto.ApiDtos.ArtisanResponse;
import com.forcreators.api.dto.ApiDtos.ArtisanSummary;
import com.forcreators.api.dto.ApiDtos.CategoryResponse;
import com.forcreators.api.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return catalog.listCategories();
    }

    @GetMapping("/artisans")
    public List<ArtisanSummary> artisans() {
        return catalog.listArtisans();
    }

    @GetMapping("/artisans/{id}")
    public ArtisanResponse artisan(@PathVariable Long id) {
        return catalog.getArtisan(id);
    }
}
