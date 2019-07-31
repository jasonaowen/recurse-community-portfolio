package com.recurse.portfolio.data;

import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;
import com.github.slugify.Slugify;

@Value
public class DisplayProject {
    int projectId;
    String name;
    String slugifiedName;
    Set<DisplayAuthor> authors;
    Set<Tag> tags;

    public static DisplayProject fromProjectForUser(
        Project project,
        User currentUser
    ) {

        Slugify slugifier = new Slugify();
        String slugifiedName = slugifier.slugify(project.getName());

        return new DisplayProject(
            project.getProjectId(),
            project.getName(),
            slugifiedName,
            project.getAuthors().stream()
                .map(u -> DisplayAuthor.fromUserForUser(u, currentUser))
                .collect(Collectors.toUnmodifiableSet()),
            project.getTags()
        );
    }
}
