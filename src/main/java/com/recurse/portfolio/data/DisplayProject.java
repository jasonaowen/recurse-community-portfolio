package com.recurse.portfolio.data;

import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class DisplayProject {
    int projectId;
    String name;
    Set<DisplayAuthor> authors;
    Set<Tag> tags;

    public static DisplayProject fromProjectForUser(
        Project project,
        User currentUser
    ) {
        return new DisplayProject(
            project.getProjectId(),
            project.getName(),
            project.getAuthors().stream()
                .map(u -> DisplayAuthor.fromUserForUser(u, currentUser))
                .collect(Collectors.toUnmodifiableSet()),
            project.getTags()
        );
    }
}
