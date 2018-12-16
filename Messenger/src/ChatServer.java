import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer("This is second constructor");
    }

    private List<TCPConnection> connections = new ArrayList<>();
    private static ChatServer chatServer;
    private String[] getSplitText;

    public List<TCPConnection> getConnections() {
        return connections;
    }

    private ChatServer() {
    }

    private ChatServer(String str) {
        System.out.println("Server running.........");
        try (ServerSocket serverSocket = new ServerSocket(8130)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChatServer getChatServer() {
        if (chatServer == null) {
            chatServer = new ChatServer();
        }
        return chatServer;
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected : " + tcpConnection);
    }


    @Override
    public synchronized void onRecieveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected : " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP error " + e);

    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            connections.get(i).sendString(value);
            if (splitter(value).equalsIgnoreCase("count")) {
                connections.get(i).sendString("Кол-во юзеров : " + getConnections().size());
            }
        }
    }

    private  String splitter(String str) {
        int length = 0;
        if (str.length() > 5) {
            getSplitText = str.split(":");
            if (getSplitText.length >= 2) {
                length = 1;
            } else {
                length = 0;
            }
        }
        return getSplitText[length];
    }


}
