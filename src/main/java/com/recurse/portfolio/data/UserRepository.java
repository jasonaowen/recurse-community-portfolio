package com.recurse.portfolio.data;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository
    extends CrudRepository<User, Integer>
{
    @Query("select * from users where recurse_profile_id = :recurseProfileId")
    Optional<User> findByRecurseProfileId(int recurseProfileId);
}
