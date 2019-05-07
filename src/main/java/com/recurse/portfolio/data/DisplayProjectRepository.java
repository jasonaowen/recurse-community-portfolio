package com.recurse.portfolio.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DisplayProjectRepository {
    @Autowired
    ProjectRepository projectRepository;

    public Page<DisplayProject> displayProjectsForUser(
        User user,
        Pageable pageable
    ) {
        if (user == null) {
            return new PageImpl<>(
                toDisplayProject(
                    projectRepository.getPublicProjects(
                        pageable.getPageSize(),
                        (int) pageable.getOffset()
                    ),
                    user
                ),
                pageable,
                projectRepository.getPublicProjectsCount()
            );
        } else {
            return new PageImpl<>(
                toDisplayProject(
                    projectRepository.getProjectsVisibleToUser(
                        user.getUserId(),
                        pageable.getPageSize(),
                        (int) pageable.getOffset()
                    ),
                    user
                ),
                pageable,
                projectRepository.getProjectsVisibleToUserCount(user.getUserId())
            );
        }
    }

    private List<DisplayProject> toDisplayProject(
        List<ProjectAndAuthor> input,
        User user
    ) {
        return ProjectAndAuthor.collect(input)
            .stream()
            .map(p -> DisplayProject.fromProjectForUser(p, user))
            .collect(Collectors.toUnmodifiableList());
    }
}
