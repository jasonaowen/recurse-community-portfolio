package com.recurse.portfolio.web;

import com.recurse.portfolio.data.DisplayProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private static final int LIMIT = 10;

    @Autowired
    DisplayProjectRepository projectRepository;

    @GetMapping("/")
    public ModelAndView index(@CurrentUser User currentUser) {
        return new ModelAndView("home")
            .addObject(
                "projects",
                projectRepository.displayProjectsForUser(
                    currentUser,
                    PageRequest.of(0, LIMIT)
                )
            );
    }
}
