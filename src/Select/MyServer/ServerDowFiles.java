package Select.MyServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Select on 27.12.2016.
 */
public class ServerDowFiles {
    static private DataInputStream dataStream;
    private Long lenght;
    private byte[] buffer;
    private String name;

    public ServerDowFiles(Socket s){
        try {

            dataStream=new DataInputStream(s.getInputStream());
            lenght=dataStream.readLong();
            synchronized (dataStream){
            name=dataStream.readUTF();
            System.out.println(lenght+" " + name);
            System.out.println("downloading file "+name+" file size "+getSize(lenght)+" Mbs");
            buffer=new byte[Math.toIntExact(lenght)];
            dataStream.readFully(buffer,0, Math.toIntExact(lenght));
            FileOutputStream fl= new FileOutputStream(name);
            fl.write(buffer);
            fl.flush();
            fl.close();
            System.out.println("downloading end");
        }} catch (IOException e) {
           e.printStackTrace();
        }
    }

    public double getSize(long temp){
        double Mbts=1024*1024;
        double wtemp=Math.round((temp/Mbts)*100);
        return wtemp/100;
    }


}
