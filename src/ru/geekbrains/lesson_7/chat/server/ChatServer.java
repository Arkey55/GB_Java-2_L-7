package ru.geekbrains.lesson_7.chat.server;

import ru.geekbrains.lesson_7.chat.authentication.AuthenticationService;
import ru.geekbrains.lesson_7.chat.authentication.BasicAuthenticationService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer implements Server {
    private Set<ClientHandler> clients;
    private AuthenticationService authenticationService;
    private Socket socket;
    private ServerSocket serverSocket;

    public ChatServer() {
        try {
            System.out.println("Server is starting up...");
            serverSocket = new ServerSocket(8080);
            clients = new HashSet<>();
            authenticationService = new BasicAuthenticationService();
            System.out.println("Server is up");

            while (true){
                System.out.println("Server is waiting for clients...");
                socket = serverSocket.accept();
                System.out.println("Client connected: " + socket);
                new ClientHandler(this, socket);
            }
        } catch (IOException e){
            throw new RuntimeException("SWW", e);
        }
    }

    @Override
    public synchronized void broadcastMsg(String message) {
        clients.forEach(client -> client.sendMsg(message));
    }

    @Override
    public synchronized void sendPrivateMessage(ClientHandler sender, String nickname, String message){
        ClientHandler c = clients.stream().filter(clientHandler -> clientHandler.getName().equals(nickname)).findFirst().get();
        c.sendMsg("From " + sender.getName() + ": " + message);
    }
    @Override
    public synchronized boolean isLoggedIn(String nickname) {
        return clients.stream().filter(clientHandler -> clientHandler.getName().equals(nickname)).findFirst().isPresent();
    }

    @Override
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    @Override
    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    @Override
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
}
