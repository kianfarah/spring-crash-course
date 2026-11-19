package com.example.crashcourse.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController = @Controller + @ResponseBody: return values are written as JSON.
// The controller only translates HTTP <-> Java and delegates the real work to the service.
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    // GET /api/users/1  -> 200, or 404 via UserNotFoundException + GlobalExceptionHandler
    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // POST /api/users
    // @Valid runs the checks on CreateUserRequest; if one fails Spring answers 400 before this method runs.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    // DELETE /api/users/1  -> 204, or 404
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
