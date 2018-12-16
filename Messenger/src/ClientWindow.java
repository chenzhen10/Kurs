import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;



public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP = "134.17.151.34";
    private static final int PORT = 8130;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());// По сколько у свинга жесткие ограничение то мы делаем асинхронный поток чтобы обновить наш GUI
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Tim");
    private final JTextField fieldInput = new JTextField();
    private final JScrollPane scroll = new JScrollPane();
    private TCPConnection connection;



    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        //Работа с TextArea
        log.setEditable(false);
        log.setLineWrap(true);
        add(log);
        //Добавление скроллбара
        scroll.setViewportView(log);
        add(scroll);

        fieldInput.addActionListener(ClientWindow.this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            connection = new TCPConnection(this, IP, PORT);
        } catch (IOException e) {
            printMessage("Connection exception " + e);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        connection.sendString(fieldNickname.getText() + ":" + msg);
        if(msg.equalsIgnoreCase("/quit")) {System.exit(0);}
        if(msg.equalsIgnoreCase("/cls")) {log.setText(null);}
        fieldInput.setText(null);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready ... If u want to leave this chat /quit");
    }

    @Override
    public void onRecieveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }


    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close....");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception " + e);

    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());//Установка каретки в нужном положении
        });
    }

}
