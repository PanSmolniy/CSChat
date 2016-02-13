import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by PC on 15.01.2016.
 */
public class Main
{
    static BufferedReader r;

    public static void main(String[] args) throws IOException
    {
        r = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Выберите режим работы программы:\ns - Server \nc - Client");
        char line;
        while (true) {

            line  = r.readLine().toLowerCase().charAt(0);
            if (line == 's')
            {
                new Server();
                break;

            } else if (line == 'c')
            {
                new Client();
                break;

            } else {
                System.out.println("Неверный ввод. Повторите");
            }
        }
    }
}
