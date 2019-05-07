package com.recurse.portfolio.web;

import com.recurse.portfolio.data.DisplayProject;
import com.recurse.portfolio.data.DisplayProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProjectListController {
    @Autowired
    private DisplayProjectRepository projectRepository;

    @GetMapping("/projects")
    public ModelAndView getProjectList(
        @CurrentUser User currentUser,
        Pageable pageable
    ) {
        Page<DisplayProject> projects = projectRepository.displayProjectsForUser(
            currentUser,
            pageable
        );
        return new ModelAndView("projects/list")
            .addObject("projects", projects)
            .addObject("page", pageable);
    }
}
