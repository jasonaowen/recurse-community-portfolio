package com.recurse.portfolio.data;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("project_authors")
@Value
public class ProjectAuthor {
    @Id
    int authorId;
}
