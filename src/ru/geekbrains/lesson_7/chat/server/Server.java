package ru.geekbrains.lesson_7.chat.server;

import ru.geekbrains.lesson_7.chat.authentication.AuthenticationService;

public interface Server {
    void broadcastMsg(String message);
    void sendPrivateMessage(ClientHandler sender, String nickname, String message);
    boolean isLoggedIn(String nickname);
    void subscribe(ClientHandler client);
    void  unsubscribe(ClientHandler client);
    AuthenticationService getAuthenticationService();
}
