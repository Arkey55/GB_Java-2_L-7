package ru.geekbrains.lesson_7.chat.authentication;

import ru.geekbrains.lesson_7.chat.entity.User;

import java.util.List;
import java.util.Optional;

public class BasicAuthenticationService implements AuthenticationService {
    private final static List<User> users;

    static {
        users = List.of(
                (new User("u1", "e1", "p1")),
                (new User("u2", "e2", "p2")),
                (new User("u3", "e3", "p3"))
        );
    }
    @Override
    public Optional<User> doAuthentication(String email, String password) {
        for (User user : users){
            if (user.getEmail().equals(email) && user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
