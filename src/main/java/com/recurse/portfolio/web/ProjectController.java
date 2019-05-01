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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.recurse.portfolio.web.MarkdownHelper.renderMarkdownToHtml;

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
        mv.addObject("errors", Collections.emptyList());
        return mv;
    }

    @PostMapping("/project/")
    @Transactional
    public ModelAndView postNewProject(
        @CurrentUser User currentUser,
        Project postedProject
    ) {
        if (currentUser == null) {
            throw new VisibilityException(Visibility.PRIVATE);
        }
        List<String> errors = validate(postedProject);
        if (errors.isEmpty()) {
            Project newProject = new Project();
            updateMutableProjectValues(newProject, postedProject);
            Project savedProject = repository.save(newProject);
            repository.addProjectAuthor(
                savedProject.getProjectId(),
                currentUser.getUserId()
            );
            return new ModelAndView(new RedirectView("/project/" + savedProject.getProjectId()));
        } else {
            return new ModelAndView("projects/new")
                .addObject("authors", Set.of(currentUser))
                .addObject("project", postedProject)
                .addObject("errors", errors);
        }
    }

    @GetMapping("/project/{projectId}")
    public ModelAndView showProject(
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

        project.setPublicDescription(renderMarkdownToHtml(
            project.getPublicDescription()
        ));
        project.setInternalDescription(renderMarkdownToHtml(
            project.getInternalDescription()
        ));
        project.setPrivateDescription(renderMarkdownToHtml(
            project.getPrivateDescription()
        ));

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
        mv.addObject("errors", Collections.emptyList());
        return mv;
    }

    @PostMapping("/project/{id}/edit")
    public ModelAndView postEditProject(
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

        List<String> errors = validate(postedProject);
        if (errors.isEmpty()) {
            updateMutableProjectValues(project, postedProject);
            repository.save(project);

            return new ModelAndView(new RedirectView("/project/" + projectId));
        } else {
            postedProject.setProjectId(projectId);
            return new ModelAndView("projects/edit")
                .addObject("project", postedProject)
                .addObject("errors", errors);
        }
    }

    private List<String> validate(Project postedProject) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(postedProject.getName())) {
            errors.add("name");
        }
        return errors;

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
