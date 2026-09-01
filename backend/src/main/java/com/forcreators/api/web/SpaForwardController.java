package com.forcreators.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/",
            "/shop", "/shop/{path:.*}",
            "/product/{path:.*}",
            "/artisans", "/artisans/{path:.*}",
            "/about",
            "/cart", "/checkout",
            "/login", "/register", "/account",
            "/journal", "/journal/{path:.*}",
            "/blog",
            "/contact",
            "/custom-orders",
            "/admin", "/admin/{path:.*}"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
