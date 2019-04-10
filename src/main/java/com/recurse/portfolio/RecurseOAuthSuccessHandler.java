package com.recurse.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Log
public class RecurseOAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        log.info(() -> String.format(
            "Logged in: %s", authentication.getName()
        ));
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            Map<String, Object> userAttributes =
                ((DefaultOAuth2User) authentication.getPrincipal())
                .getAttributes();
            RecurseProfile profile = new ObjectMapper().convertValue(userAttributes, RecurseProfile.class);
            log.info("Profile: " + profile.toString());
        }
        response.sendRedirect("/");
    }
}
