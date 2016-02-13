import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by PC on 15.01.2016.
 */
public class Client extends Thread
{

    String ip = "127.0.0.1";
    int port = 1488;
    String name = "";
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    DataInputStream in;
    DataOutputStream out;

    ObjectInputStream oin;
    ObjectOutputStream oout;

    Message message;


    public Client()
    {
        System.out.println("Введите ваше имя");
        try {
            name = r.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    @Override
    public void run()
    {
        try
        {
            InetAddress address = InetAddress.getByName(ip);

            Socket socket = new Socket(address, port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());


                out.writeUTF(name);

                System.out.println(in.readUTF());
                System.out.println(in.readUTF());


                Listener listener = new Listener();
                Thread listenerThread = new Thread(listener);
                listenerThread.start();

                Writer writer = new Writer();
                Thread writerThread = new Thread(writer);
                writerThread.start();


        } catch (IOException e)
        {

        }
    }

    private class Listener extends Thread
    {

        @Override
        public void run()
        {
            while (true)
            {
                try {
                     message = (Message) oin.readObject();
                    System.out.println(message);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException ioe)
                {

                }
            }
        }
    }

    private class Writer extends Thread
    {
        @Override
        public void run() {
            String line = "";

            while (true)
            {
                try
                {
                    line = r.readLine();
                    Message message = new Message(line, name);
                    oout.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
