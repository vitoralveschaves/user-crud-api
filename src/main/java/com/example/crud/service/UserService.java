package com.example.crud.service;

import com.example.crud.controller.CreateUserDto;
import com.example.crud.controller.UpdateUserDto;
import com.example.crud.entity.User;
import com.example.crud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID createUser(CreateUserDto createUserDto) {

        var entity = new User(
                UUID.randomUUID(),
                createUserDto.username(),
                createUserDto.email(),
                createUserDto.password(),
                Instant.now(),
                null
        );
        var userSaved = this.userRepository.save(entity);
        return userSaved.getUserId();

    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUserById(String userId) {
        return this.userRepository.findById(UUID.fromString(userId));
    }

    public void updateUserById(String userId, UpdateUserDto updateUserDto) {
        var id = UUID.fromString(userId);
        var userEntity = this.userRepository.findById(id);

        if(userEntity.isPresent()) {
            var user = userEntity.get();
            if(updateUserDto.username() != null) {
                user.setUsername(updateUserDto.username());
            }
            if(updateUserDto.password() != null) {
                user.setPassword(updateUserDto.password());
            }

            this.userRepository.save(user);
        }
    }

    public void deleteById(String userId) {
        var id = UUID.fromString(userId);

        var userExists = this.userRepository.existsById(id);
        if(userExists) {
            this.userRepository.deleteById(id);
        }
    }
}
