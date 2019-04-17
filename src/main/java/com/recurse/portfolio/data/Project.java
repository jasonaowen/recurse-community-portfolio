package com.recurse.portfolio.data;

import com.recurse.portfolio.security.Visibility;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("projects")
public class Project {
    @Id
    Integer projectId;

    Visibility visibility;

    String name;

    @Column("description_private")
    String privateDescription;

    @Column("description_internal")
    String internalDescription;

    @Column("description_public")
    String publicDescription;
}
