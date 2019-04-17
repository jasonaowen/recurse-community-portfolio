package com.recurse.portfolio.data;

import org.springframework.data.jdbc.repository.query.Modifying;
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

    @Modifying
    @Query("insert into project_authors(project_id, author_id) " +
        "values (:projectId, :authorId) " +
        "on conflict(author_id, project_id) do nothing")
    void addProjectAuthor(int projectId, int authorId);
}
