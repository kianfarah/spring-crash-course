package com.example.crashcourse.user;

import com.example.crashcourse.notification.WelcomeService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 1) Plain unit test: NO Spring at all. Runs in milliseconds.
// Because UserService gets its dependencies through the constructor, we can just pass in mocks.
class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final WelcomeService welcomeService = mock(WelcomeService.class);
    private final UserService userService = new UserService(userRepository, welcomeService);

    @Test
    void rejectsDuplicateEmail() {
        when(userRepository.findByEmail("ada@example.com"))
                .thenReturn(Optional.of(new User(1L, "ada", "ada@example.com", null)));

        assertThatThrownBy(() -> userService.create(new CreateUserRequest("ada2", "ada@example.com", null)))
                .isInstanceOf(EmailAlreadyTakenException.class);

        verify(userRepository, never()).save(any());
        verify(welcomeService, never()).welcome(any());
    }
}
