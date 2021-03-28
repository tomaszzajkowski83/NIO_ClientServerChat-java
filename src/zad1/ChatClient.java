/**
 *
 *  @author Zajkowski Tomasz S18325
 *
 */

package zad1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClient {

    private InetSocketAddress serverAdress;
    private SocketChannel chanel;
    private String id;

    public ChatClient(String host, int port, String id){
        serverAdress = new InetSocketAddress(host, port);
        this.id = id;
    }

    public void login(){
        try {
            //chanel = SocketChannel.open(serverAdress);
            chanel = SocketChannel.open();
            //chanel.configureBlocking(false);
            chanel.connect(serverAdress);
            while(!chanel.finishConnect()){
                System.out.println("Connecting....");
            }
            byte[] bufor = id.getBytes();
            ByteBuffer buf = ByteBuffer.wrap(bufor);
            chanel.write(buf);
        }catch(UnknownHostException he){
            System.out.println("There is no such host. Check the host name you have given");
        }catch (IOException ie){
            System.out.println("Some I/O error occured during Socket creating");
        }
    }

    public void logout(){

    }

    public void send(String req){

    }

    public String getChatView(){
        return new String();
    }
}
