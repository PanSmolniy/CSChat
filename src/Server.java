import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by PC on 15.01.2016.
 *
 *
 * Отлавливать дисконект
 * Баг. Есть ячейка, но сервер видит ее только после повторного подключения.
 */
public class Server
{
    ArrayList<Connection> connections = new ArrayList<>();
    int port = 1488;
    ServerSocket ss;
    boolean hasSlots = true;


    final int messageAmount = 10;

    BlockingQueue<Message> messageList = new ArrayBlockingQueue<>(messageAmount);

    final int maxUsersConnected = 3;

    public Server()
    {
        System.out.println("Это я, твой новый сервер");
        System.out.println("Ждем подключений");

        try {
            ss = new ServerSocket(port);


            while (true)
            {
                if (hasSlots) {
                    Socket socket = ss.accept();
                    System.out.println("Ура! Какой-то хуедрыга подключился!");

                    Connection con = new Connection(socket);
                    connections.add(con);
                    if (connections.size() == maxUsersConnected) hasSlots = false;
                    con.start();
                } else
                {
                    Socket socket = ss.accept();
                    System.out.println("Ура! Какой-то *** подключился, но к сожалению у нас нет для него места :(");

                    Connection con = new Connection(socket);


                    con.out.writeUTF("Извените, на сервере много народу");

                    con.in.readUTF();

                    con.socket.close();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends Thread
    {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        ObjectInputStream oin;
        ObjectOutput oout;

        String clientName = "";
        Message message;

        public Connection(Socket socket)
        {
            this.socket = socket;

            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                oin = new ObjectInputStream(socket.getInputStream());
                oout = new ObjectOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run()
        {
            try {
                clientName = in.readUTF();
                out.writeUTF("Добро пожаловать на сервер " + clientName);
                out.writeUTF(buildString(messageList));

                for(Connection c : connections)
                {
                    message = new Message("Пользователь " + this.clientName + " присоединился к беседе");
                    c.oout.writeObject(message);
                }

                while (true)
                {
                    try {
                        message = (Message) oin.readObject();
                        if (messageList.size() == 10)
                        {
                            messageList.poll();
                            messageList.add(message);
                        } else messageList.add(message);
                    }

                    catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (SocketException se) {
                        connections.remove(this);
                        hasSlots = true;
                        /*
                        Требуется подключиться 2 раза, чтобы появилось свободное место
                         */
                        System.out.println("Соединение разорвано. Пользователь " + this.clientName + " покидает беседу. " + connections.size());
                        message = new Message("Пользователь " + this.clientName + " покинул беседу.");
                        for(Connection c : connections)
                        {
                            c.oout.writeObject(message);
                        }
                        break;
                    }

                    for(Connection c : connections)
                    {
                        c.oout.writeObject(message);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String buildString(BlockingQueue<Message> queue)
    {
        StringBuilder sb = new StringBuilder();
        for (Message s : queue)
        {
            sb.append(s + "\n");
        }
        return sb.toString();
    }

}
