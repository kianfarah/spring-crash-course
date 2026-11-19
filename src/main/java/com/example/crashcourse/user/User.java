package com.example.crashcourse.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// A Java record: an immutable data class. The compiler generates the constructor,
// getters (id(), username(), email(), fullName()), equals/hashCode and toString.
// Jackson turns it into JSON automatically: {"id":1,"username":"ada","email":"ada@example.com","fullName":"Ada Lovelace"}
//
// Spring Data JDBC maps it to the "users" table (fullName -> column full_name). @Id marks the primary key:
// id == null  -> save() does an INSERT and the database generates the id
// id != null  -> save() does an UPDATE
@Table("users")
public record User(@Id Long id, String username, String email, String fullName) {
}
