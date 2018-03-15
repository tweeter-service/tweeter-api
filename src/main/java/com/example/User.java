package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;

@Component
@RequestScope
public class User {
    private final Map<String, Object> payload;

    public User(ObjectMapper objectMapper) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null || !(authentication instanceof OAuth2Authentication)) {
            this.payload = Collections.emptyMap();
        } else {
            OAuth2Authentication auth = OAuth2Authentication.class.cast(authentication);
            OAuth2AuthenticationDetails details = OAuth2AuthenticationDetails.class
                    .cast(auth.getDetails());
            String payload = details.getTokenValue().split("\\.")[1];
            try {
                Map<String, Object> params = objectMapper.readValue(
                        Base64Utils.decodeFromUrlSafeString(payload),
                        new TypeReference<Map<String, Object>>() {
                        });
                this.payload = Collections.unmodifiableMap(params);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public Map<String, Object> getPayload() {
        return this.payload;
    }

    public String getUsername() {
        return (String) this.payload.get("sub");
    }
}
