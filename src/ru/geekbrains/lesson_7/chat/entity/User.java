package ru.geekbrains.lesson_7.chat.entity;

public class User {
    private String nick;
    private String email;
    private String password;

    public User(String nick, String email, String password) {
        this.nick = nick;
        this.email = email;
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
