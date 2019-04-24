package com.recurse.portfolio.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recurse.portfolio.data.RecurseProfile;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PortfolioOAuth2UserService
    implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private UserRepository userRepository;

    private DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public PortfolioOAuth2User loadUser(
        OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        RecurseProfile profile = new ObjectMapper().convertValue(
            oAuth2User.getAttributes(),
            RecurseProfile.class
        );
        User user = userRepository
            .findByRecurseProfileId(profile.getUserId())
            .orElseGet(() -> createUserFromProfile(profile));
        return new PortfolioOAuth2User(oAuth2User, user);
    }

    private User createUserFromProfile(RecurseProfile profile) {
        return userRepository.save(User.builder()
            .userId(null)
            .recurseProfileId(profile.getUserId())
            .profileVisibility(Visibility.PRIVATE)
            .internalName(profile.getName())
            .publicName(profile.getName())
            .internalImageUrl(profile.getImageUrl())
            .publicImageUrl(null)
            .internalBio("")
            .publicBio("")
            .build()
        );
    }
}
