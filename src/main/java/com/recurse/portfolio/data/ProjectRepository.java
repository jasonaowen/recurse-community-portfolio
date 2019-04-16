package com.recurse.portfolio.data;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ProjectRepository
    extends CrudRepository<Project, Integer> {

    @Query("select u.* " +
        "from project_authors pa" +
        "  inner join users u" +
        "    on pa.author_id = u.user_id " +
        "where pa.project_id = :projectId"
    )
    Set<User> findProjectAuthors(int projectId);
}
