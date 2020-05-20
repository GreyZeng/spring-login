package org.hui.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zenghui
 * @date 2020-05-20
 */
@Controller
public class LoginController {
    @RequestMapping("/")
    public String home() {
        return "home.html";
    }
    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }
}
