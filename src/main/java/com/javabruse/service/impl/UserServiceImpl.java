package com.javabruse.service.impl;

import com.javabruse.entity.User;
import com.javabruse.repository.UserRepository;
import com.javabruse.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String addUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauthToken){
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            if (oAuth2User != null){
                Map<String, Object> attributes = oAuth2User.getAttributes();
                Optional<User> optionalUser = userRepository.findById((String) attributes.get("sub"));
                if (optionalUser.isEmpty()){
                    User user = new User();
                    user.setId((String) attributes.get("sub"));
                    user.setGivenName((String) attributes.get("given_name"));
                    user.setFamilyName((String) attributes.get("family_name"));
                    user.setName((String) attributes.get("name"));
                    user.setEmail((String) attributes.get("email"));
                    user.setPicture((String) attributes.get("picture"));
                    userRepository.save(user);
                }
                return "Hello, " + attributes.get("given_name");
            }
        }
        return "No OAuth2AuthenticationToken found";
    }
}
