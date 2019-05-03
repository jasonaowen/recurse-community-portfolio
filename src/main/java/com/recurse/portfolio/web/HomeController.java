package com.recurse.portfolio.web;

import com.recurse.portfolio.data.ProjectAndAuthor;
import com.recurse.portfolio.data.ProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private static final int LIMIT = 20;

    @Autowired
    ProjectRepository projectRepository;

    @GetMapping("/")
    public ModelAndView index(@CurrentUser User currentUser) {
        return new ModelAndView("home")
            .addObject("projects", displayProjectsForUser(currentUser));
    }

    private Collection<DisplayProject> displayProjectsForUser(User currentUser) {
        return ProjectAndAuthor.collect(projectsForUser(currentUser)).stream()
            .map(p -> DisplayProject.fromProjectForUser(p, currentUser))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<ProjectAndAuthor> projectsForUser(User currentUser) {
        if (currentUser == null) {
            return projectRepository.getPublicProjects(LIMIT);
        } else {
            return projectRepository.getProjectsVisibleToUser(
                currentUser.getUserId(),
                LIMIT
            );
        }
    }
}
