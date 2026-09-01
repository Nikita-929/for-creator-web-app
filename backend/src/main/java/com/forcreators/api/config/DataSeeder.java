package com.forcreators.api.config;

import com.forcreators.api.domain.*;
import com.forcreators.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            UserAccountRepository users,
            CategoryRepository categories,
            ArtisanRepository artisans,
            ProductRepository products,
            JournalPostRepository journal,
            PasswordEncoder encoder
    ) {
        return args -> {
            if (products.count() > 0) {
                return;
            }

            users.save(UserAccount.builder()
                    .email("admin@forcreators.in")
                    .name("Studio Admin")
                    .passwordHash(encoder.encode("Admin@12345"))
                    .role(Role.ADMIN)
                    .build());
            users.save(UserAccount.builder()
                    .email("buyer@forcreators.in")
                    .name("Asha Buyer")
                    .passwordHash(encoder.encode("Buyer@12345"))
                    .role(Role.BUYER)
                    .build());

            Category textiles = categories.save(Category.builder().name("Textiles").type(CategoryType.TRADITIONAL).build());
            Category ceramics = categories.save(Category.builder().name("Ceramics").type(CategoryType.TRADITIONAL).build());
            Category metal = categories.save(Category.builder().name("Metalwork").type(CategoryType.TRADITIONAL).build());
            Category home = categories.save(Category.builder().name("Contemporary Home").type(CategoryType.MODERN).build());
            Category jewellery = categories.save(Category.builder().name("Studio Jewellery").type(CategoryType.MODERN).build());
            Category paper = categories.save(Category.builder().name("Paper & Print").type(CategoryType.MODERN).build());

            Artisan meera = artisans.save(Artisan.builder()
                    .name("Meera Iyer")
                    .bio("A fifth-generation weaver from Kanchipuram who collaborates with contemporary colourists while keeping temple-border grammar intact.")
                    .photo("https://images.unsplash.com/photo-1607746882042-944635dfe10e?auto=format&fit=crop&w=800&q=80")
                    .workshopLocation("Kanchipuram, Tamil Nadu")
                    .processDescription("Warp is dressed on a pit loom; zari borders are inlaid by hand over several days before the body is woven in silk.")
                    .build());
            Artisan kabir = artisans.save(Artisan.builder()
                    .name("Ansari Kabir")
                    .bio("Blue-pottery trained in Jaipur, now firing matte stoneware that borrows from Mughal geometry for modern tables.")
                    .photo("https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=800&q=80")
                    .workshopLocation("Jaipur, Rajasthan")
                    .processDescription("Clay is thrown, carved when leather-hard, then glazed in cobalt and celadon before a high-fire reduction.")
                    .build());
            Artisan noor = artisans.save(Artisan.builder()
                    .name("Noor Rahman")
                    .bio("Industrial designer turned metalsmith. Traditional bidri inlay meets brushed brass lighting for apartments.")
                    .photo("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80")
                    .workshopLocation("Hyderabad / Bengaluru studio")
                    .processDescription("Sheets are formed, inlaid with silver wire, oxidised, then assembled with contemporary fittings.")
                    .build());

            products.save(product("Kanjeevaram Temple Border Saree",
                    "Handwoven silk saree with a condensed temple border — wearable heritage for city wardrobes.",
                    textiles, "Sarees", "Kanjeevaram weaving", List.of("silk", "zari"),
                    "5.5 m", "8900", 6, false,
                    List.of("https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1200&q=80"),
                    List.of("Handwoven silk saree with gold temple border"),
                    meera, List.of("silk", "heritage", "wearable")));
            products.save(product("Indigo Block-Print Quilt",
                    "Jaipur dabu resist print on organic cotton, filled lightly for year-round use.",
                    textiles, "Home textiles", "Dabu block print", List.of("cotton", "natural dye"),
                    "Queen 90 × 108 in", "12400", 4, false,
                    List.of("https://shakiraaz-homewares.myshopify.com/cdn/shop/files/Photo25-4-2025_105656am_grande.jpg?v=1745640718"),
                    List.of("Indigo block-printed cotton quilt on a bed"),
                    meera, List.of("indigo", "home", "blockprint")));
            products.save(product("Blue Pottery Serving Bowl",
                    "Traditional Jaipur blue pottery motif on a generous serving form.",
                    ceramics, "Serveware", "Blue pottery", List.of("quartz", "glass", "multani mitti"),
                    "28 cm diameter", "3200", 8, false,
                    List.of("https://theindiacrafthouse.com/cdn/shop/files/TBP002E_203.jpg?v=1776602338"),
                    List.of("Blue and white ceramic serving bowl"),
                    kabir, List.of("table", "heritage")));
            products.save(product("One-of-a-kind Celadon Moon Jar",
                    "A single thrown moon jar with a hairline crackle glaze. Once it leaves the kiln, it is not repeated.",
                    ceramics, "Sculpture", "Stoneware throwing", List.of("stoneware", "celadon glaze"),
                    "32 cm height", "28000", 1, true,
                    List.of("https://i.etsystatic.com/25234427/r/il/1482cf/8198740018/il_fullxfull.8198740018_kuio.jpg"),
                    List.of("Celadon moon jar with crackle glaze"),
                    kabir, List.of("unique", "sculpture")));
            products.save(product("Bidri Inlay Tray",
                    "Blackened alloy with sterling silver inlay in a compact serving tray.",
                    metal, "Serveware", "Bidriware", List.of("zinc alloy", "silver"),
                    "32 × 22 cm", "7600", 5, false,
                    List.of("https://www.josephcohenantiques.com/cdn/shop/products/A_Bidri_Tray_DSC_5983-Editwhite_800x.jpg?v=1597006210"),
                    List.of("Dark metal tray with silver inlay pattern"),
                    noor, List.of("metal", "gift")));
            products.save(product("Brass Line Sconce",
                    "A wall light that borrows bidri geometry and finishes in brushed brass for contemporary interiors.",
                    home, "Lighting", "Sheet metal forming", List.of("brass", "LED"),
                    "40 × 12 × 8 cm", "9800", 7, false,
                    List.of("https://cdn.shopify.com/s/files/1/0680/5635/1020/files/Radilum-product-line-7.31_brass-wall-sconce.jpg?v=1785490392"),
                    List.of("Modern brass wall sconce lighting a plaster wall"),
                    noor, List.of("lighting", "modern")));
            products.save(product("Terrazzo Planter Pair",
                    "Cast terrazzo planters in warm aggregate — indoor greenery with a studio-made edge.",
                    home, "Planters", "Cast terrazzo", List.of("cement", "marble chip"),
                    "18 cm and 14 cm", "4200", 12, false,
                    List.of("https://www.milkcan.com.au/cdn/shop/files/PMAT-XXX-GRYT-Malibu-Tall-Grey-Terrazzo-800px-3.jpg?v=1747014181&width=2048"),
                    List.of("Pair of terrazzo planters with small plants"),
                    kabir, List.of("home", "modern")));
            products.save(product("Oxidised Silver Line Earrings",
                    "Minimal hoops with a single granulation nod to temple jewellery, scaled for everyday.",
                    jewellery, "Earrings", "Hand fabrication", List.of("sterling silver"),
                    "3.5 cm drop", "5400", 10, false,
                    List.of("https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=1200&q=80"),
                    List.of("Minimal silver hoop earrings on linen"),
                    noor, List.of("jewellery", "modern")));
            products.save(product("Kalamkari Wall Panel",
                    "Hand-drawn kalamkari narrative on cotton, stretched and ready to hang.",
                    paper, "Wall art", "Kalamkari", List.of("cotton", "natural dyes"),
                    "70 × 90 cm", "15600", 2, false,
                    List.of("https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=1200&q=80"),
                    List.of("Kalamkari illustrated textile hanging on a wall"),
                    meera, List.of("art", "heritage")));
            products.save(product("Risograph Studio Print Set",
                    "Limited risograph prints of loom diagrams and glaze tests — a conversation between workshop and graphic studio.",
                    paper, "Prints", "Risograph", List.of("paper", "soy ink"),
                    "A3, set of 3", "2100", 20, false,
                    List.of("https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=1200&q=80"),
                    List.of("Stack of colourful risograph art prints"),
                    noor, List.of("print", "modern")));

            journal.save(JournalPost.builder()
                    .title("Why we keep the pit loom")
                    .slug("why-we-keep-the-pit-loom")
                    .excerpt("Speed is not the point. Tension, muscle memory, and a border that can only be inlaid by hand.")
                    .coverImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSb30Yjr6C_pEw3cdlWoQBG5Hz702VcxQw_Ba-8-I_bewdqh9gKQC-bpI4&s=10")
                    .body("At For Creators we do not treat traditional technique as costume. A pit loom is slower, louder, and harder to photograph — and it is the only way Meera can seat a temple border so the zari sits in the cloth rather than on it. Contemporary colourways and saree proportions are the modern half of the bargain.")
                    .build());
            journal.save(JournalPost.builder()
                    .title("Studio spotlight: Kabir's celadon")
                    .slug("studio-spotlight-kabir-celadon")
                    .excerpt("Reduction firing, crackle, and why a moon jar is allowed to be unique.")
                    .coverImage("https://i.etsystatic.com/25234427/r/il/1482cf/8198740018/il_fullxfull.8198740018_kuio.jpg")
                    .body("Kabir's moon jar is listed as one-of-a-kind because the kiln will not give us that surface twice. When it sells, the listing becomes sold out — we do not restock uniqueness.")
                    .build());
        };
    }

    private Product product(
            String name, String description, Category category, String subcategory, String technique,
            List<String> materials, String dimensions, String price, int stock, boolean unique,
            List<String> images, List<String> alts, Artisan artisan, List<String> tags
    ) {
        return Product.builder()
                .name(name)
                .description(description)
                .category(category)
                .subcategory(subcategory)
                .technique(technique)
                .materials(materials)
                .dimensions(dimensions)
                .price(new BigDecimal(price))
                .stockQuantity(stock)
                .oneOfAKind(unique)
                .images(images)
                .imageAlts(alts)
                .artisan(artisan)
                .tags(tags)
                .build();
    }
}
