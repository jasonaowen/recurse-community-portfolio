package com.recurse.portfolio.data;

import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class ProjectAuthorTag {
    Project project;
    User author;
    Tag tag;

    static Collection<Project> collect(List<ProjectAuthorTag> list) {
        Map<Integer, Project> projectsById = new HashMap<>();
        for (ProjectAuthorTag pa : list) {
            projectsById.putIfAbsent(pa.project.getProjectId(), pa.project);
            projectsById.get(pa.project.getProjectId()).getAuthors().add(
                pa.getAuthor()
            );
            if (pa.getTag() != null) {
                projectsById.get(pa.project.getProjectId()).getTags().add(
                    pa.getTag()
                );
            }
        }
        return projectsById.values();
    }
}
