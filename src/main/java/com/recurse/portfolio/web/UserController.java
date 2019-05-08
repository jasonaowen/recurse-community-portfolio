package com.recurse.portfolio.web;

import com.recurse.portfolio.data.DisplayProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.data.UserRepository;
import com.recurse.portfolio.security.CurrentUser;
import com.recurse.portfolio.security.Visibility;
import com.recurse.portfolio.security.VisibilityException;
import com.recurse.portfolio.security.VisibilityPolicy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

import static com.recurse.portfolio.web.MarkdownHelper.renderMarkdownToHtml;

@Controller
@Log
public class UserController {
    @Autowired
    DisplayProjectRepository displayProjectRepository;

    @Autowired
    UserRepository repository;

    @GetMapping("/user/{userId}")
    public ModelAndView showUser(
        @CurrentUser User currentUser,
        @PathVariable Integer userId,
        Pageable pageable
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("user", userId));

        var policy = new VisibilityPolicy<>(
            requestedUser.getProfileVisibility(),
            "users/self",
            "users/peer",
            "users/public"
        );

        var mv = new ModelAndView(policy.evaluate(requestedUser, currentUser));

        requestedUser.setPublicBio(renderMarkdownToHtml(
            requestedUser.getPublicBio()
        ));
        requestedUser.setInternalBio(renderMarkdownToHtml(
            requestedUser.getInternalBio()
        ));

        return mv.addObject("user", requestedUser)
            .addObject(
                "projects",
                displayProjectRepository.findProjectsByAuthorForUser(
                    requestedUser,
                    currentUser,
                    pageable
                )
            );
    }

    @GetMapping("/user/{userId}/edit")
    public ModelAndView getEditMyProfile(
        @CurrentUser User currentUser,
        @PathVariable Integer userId
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("user", userId));

        var policy = new VisibilityPolicy<>(
            Visibility.PRIVATE,
            "users/edit",
            null,
            null
        );

        return new ModelAndView(policy.evaluate(requestedUser, currentUser))
            .addObject("user", requestedUser);
    }

    @PostMapping("/user/{id}/edit")
    public ModelAndView postEditMyProfile(
        @CurrentUser User currentUser,
        @PathVariable(name = "id") Integer userId,
        @Valid User postedUser,
        BindingResult bindingResult
    ) {
        User requestedUser = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("user", userId));
        if (!requestedUser.equals(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        if (bindingResult.hasErrors()) {
            postedUser.setUserId(currentUser.getUserId());
            return new ModelAndView("users/edit")
                .addObject("user", postedUser);
        } else {
            updateMutableCurrentUserValues(currentUser, postedUser);
            repository.save(currentUser);

            return new ModelAndView(new RedirectView("/user/" + userId));
        }
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
