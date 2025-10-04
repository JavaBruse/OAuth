package com.javabruse.controller;

import com.javabruse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home(Authentication authentication,
                       Model model,
                       @RequestParam(value = "logout", required = false) String logout) {

        if (logout != null) {
            model.addAttribute("showLogoutMessage", true);
        }

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            String givenName = (String) attributes.get("given_name");

            model.addAttribute("givenName", givenName);
            model.addAttribute("isAuthenticated", true);
            return "home";
        } else {
            model.addAttribute("isAuthenticated", false);
            return "home";
        }
    }

    @GetMapping("/secured")
    public String secured(Authentication authentication, Model model) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            if (oAuth2User != null) {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                String givenName = (String) attributes.get("given_name");
                String email = (String) attributes.get("email");
                String id = attributes.get("sub") != null ? attributes.get("sub").toString() :
                        attributes.get("id") != null ? attributes.get("id").toString() : "N/A";

                model.addAttribute("givenName", givenName);
                model.addAttribute("email", email);
                model.addAttribute("userId", id);
                model.addAttribute("isAuthenticated", true);
                return "secured";
            }
        }

        model.addAttribute("isAuthenticated", false);
        return "secured";
    }
}