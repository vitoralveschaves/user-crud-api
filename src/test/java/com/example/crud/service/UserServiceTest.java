package com.example.crud.service;

import com.example.crud.controller.CreateUserDto;
import com.example.crud.controller.UpdateUserDto;
import com.example.crud.entity.User;
import com.example.crud.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Nested
    class CreateUser {
        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateAUserWithSuccess() {

            //Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "1234"
            );

            //Act
            var output = userService.createUser(input);

            //Assert
            assertNotNull(output);
            assertEquals(input.username(), userArgumentCaptor.getValue().getUsername());
            assertEquals(input.email(), userArgumentCaptor.getValue().getEmail());
            assertEquals(input.password(), userArgumentCaptor.getValue().getPassword());
        }

        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {
            doReturn(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "123"
            );
            assertThrows(RuntimeException.class, () -> userService.createUser(input));
        }
    }

    @Nested
    class GetUserById {

        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());

            var output = userService.getUserById(user.getUserId().toString());

            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should get user by id with success when optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            var output = userService.getUserById(user.getUserId().toString());

            assertTrue(output.isEmpty());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }
    }

    @Nested
    class ListUser {
        @Test
        @DisplayName("Should return all users with success")
        void shouldReturnAllUserWithSuccess() {

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(List.of(user)).when(userRepository).findAll();

            var output = userService.getUsers();

            assertNotNull(output);
            assertEquals(1, output.size());
        }
    }

    @Nested
    class DeleteById {
        @Test
        @DisplayName("Should delete user with success when user exists")
        void shouldDeleteUserWithSuccessWhenUserExists() {

            var userId = UUID.randomUUID();

            doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
            doNothing().when(userRepository).deleteById(uuidArgumentCaptor.capture());

            userService.deleteById(userId.toString());

            assertEquals(userId, uuidArgumentCaptor.getAllValues().get(0));
            assertEquals(userId, uuidArgumentCaptor.getAllValues().get(1));
            verify(userRepository, times(1)).existsById(uuidArgumentCaptor.getAllValues().get(0));
            verify(userRepository, times(1)).deleteById(uuidArgumentCaptor.getAllValues().get(1));
        }

        @Test
        @DisplayName("Should not delete user when user not exists")
        void shouldNotDeleteUserWhenUserNotExists() {
            var userId = UUID.randomUUID();

            doReturn(false).when(userRepository).existsById(uuidArgumentCaptor.capture());

            userService.deleteById(userId.toString());

            assertEquals(userId, uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).existsById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).deleteById(any());
        }
    }

    @Nested
    class UpdateUserById {
        @Test
        @DisplayName("Should update user by id when user exists and username and password is filled")
        void shouldUpdateUserByIdWhenUserExistsAndUsernameAndPasswordIsFilled() {

            var updateUser = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            userService.updateUserById(user.getUserId().toString(), updateUser);

            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
            assertEquals(updateUser.username(), userArgumentCaptor.getValue().getUsername());
            assertEquals(updateUser.password(), userArgumentCaptor.getValue().getPassword());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(userArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should not update user by id when user not exists")
        void shouldNotUpdateUserByIdWhenUserNotExists() {

            var updateUser = new UpdateUserDto(
                    "newUsername",
                    "newPassword"
            );

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "123",
                    Instant.now(),
                    null
            );

            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            userService.updateUserById(user.getUserId().toString(), updateUser);

            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).save(any());
        }
    }
}