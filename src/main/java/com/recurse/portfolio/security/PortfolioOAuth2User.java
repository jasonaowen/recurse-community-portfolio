package com.recurse.portfolio.security;

import com.recurse.portfolio.data.User;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Value
public class PortfolioOAuth2User implements OAuth2User {
    private final OAuth2User oAuth2User;
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}
