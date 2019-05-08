package com.recurse.portfolio.web;

import com.recurse.portfolio.data.DisplayProject;
import com.recurse.portfolio.data.DisplayProjectRepository;
import com.recurse.portfolio.data.Tag;
import com.recurse.portfolio.data.TagRepository;
import com.recurse.portfolio.data.User;
import com.recurse.portfolio.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TagController {
    @Autowired
    private DisplayProjectRepository displayProjectRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/tag/{tagId}")
    public ModelAndView showProject(
        @CurrentUser User currentUser,
        @PathVariable Integer tagId,
        Pageable pageable
    ) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("tag", tagId));
        Page<DisplayProject> projects
            = displayProjectRepository.displayProjectsWithTagForUser(
            tag,
            currentUser,
            pageable
        );
        return new ModelAndView("tags/tag")
            .addObject("projects", projects)
            .addObject("tag", tag);
    }
}
