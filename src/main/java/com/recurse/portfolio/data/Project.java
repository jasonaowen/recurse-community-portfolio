package com.recurse.portfolio.data;

import com.recurse.portfolio.security.Visibility;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@NoArgsConstructor
@Table("projects")
public class Project {
    @Id
    Integer projectId;

    @Column("author_id")
    Set<ProjectAuthor> authors;

    Visibility visibility;

    String name;

    @Column("description_private")
    String privateDescription;

    @Column("description_internal")
    String internalDescription;

    @Column("description_public")
    String publicDescription;

    public void addAuthor(User author) {
        authors.add(new ProjectAuthor(author.getUserId()));
    }
}
