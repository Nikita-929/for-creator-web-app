package com.forcreators.api.web;

import com.forcreators.api.repository.ProductRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {

    private final ProductRepository products;

    public SeoController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        StringBuilder sb = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                  <url><loc>https://forcreators.example/shop</loc></url>
                  <url><loc>https://forcreators.example/artisans</loc></url>
                  <url><loc>https://forcreators.example/journal</loc></url>
                """);
        products.findAll().forEach(p -> sb.append("  <url><loc>https://forcreators.example/product/")
                .append(p.getId()).append("</loc></url>\n"));
        sb.append("</urlset>");
        return sb.toString();
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return "User-agent: *\nAllow: /\nSitemap: https://forcreators.example/sitemap.xml\n";
    }
}
