package Select.MyServer;

import java.io.IOException;

/**
 * Created by Select on 27.12.2016.
 */
public class MainController {
    public static void main(String[] args) {
        try {
            new MyServer(4750).Runer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
