package ru.geekbrains.lesson_7.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            listenForClientMsg();

        } catch (IOException e){
            throw new RuntimeException("SWW", e);
        }
    }

    private void doAuth() throws SocketException {
        socket.setSoTimeout(120000);
        try {
            while (true){
                String str = in.readUTF();
                if (str.startsWith("-auth")){
                    String[] parts = str.split("\\s");
                    server.getAuthenticationService().doAuthentication(parts[1], parts[2]).ifPresentOrElse(
                            user -> {
                                if (!server.isLoggedIn(user.getNick())){
                                    sendMsg("Login successful");
                                    name = user.getNick();
                                    server.broadcastMsg(name + " is logged in");
                                    server.subscribe(this);
                                } else {
                                    sendMsg("Authentication error. Account already logged in.");
                                }
                            },
                            () -> sendMsg("Wrong login or password")
                        );
                }
                return;
            }
        } catch (IOException e){
            throw new RuntimeException("SWW", e);
        }
    }



    private void listenForClientMsg(){
        new Thread(()->{
            try {
                doAuth();
            } catch (SocketException e) {
                System.out.println("time"); //не работает =(
            }
            try {
                receiveMsg();
            } catch (Exception ex){
                throw new RuntimeException("SWW", ex);
            } finally {
                server.unsubscribe(this);
            }
        }).start();
    }

    private void receiveMsg(){
        try {
            while (true){
                StringBuilder msg = new StringBuilder(in.readUTF());
                if (msg.toString().equals("-exit")){
                    break;
                }
                if (msg.toString().contains("/w")){
                    String[] parts = msg.toString().split("\\s");
                    String nickname = parts[1];
                    msg = new StringBuilder();
                    for (int i = 0; i < parts.length; i++) {
                        if (i > 1){
                            msg.append(" ").append(parts[i]);
                        }
                    }
                    server.sendPrivateMessage(this, nickname, msg.toString());
                } else {
                    server.broadcastMsg(name + ": " + msg.toString());
                }
            }
        } catch (IOException e){
            throw new RuntimeException("SWW", e);
        }
    }
    public void sendMsg(String message){
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException("SWW", e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(server, that.server) &&
                Objects.equals(socket, that.socket) &&
                Objects.equals(in, that.in) &&
                Objects.equals(out, that.out) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, socket, in, out, name);
    }
}
