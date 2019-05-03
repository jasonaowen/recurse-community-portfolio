package com.recurse.portfolio.web;

import com.recurse.portfolio.data.Project;
import com.recurse.portfolio.data.User;
import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
public class DisplayProject {
    int projectId;
    String name;
    Set<DisplayAuthor> authors;

    public static DisplayProject fromProjectForUser(
        Project project,
        User currentUser
    ) {
        return new DisplayProject(
            project.getProjectId(),
            project.getName(),
            project.getAuthors().stream()
                .map(u -> DisplayAuthor.fromUserForUser(u, currentUser))
                .collect(Collectors.toUnmodifiableSet())
        );
    }
}
