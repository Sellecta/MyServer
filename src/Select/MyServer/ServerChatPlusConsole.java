package Select.MyServer;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Select on 29.12.2016.
 */
public class ServerChatPlusConsole {

    private DataInputStream MyDataIn;
    private DataOutputStream MyDataOu;
    private Socket mySocket;
    private String id;
    private byte[] buffer;
    private static List<ServerChatPlusConsole> MylistS = Collections.synchronizedList(new ArrayList<ServerChatPlusConsole>());
    private boolean Cheker=true;
    private Long Length;
    private MyStringParser myParser;

    public ServerChatPlusConsole(Socket s){
        try {
            mySocket=s;
            MyDataIn=new DataInputStream(mySocket.getInputStream());
            MyDataOu=new DataOutputStream(mySocket.getOutputStream());
            System.out.println("Socked Crate");
            Messengers();
            MylistS.add(this);
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    public void Messengers(){
        new Thread(()->{
            byte[] tempbuff=new byte[512];
            int Length;
            System.out.println("Started conected");
         while (true){
             try {
               Length= System.in.read(tempbuff);
               if((new String(tempbuff,0,Length-1).equals("//test"))) System.out.println(MylistS.get(0).id);
               else MyBroadcastMessage(tempbuff,Length);
             } catch (IOException e) {
                 e.printStackTrace();
                 break;
             }
         }
            System.out.println("Close conected");
        }).start();
        new Thread(()->{
            try {
                id=MyDataIn.readUTF();

            while (Cheker){
                try {
                Length=MyDataIn.readLong();
                buffer=new byte[Math.toIntExact(Length)];
                MyDataIn.read(buffer,0, Math.toIntExact(Length));
                if(new String((buffer)).contains("//private")) SenderPrivateMassage(id);
                else if(new String(buffer).contains("//sendFiles")) {
                    ResendFiles();
                }
                else{
                String temp=id+": "+new String(buffer);
                MyBroadcastMessage(temp.getBytes(),temp.getBytes().length);
                System.out.print(id+": "+new String(buffer));
                }
            }catch (IOException ex){
                    ex.printStackTrace();
                    try {
                        mySocket.close();
                        MyDataIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                MylistS.remove(this);
            }
        }).start();
    }

    protected  void SenderPrivateMassage(String id){
        myParser=new MyStringParser(buffer);
        synchronized (MylistS){
            Iterator<ServerChatPlusConsole> iter=MylistS.iterator();
            while (iter.hasNext()){
                ServerChatPlusConsole MyBrk=iter.next();
                if(MyBrk.id.equals(myParser.getParserNick(10,' ')) || MyBrk.id.equals(id)){
                    synchronized (MyBrk.MyDataOu){
                        try{
                            MyBrk.MyDataOu.writeLong(myParser.genMessenger(id,10,' ').length());
                            MyBrk.MyDataOu.write(myParser.genMessenger(id,10,' ').getBytes());
                            MyBrk.MyDataOu.flush();
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected void MyBroadcastMessage(byte[] buff, int Length){
        synchronized (MylistS){
            Iterator<ServerChatPlusConsole> iter=MylistS.iterator();
            while (iter.hasNext()){
                ServerChatPlusConsole MyBrk=iter.next();
                synchronized (MyBrk.MyDataOu){
                    try {
                        MyBrk.MyDataOu.writeLong(Length);
                        MyBrk.MyDataOu.write(buff,0,Length);
                        MyBrk.MyDataOu.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                        MyBrk.Cheker=false;
                    }
                }
            }
        }
    }

    protected void ResendFiles(){
        myParser=new MyStringParser(buffer, Math.toIntExact(Length));
        synchronized (MylistS){
            Iterator<ServerChatPlusConsole> iter=MylistS.iterator();
            while (iter.hasNext()){
                ServerChatPlusConsole MyBrk=iter.next();
                if(MyBrk.id.equals(myParser.getParserNick(12,' '))){
                    synchronized (MyBrk.MyDataOu){
                        try {
                            MyBrk.MyDataOu.writeLong("//sendFiles".length());
                            MyBrk.MyDataOu.write("//sendFiles".getBytes());
                            MyBrk.MyDataOu.flush();
                            Length=MyDataIn.readLong();
                            buffer=new byte[Math.toIntExact(Length)];
                            String temp=MyDataIn.readUTF();
                            MyDataIn.readFully(buffer,0, Math.toIntExact(Length));
                            MyBrk.MyDataOu.writeLong(Length);
                            MyBrk.MyDataOu.writeUTF(temp);
                            MyBrk.MyDataOu.write(buffer);
                            MyBrk.MyDataOu.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
