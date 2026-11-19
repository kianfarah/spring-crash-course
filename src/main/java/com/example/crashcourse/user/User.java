package com.example.crashcourse.user;

// A Java record: an immutable data class. The compiler generates the constructor,
// getters (id(), username(), email()), equals/hashCode and toString.
// Jackson turns it into JSON automatically: {"id":1,"username":"ada","email":"ada@example.com"}
public record User(Long id, String username, String email) {
}
