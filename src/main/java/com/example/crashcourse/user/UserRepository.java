package com.example.crashcourse.user;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

// Just an interface: Spring Data generates the implementation at startup.
// ListCrudRepository gives us save, findById, findAll, deleteById, count, ... for free.
public interface UserRepository extends ListCrudRepository<User, Long> {

    // "Derived query": Spring reads the method name and generates
    //   SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
