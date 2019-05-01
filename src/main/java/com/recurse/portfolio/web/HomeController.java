package com.recurse.portfolio.web;

import com.recurse.portfolio.data.Project;
import com.recurse.portfolio.data.ProjectAndAuthor;
import com.recurse.portfolio.data.ProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@Controller
public class HomeController {
    private static final int LIMIT = 20;

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping("/")
    public ModelAndView index(@CurrentUser User currentUser) {
        return new ModelAndView("home")
            .addObject("projects", projectsForUser(currentUser));
    }

    private Collection<Project> projectsForUser(User currentUser) {
        if (currentUser == null) {
            var projectAuthors = projectRepository.getPublicProjects(LIMIT);
            return ProjectAndAuthor.collect(projectAuthors);
        } else {
            var projectAuthors = projectRepository.getProjectsVisibleToUser(
                currentUser.getUserId(),
                LIMIT
            );
            return ProjectAndAuthor.collect(projectAuthors);
        }
    }
}
