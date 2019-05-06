package com.recurse.portfolio.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("tags")
public class Tag {
    @Id
    Integer tagId;

    String name;
}
