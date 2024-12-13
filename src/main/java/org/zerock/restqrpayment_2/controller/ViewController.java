package org.zerock.restqrpayment_2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    
    @GetMapping("/owner/dashboard")
    public String adminDashboard() {
        return "owner-dashboard";
    }
    
    @GetMapping("/owner/login")
    public String ownerLogin() {
        return "owner-login";
    }
    
    @GetMapping("/owner/signup")
    public String ownerSignup() {
        return "owner-signup";
    }
    
    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }
    
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }
    
    @GetMapping("/menu/detail")
    public String menuDetail() {
        return "menu-detail";
    }
    
    @GetMapping("/order/complete")
    public String orderComplete() {
        return "order-complete";
    }
    
    @GetMapping("/order/history")
    public String orderHistory() {
        return "order-history";
    }
    
    @GetMapping("/order/status")
    public String orderStatus() {
        return "order-status";
    }
    
    @GetMapping("/find/id")
    public String findId() {
        return "find-id";
    }
    
    @GetMapping("/find/password")
    public String findPassword() {
        return "find-password";
    }
}
