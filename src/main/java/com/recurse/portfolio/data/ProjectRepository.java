package com.recurse.portfolio.data;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
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

    @Query("select t.* " +
        "from project_tags pt" +
        "  inner join tags t" +
        "    on pt.tag_id = t.tag_id " +
        "where pt.project_id = :projectId")
    Set<Tag> findProjectTags(int projectId);

    @Modifying
    @Query("insert into project_tags(project_id, tag_id) " +
        "values (:projectId, :tagId) " +
        "on conflict(project_id, tag_id) do nothing")
    void addProjectTag(int projectId, int tagId);

    @Modifying
    @Query("delete from project_tags " +
        "where project_id = :projectId" +
        "  and tag_id = :tagId")
    void removeProjectTag(int projectId, int tagId);

    String SELECT = "select" +
        "  p.project_id as project_project_id," +
        "  p.visibility as project_visibility," +
        "  p.name as project_name," +
        "  p.description_private as project_description_private," +
        "  p.description_internal as project_description_internal," +
        "  p.description_public as project_description_public," +
        "  a.user_id as author_user_id," +
        "  a.recurse_profile_id as author_recurse_profile_id," +
        "  a.name_internal as author_name_internal," +
        "  a.profile_visibility as author_profile_visibility," +
        "  a.name_public as author_name_public," +
        "  a.image_url_internal as author_image_url_internal," +
        "  a.image_url_public as author_image_url_public," +
        "  a.bio_internal as author_bio_internal," +
        "  a.bio_public as author_bio_public," +
        "  t.tag_id as tag_tag_id," +
        "  t.name as tag_name " +
        "from visible_projects v" +
        "  inner join projects p" +
        "    on v.project_id = p.project_id" +
        "  inner join project_authors pa" +
        "    on p.project_id = pa.project_id" +
        "  inner join users a" +
        "    on pa.author_id = a.user_id " +
        "  left outer join project_tags pt" +
        "    on p.project_id = pt.project_id" +
        "  left outer join tags t" +
        "    on pt.tag_id = t.tag_id " +
        "order by p.project_id desc";

    @Query("with visible_projects as (" +
        "  select * from projects" +
        "  where visibility = 'PUBLIC'" +
        "  order by project_id desc" +
        "  limit :limit" +
        "  offset :offset" +
        ") " + SELECT)
    List<ProjectAuthorTag> getPublicProjects(int limit, int offset);

    @Query("select count(*) from projects where visibility = 'PUBLIC'")
    int getPublicProjectsCount();

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "  where p.visibility in ('PUBLIC', 'INTERNAL')" +
        "    OR (pa.author_id = :userId)" +
        "  group by 1" +
        "  order by p.project_id desc " +
        "  limit :limit" +
        "  offset :offset" +
        ") " + SELECT)
    List<ProjectAuthorTag> getProjectsVisibleToUser(
        int userId,
        int limit,
        int offset
    );

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "  where p.visibility in ('PUBLIC', 'INTERNAL')" +
        "    OR (pa.author_id = :userId)" +
        "  group by 1" +
        ") " +
        "select count(*) from visible_projects")
    int getProjectsVisibleToUserCount(int userId);

   @Query("with visible_projects as (" +
       "  select p.project_id" +
       "  from projects p" +
       "    inner join project_tags pt" +
       "      on p.project_id = pt.project_id" +
       "  where pt.tag_id = :tagId" +
       "    and p.visibility = 'PUBLIC'" +
       "  order by p.project_id desc" +
       "  limit :limit" +
       "  offset :offset" +
       ") " + SELECT)
    List<ProjectAuthorTag> getPublicProjectsWithTag(
        int tagId,
        int limit,
        int offset
    );

    @Query("select count(*) from project_tags where tag_id = :tagId")
    int getPublicProjectsWithTagCount(int tagId);

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "    inner join project_tags pt" +
        "      on p.project_id = pt.project_id" +
        "  where (p.visibility in ('PUBLIC', 'INTERNAL')" +
        "      OR (pa.author_id = :userId))" +
        "    AND pt.tag_id = :tagId" +
        "  group by 1" +
        "  order by p.project_id desc" +
        "  limit :limit" +
        "  offset :offset" +
        ") " + SELECT)
    List<ProjectAuthorTag> getProjectsVisibleToUserWithTag(
        int userId,
        int tagId,
        int limit,
        int offset
    );

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "    inner join project_tags pt" +
        "      on p.project_id = pt.project_id" +
        "  where (p.visibility in ('PUBLIC', 'INTERNAL')" +
        "      OR (pa.author_id = :userId))" +
        "    AND pt.tag_id = :tagId" +
        "  group by 1" +
        ") " +
        "select count(*) from visible_projects")
    int getProjectsVisibleToUserWithTagCount(int userId, int tagId);

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "  where p.visibility = 'PUBLIC'" +
        "    and pa.author_id = :authorId" +
        "  order by p.project_id desc" +
        "  limit :limit" +
        "  offset :offset" +
        ") " + SELECT)
    List<ProjectAuthorTag> getPublicProjectsWithAuthor(
        int authorId,
        int limit,
        int offset
    );

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "  where p.visibility = 'PUBLIC'" +
        "    and pa.author_id = :authorId" +
        ") select count(*) from visible_projects")
    int getPublicProjectsWithAuthorCount(int authorId);

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "    left outer join project_authors coauthors" +
        "      on p.project_id = coauthors.project_id" +
        "        and pa.author_id != coauthors.author_id" +
        "  where pa.author_id = :authorId" +
        "    and (p.visibility in ('PUBLIC', 'INTERNAL')" +
        "      or pa.author_id = :userId" +
        "      or coauthors.author_id = :userId)" +
        "  order by p.project_id desc" +
        "  limit :limit" +
        "  offset :offset" +
        ") " + SELECT)
    List<ProjectAuthorTag> getProjectsVisibleToUserWithAuthor(
        int userId,
        int authorId,
        int limit,
        int offset
    );

    @Query("with visible_projects as (" +
        "  select p.project_id" +
        "  from projects p" +
        "    inner join project_authors pa" +
        "      on p.project_id = pa.project_id" +
        "    left outer join project_authors coauthors" +
        "      on p.project_id = coauthors.project_id" +
        "        and pa.author_id != coauthors.author_id" +
        "  where pa.author_id = :authorId" +
        "    and (p.visibility in ('PUBLIC', 'INTERNAL')" +
        "      or pa.author_id = :userId" +
        "      or coauthors.author_id = :userId)" +
        ") select count(*) from visible_projects")
    int getProjectsVisibleToUserWithAuthorCount(int userId, int authorId);
}
