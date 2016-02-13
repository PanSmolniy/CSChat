import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 16.01.2016.
 */
public class Message implements Serializable
{
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("[dd.MM.yyyy hh:mm:ss]");

    String string;

    public Message(String string, String userName)
    {
        date = new Date();
        String da =  sdf.format(date);
        this.string = da + userName + ": " + string;
    }

    @Override
    public String toString() {
        return string;
    }
}
