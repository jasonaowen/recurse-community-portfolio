package com.recurse.portfolio.data;

import com.recurse.portfolio.security.Visibility;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Table("projects")
public class Project {
    @Id
    Integer projectId;

    Visibility visibility;

    @Transient
    Set<User> authors = new HashSet<>();

    @Transient
    Set<Tag> tags = new HashSet<>();

    @NotNull
    @Size(min = 1, max = 100)
    String name;

    @Column("description_private")
    String privateDescription;

    @Column("description_internal")
    String internalDescription;

    @Column("description_public")
    String publicDescription;
}
