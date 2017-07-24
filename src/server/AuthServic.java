package server;


public interface AuthServic {
    void start();
    String getNickLoginPass(String login, String pass);
    void stop();
}
