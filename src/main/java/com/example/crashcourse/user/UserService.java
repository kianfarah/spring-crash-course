package com.example.crashcourse.user;

import com.example.crashcourse.notification.WelcomeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Same public methods as in step 04, but the HashMap is replaced by a repository.
// The controller didn't have to change at all.
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WelcomeService welcomeService;

    public UserService(UserRepository userRepository, WelcomeService welcomeService) {
        this.userRepository = userRepository;
        this.welcomeService = welcomeService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        User saved = userRepository.save(new User(null, user.username(), user.email()));
        welcomeService.welcome(saved.email());
        return saved;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
