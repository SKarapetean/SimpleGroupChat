import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket server;

    public Server(ServerSocket server) {
        this.server = server;
    }

    public void startServer() {

        try {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void closeServerSocket() {
        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}