package server;

public class ServerMain {
    public static void main(String[] args) {
        new AioServer(8090).start();
    }
}
