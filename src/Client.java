import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public void sendMessage() {
        try {
            writer.write(username);
            writer.newLine();
            writer.flush();

            Scanner scan = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scan.nextLine();
                writer.write(username + ": " + messageToSend);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromeGroupChat;

            while (socket.isConnected()) {
                try {
                    msgFromeGroupChat = reader.readLine();
                    System.out.println(msgFromeGroupChat);
                } catch (IOException e) {
                    closeEverything(socket, reader, writer);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufReader, BufferedWriter bufWriter) {
        try {
            if (bufReader != null) {
                bufReader.close();
            }

            if (bufWriter != null) {
                bufWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your username for the group chat : ");
        String username = scan.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}