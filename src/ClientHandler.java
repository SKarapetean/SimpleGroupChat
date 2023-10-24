import java.util.ArrayList;
import java.net.Socket;
import java.io.*;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = reader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + "has entered the chat!");

        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }


    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = reader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, reader, writer);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.writer.write(messageToSend);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                }

            } catch (IOException e) {
                closeEverything(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER:" + clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufReader, BufferedWriter bufWriter) {
        removeClientHandler();
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

}