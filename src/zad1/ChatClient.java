/**
 *
 *  @author Zajkowski Tomasz S18325
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClient {

    private InetSocketAddress serverAdress;
    private SocketChannel chanel;
    private String id;


    public ChatClient(String host, int port, String id){
        serverAdress = new InetSocketAddress(host, port);
        this.id = id;
        connect();
    }

    public void login(){
        send("HELLO "+id);
        //send("\n");
        /*
        try {
            chanel = SocketChannel.open();
            chanel.connect(serverAdress);
            while(!chanel.finishConnect()){
                System.out.println("Connecting....");
            }
            //send("HELLO "+id);
            //send("\n");

        }catch(UnknownHostException he){
            System.out.println("There is no such host. Check the host name you have given");
        }catch (IOException ie){
            System.out.println("Some I/O error occured during Socket creating");
        }
        */
    }

    public void logout() throws IOException{
        send("BYE");
        chanel.finishConnect();
    }

    public void send(String req){
        try {
            byte[] bufor = req.getBytes();
            ByteBuffer buf = ByteBuffer.wrap(bufor);
            chanel.write(buf);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String getChatView(){
        return new String();
    }
    private void connect(){
        try {
            chanel = SocketChannel.open();
            chanel.connect(serverAdress);
            //while(!chanel.finishConnect()){
                //System.out.println("Connecting....");
            //}
        }catch(UnknownHostException he){
            System.out.println("There is no such host. Check the host name you have given");
        }catch (IOException ie){
            System.out.println("Some I/O error occured during Socket creating");
        }
    }
}
