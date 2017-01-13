package Select.MyServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Select on 27.12.2016.
 */
public class MyServer {
    private Socket MySocket;
    private ServerSocket MyServerSocket;
    private int port;
   static private DataInputStream dataInp;
    private List<InetAddress> MyListInetAddress;
    private List<Socket> MyListSocket;

    public MyServer(int port) throws IOException {
        try{
            MyListInetAddress = new ArrayList<>();
            MyListSocket = new ArrayList<>();
            this.port=port;
            MyServerSocket=new ServerSocket(this.port);


        }
        catch (IOException ex){
            ex.printStackTrace();
            MyServerSocket.close();
    }}

   synchronized public void Runer() throws  IOException{
        System.out.println("Server started...");
        try {
            while (true) {
                MySocket = MyServerSocket.accept();
                System.out.println("Accepted from " + MySocket.getInetAddress() + " and Port " + MySocket.getPort());
                MyListInetAddress.add(MyServerSocket.getInetAddress());
                MyListSocket.add(MySocket);
                String Chek;
                dataInp = new DataInputStream(MySocket.getInputStream());
                Chek = dataInp.readUTF();
                new Thread(()->{
                    try {
                        switch (Chek) {
                            case "chat":
                                new ServerChatPlusConsole(MySocket);
                                break;
                            case "DwFiles":
                                new ServerDowFiles(MySocket);
                                break;
                            default:
                                break;
                         }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }} catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            MySocket.close();
        }
    }
}
