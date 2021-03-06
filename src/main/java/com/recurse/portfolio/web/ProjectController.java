package com.recurse.portfolio.web;

import com.github.slugify.Slugify;
import com.recurse.portfolio.data.DisplayAuthor;
import com.recurse.portfolio.data.Project;
import com.recurse.portfolio.data.ProjectRepository;
import com.recurse.portfolio.data.Tag;
import com.recurse.portfolio.data.TagRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import com.recurse.portfolio.security.Visibility;
import com.recurse.portfolio.security.VisibilityException;
import com.recurse.portfolio.security.VisibilityPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static com.recurse.portfolio.web.MarkdownHelper.renderMarkdownToHtml;


@Controller
public class ProjectController {
    @Autowired
    ProjectRepository repository;

    @Autowired
    TagRepository tagRepository;

    private Slugify slugifier = new Slugify();

    @GetMapping("/project/new")
    public ModelAndView getNewProject(
        @CurrentUser User currentUser
    ) {
        if (currentUser == null) {
            throw new VisibilityException(Visibility.PRIVATE);
        }
        var project = new Project();
        project.setVisibility(Visibility.PRIVATE);
        return new ModelAndView("projects/new")
            .addObject("authors", Set.of(
                DisplayAuthor.fromUserForUser(currentUser, currentUser)))
            .addObject("project", project)
            .addObject("allTags", tagRepository.findAll());
    }

    @PostMapping("/project/new")
    @Transactional
    public ModelAndView postNewProject(
        @CurrentUser User currentUser,
        @Valid Project postedProject,
        BindingResult bindingResult
    ) {
        if (currentUser == null) {
            throw new VisibilityException(Visibility.PRIVATE);
        }
        if (bindingResult.hasErrors()) {
            return new ModelAndView("projects/new")
                .addObject("authors", Set.of(
                    DisplayAuthor.fromUserForUser(currentUser, currentUser)))
                .addObject("project", postedProject);
        } else {
            Project newProject = new Project();
            updateMutableProjectValues(newProject, postedProject);
            Project savedProject = repository.save(newProject);
            repository.addProjectAuthor(
                savedProject.getProjectId(),
                currentUser.getUserId()
            );
            for (Tag tag : postedProject.getTags()) {
                repository.addProjectTag(
                    savedProject.getProjectId(),
                    tag.getTagId()
                );
            }

            return getSlugifiedModelAndView(savedProject.getProjectId(), savedProject.getName());
        }
    }

    @GetMapping("/project/{projectId}/**")
    public ModelAndView showProjectRedirect(
            @CurrentUser User currentUser,
            @PathVariable Integer projectId
    ) {
        return showProject(currentUser, projectId, "");
    }

    @GetMapping("/project/{projectId}/{projectName}")
    public ModelAndView showProject(
        @CurrentUser User currentUser,
        @PathVariable Integer projectId,
        @PathVariable String projectName
    ) {
        // ensure provided project ID is valid
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));

        var policy = new VisibilityPolicy<>(
                project.getVisibility(),
                "projects/author",
                "projects/peer",
                "projects/public"
            );

        Set<User> authors = repository.findProjectAuthors(projectId);

        // will throw exception if current user is denied access to project
        policy.evaluate(authors, currentUser);

        // retrieve name of project from model
        String projectNameFromModel = project.getName();

        String slugifiedProjectName = slugifyString(projectNameFromModel);

        // if slugified version of project name was NOT used in the URL,
        // replace project name with its slugified version and redirect
        if (!projectName.equals(slugifiedProjectName))
        {
            return new ModelAndView(new RedirectView(
                    "/project/" + projectId + "/" + slugifiedProjectName
                ));
        }

        project.setTags(repository.findProjectTags(projectId));

        project.setPublicDescription(renderMarkdownToHtml(
            project.getPublicDescription()
        ));
        project.setInternalDescription(renderMarkdownToHtml(
            project.getInternalDescription()
        ));
        project.setPrivateDescription(renderMarkdownToHtml(
            project.getPrivateDescription()
        ));

        Set<DisplayAuthor> displayAuthors = authors.stream()
            .map(a -> DisplayAuthor.fromUserForUser(a, currentUser))
            .collect(Collectors.toUnmodifiableSet());

        return new ModelAndView(policy.evaluate(authors, currentUser))
            .addObject("project", project)
            .addObject("authors", displayAuthors);
    }

    @GetMapping("/project/edit/{projectId}")
    public ModelAndView getEditProject(
        @CurrentUser User currentUser,
        @PathVariable Integer projectId
    ) {
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));
        project.setTags(repository.findProjectTags(projectId));
        Set<User> authors = repository.findProjectAuthors(projectId);

        if (!authors.contains(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        Set<DisplayAuthor> displayAuthors = authors.stream()
            .map(a -> DisplayAuthor.fromUserForUser(a, currentUser))
            .collect(Collectors.toUnmodifiableSet());


        return new ModelAndView("projects/edit")
            .addObject("project", project)
            .addObject("authors", displayAuthors)
            .addObject("allTags", tagRepository.findAll());
    }

    @PostMapping("/project/edit/{id}")
    public ModelAndView postEditProject(
        @CurrentUser User currentUser,
        @PathVariable(name = "id") Integer projectId,
        @Valid Project postedProject,
        BindingResult bindingResult
    ) {
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new NotFoundException("project", projectId));
        Set<User> authors = repository.findProjectAuthors(projectId);
        if (!authors.contains(currentUser)) {
            throw new VisibilityException(Visibility.PRIVATE);
        }

        if (bindingResult.hasErrors()) {
            postedProject.setProjectId(projectId);
            return new ModelAndView("projects/edit")
                .addObject("project", postedProject);
        } else {
            updateMutableProjectValues(project, postedProject);
            repository.save(project);
            repository.findProjectTags(projectId)
                .stream()
                .filter(t -> !postedProject.getTags().contains(t))
                .map(Tag::getTagId)
                .forEach(tagId -> repository.removeProjectTag(projectId, tagId));
            for (Tag tag : postedProject.getTags()) {
                repository.addProjectTag(
                    projectId,
                    tag.getTagId()
                );
            }

            return getSlugifiedModelAndView(projectId, project.getName());
        }
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

    // slugify, i.e. create URL-friendly version of, the project name
    private String slugifyString(String text)
    {
        return slugifier.slugify(text);
    }

    private ModelAndView getSlugifiedModelAndView(int projectId, String projectName)
    {
        String slugifiedProjectName = slugifyString(projectName);
        return new ModelAndView(new RedirectView("/project/" + projectId + "/" + slugifiedProjectName));
    }
}
