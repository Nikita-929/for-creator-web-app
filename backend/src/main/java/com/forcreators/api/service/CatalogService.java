package com.forcreators.api.service;

import com.forcreators.api.domain.Artisan;
import com.forcreators.api.domain.Category;
import com.forcreators.api.dto.ApiDtos.*;
import com.forcreators.api.exception.NotFoundException;
import com.forcreators.api.repository.ArtisanRepository;
import com.forcreators.api.repository.CategoryRepository;
import com.forcreators.api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final CategoryRepository categories;
    private final ArtisanRepository artisans;
    private final ProductRepository products;
    private final DtoMapper mapper;

    public CatalogService(
            CategoryRepository categories,
            ArtisanRepository artisans,
            ProductRepository products,
            DtoMapper mapper
    ) {
        this.categories = categories;
        this.artisans = artisans;
        this.products = products;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categories.findAll().stream().map(mapper::toCategory).toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(AuthService.sanitize(request.name()))
                .type(request.type())
                .parent(request.parentId() == null ? null : categories.findById(request.parentId())
                        .orElseThrow(() -> new NotFoundException("Parent category not found")))
                .build();
        return mapper.toCategory(categories.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categories.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        category.setName(AuthService.sanitize(request.name()));
        category.setType(request.type());
        category.setParent(request.parentId() == null ? null : categories.findById(request.parentId())
                .orElseThrow(() -> new NotFoundException("Parent category not found")));
        return mapper.toCategory(categories.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categories.delete(categories.findById(id).orElseThrow(() -> new NotFoundException("Category not found")));
    }

    @Transactional(readOnly = true)
    public List<ArtisanSummary> listArtisans() {
        return artisans.findAll().stream().map(mapper::toArtisanSummary).toList();
    }

    @Transactional(readOnly = true)
    public ArtisanResponse getArtisan(Long id) {
        Artisan artisan = artisans.findById(id).orElseThrow(() -> new NotFoundException("Artisan not found"));
        return mapper.toArtisan(artisan, products.findByArtisanId(id));
    }

    @Transactional
    public ArtisanResponse createArtisan(ArtisanRequest request) {
        Artisan artisan = Artisan.builder()
                .name(AuthService.sanitize(request.name()))
                .bio(AuthService.sanitize(request.bio()))
                .photo(request.photo())
                .workshopLocation(AuthService.sanitize(request.workshopLocation()))
                .processDescription(AuthService.sanitize(request.processDescription()))
                .build();
        Artisan saved = artisans.save(artisan);
        return mapper.toArtisan(saved, List.of());
    }

    @Transactional
    public ArtisanResponse updateArtisan(Long id, ArtisanRequest request) {
        Artisan artisan = artisans.findById(id).orElseThrow(() -> new NotFoundException("Artisan not found"));
        artisan.setName(AuthService.sanitize(request.name()));
        artisan.setBio(AuthService.sanitize(request.bio()));
        artisan.setPhoto(request.photo());
        artisan.setWorkshopLocation(AuthService.sanitize(request.workshopLocation()));
        artisan.setProcessDescription(AuthService.sanitize(request.processDescription()));
        return mapper.toArtisan(artisans.save(artisan), products.findByArtisanId(id));
    }

    @Transactional
    public void deleteArtisan(Long id) {
        artisans.delete(artisans.findById(id).orElseThrow(() -> new NotFoundException("Artisan not found")));
    }
}
