package com.recurse.portfolio.web;

import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {
    @ModelAttribute("currentUser")
    public User getCurrentUser(@CurrentUser User currentUser) {
        return currentUser;
    }
}
