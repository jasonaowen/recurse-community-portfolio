package com.recurse.portfolio.data;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Table("project_authors")
public class ProjectAndAuthor {
    @Column(value = "project_id", keyColumn = "project_id")
    Project project;

    @Column(value = "author_id", keyColumn = "user_id")
    User author;

    public static Collection<Project> collect(List<ProjectAndAuthor> list) {
        Map<Integer, Project> projectsById = new HashMap<>();
        for (ProjectAndAuthor pa : list) {
            projectsById.putIfAbsent(pa.project.getProjectId(), pa.project);
            projectsById.get(pa.project.getProjectId()).getAuthors().add(
                pa.getAuthor()
            );
        }
        return projectsById.values();
    }
}
