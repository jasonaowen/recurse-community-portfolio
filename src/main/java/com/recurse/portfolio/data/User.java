package com.recurse.portfolio.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@Data
@Table("users")
public class User {
    @Id
    int userId;

    int recurseProfileId;
    @NonNull String name;
}
