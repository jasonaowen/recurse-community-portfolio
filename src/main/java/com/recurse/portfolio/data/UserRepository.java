package com.recurse.portfolio.data;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
    extends CrudRepository<User, Integer>
{
    @Query("select * from users where recurse_profile_id = :recurseProfileId")
    Optional<User> findByRecurseProfileId(int recurseProfileId);

    @Query(
        "select p.* " +
            "from projects p" +
            "  inner join project_authors pa" +
            "    on p.project_id = pa.project_id " +
            "where pa.author_id = :authorId" +
            "  and p.visibility in (:visibilities) " +
            "order by p.project_id asc"
    )
    List<Project> findProjectsByAuthor(
        int authorId,
        List<String> visibilities
    );
}
