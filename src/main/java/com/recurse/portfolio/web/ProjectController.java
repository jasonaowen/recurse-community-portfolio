package com.recurse.portfolio.web;

import com.recurse.portfolio.data.Project;
import com.recurse.portfolio.data.ProjectRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import com.recurse.portfolio.security.Visibility;
import com.recurse.portfolio.security.VisibilityException;
import com.recurse.portfolio.security.VisibilityPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Set;

@Controller
public class ProjectController {
    @Autowired
    ProjectRepository repository;

    @GetMapping("/project/new")
    public ModelAndView getNewProject(
        @CurrentUser User currentUser
    ) {
        if (currentUser == null) {
            throw new VisibilityException(Visibility.PRIVATE);
        }
        var mv = new ModelAndView("projects/new");
        var project = new Project();
        project.setVisibility(Visibility.PRIVATE);
        mv.addObject("authors", Set.of(currentUser));
        mv.addObject("project", project);
        return mv;
    }

    @PostMapping("/project/")
    @Transactional
    public RedirectView postNewProject(
        @CurrentUser User currentUser,
        Project postedProject
    ) {
        if (currentUser == null) {
            throw new VisibilityException(Visibility.PRIVATE);
        }
        Project newProject = new Project();
        updateMutableProjectValues(newProject, postedProject);
        Project savedProject = repository.save(newProject);
        repository.addProjectAuthor(
            savedProject.getProjectId(),
            currentUser.getUserId()
        );
        return new RedirectView("/project/" + savedProject.getProjectId());
    }

    @GetMapping("/project/{projectId}")
    public ModelAndView showUser(
        @CurrentUser User currentUser,
        @PathVariable Integer projectId
    ) {
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));
        Set<User> authors = repository.findProjectAuthors(projectId);

        var policy = new VisibilityPolicy<>(
            project.getVisibility(),
            "projects/author",
            "projects/peer",
            "projects/public"
        );

        ModelAndView mv = new ModelAndView(
            policy.evaluate(authors, currentUser)
        );

        mv.addObject("project", project);
        mv.addObject("authors", authors);
        return mv;
    }

    @GetMapping("/project/{projectId}/edit")
    public ModelAndView getEditProject(
        @CurrentUser User currentUser,
        @PathVariable Integer projectId
    ) {
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));
        Set<User> authors = repository.findProjectAuthors(projectId);

        if (!authors.contains(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        ModelAndView mv = new ModelAndView("projects/edit");
        mv.addObject("project", project);
        mv.addObject("authors", authors);
        return mv;
    }

    @PostMapping("/project/{id}/edit")
    public RedirectView postEditProject(
        @CurrentUser User currentUser,
        @PathVariable(name = "id") Integer projectId,
        Project postedProject
    ) {
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));
        Set<User> authors = repository.findProjectAuthors(projectId);
        if (!authors.contains(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        updateMutableProjectValues(project, postedProject);
        repository.save(project);

        return new RedirectView("/project/" + projectId);
    }

    private void updateMutableProjectValues(
        Project project,
        Project postedProject
    ) {
        project.setName(postedProject.getName());
        project.setPublicDescription(postedProject.getPublicDescription());
        project.setInternalDescription(postedProject.getInternalDescription());
        project.setPrivateDescription(postedProject.getPrivateDescription());
        project.setVisibility(postedProject.getVisibility());
    }
}
