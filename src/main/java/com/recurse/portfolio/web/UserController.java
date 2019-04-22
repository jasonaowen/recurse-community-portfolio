package com.recurse.portfolio.web;

import com.recurse.portfolio.data.User;
import com.recurse.portfolio.data.UserRepository;
import com.recurse.portfolio.security.CurrentUser;
import com.recurse.portfolio.security.Visibility;
import com.recurse.portfolio.security.VisibilityException;
import com.recurse.portfolio.security.VisibilityPolicy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Log
public class UserController {
    @Autowired
    UserRepository repository;

    @GetMapping("/user/{userId}")
    public ModelAndView showUser(
        @CurrentUser User currentUser,
        @PathVariable Integer userId
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        var policy = new VisibilityPolicy<>(
            requestedUser.getProfileVisibility(),
            "users/self",
            "users/peer",
            "users/public"
        );

        ModelAndView mv = new ModelAndView(
            policy.evaluate(requestedUser, currentUser)
        );

        mv.addObject("user", requestedUser);
        return mv;
    }

    @GetMapping("/user/{userId}/edit")
    public ModelAndView getEditMyProfile(
        @CurrentUser User currentUser,
        @PathVariable Integer userId
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        var policy = new VisibilityPolicy<>(
            Visibility.PRIVATE,
            "users/edit",
            null,
            null
        );

        ModelAndView mv = new ModelAndView(
            policy.evaluate(requestedUser, currentUser)
        );

        mv.addObject("user", requestedUser);
        return mv;
    }

    @PostMapping("/user/{id}/edit")
    public RedirectView postEditMyProfile(
        @CurrentUser User currentUser,
        @PathVariable(name = "id") Integer userId,
        User postedUser
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        if (!requestedUser.equals(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        updateMutableCurrentUserValues(currentUser, postedUser);
        repository.save(currentUser);

        return new RedirectView("/user/" + userId);
    }

    private void updateMutableCurrentUserValues(
        User currentUser,
        User postedUser
    ) {
        currentUser.setProfileVisibility(postedUser.getProfileVisibility());
        currentUser.setInternalName(postedUser.getInternalName());
        currentUser.setPublicName(postedUser.getPublicName());
        currentUser.setInternalImageUrl(postedUser.getInternalImageUrl());
        currentUser.setPublicImageUrl(postedUser.getPublicImageUrl());
        currentUser.setInternalBio(postedUser.getInternalBio());
        currentUser.setPublicBio(postedUser.getPublicBio());
    }
}
