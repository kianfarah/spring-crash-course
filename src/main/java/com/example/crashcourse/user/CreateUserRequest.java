package com.example.crashcourse.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// What the client is allowed to send when creating a user (no id!).
// Keeping the API model separate from the database model (User) lets each change independently.
public record CreateUserRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Email String email,
        String fullName) {
}
