package com.forcreators.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forcreators.api.domain.Product;
import com.forcreators.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutFlowTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProductRepository products;

    @Test
    void guestCheckoutThenDemoPaymentDecrementsInventory() throws Exception {
        Product product = products.findAll().stream()
                .filter(p -> !p.isOneOfAKind() && p.getStockQuantity() > 0)
                .findFirst()
                .orElseThrow();
        int before = product.getStockQuantity();

        String checkoutBody = """
                {
                  "items": [{"productId": %d, "quantity": 1}],
                  "shippingAddress": "12 Loom Lane, Bengaluru 560001",
                  "guestEmail": "guest@example.com",
                  "guestName": "Guest Weaver"
                }
                """.formatted(product.getId());

        MvcResult checkout = mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkoutBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razorpayOrderId").exists())
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andReturn();

        String razorpayOrderId = objectMapper.readTree(checkout.getResponse().getContentAsString())
                .get("razorpayOrderId").asText();

        String verifyBody = """
                {
                  "razorpayOrderId": "%s",
                  "razorpayPaymentId": "pay_demo",
                  "razorpaySignature": "demo"
                }
                """.formatted(razorpayOrderId);

        mockMvc.perform(post("/api/payments/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(verifyBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        Product after = products.findById(product.getId()).orElseThrow();
        assertEquals(before - 1, after.getStockQuantity());
    }
}
