package ru.geekbrains.lesson_7.chat.authentication;

import ru.geekbrains.lesson_7.chat.entity.User;

import java.util.Optional;

public interface AuthenticationService {
    Optional<User> doAuthentication(String login, String password);

}
