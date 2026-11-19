package com.example.crashcourse.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// "Database" kept in memory. A real one would need a connection pool, SQL, transactions, schema migrations...
public class UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User save(String username, String email) {
        User user = new User(nextId.getAndIncrement(), username, email);
        users.put(user.id(), user);
        return user;
    }
}
