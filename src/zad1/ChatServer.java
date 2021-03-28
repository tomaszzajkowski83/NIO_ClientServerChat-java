/**
 *
 *  @author Zajkowski Tomasz S18325
 *
 */

package zad1;

import java.net.InetSocketAddress;

public class ChatServer {

    //private String host;
    //private int port;
    InetSocketAddress serverSocket;

    public ChatServer(String host, int port){
        //this.host = host;
        //this.port = port;
        serverSocket = new InetSocketAddress(host, port);
    }

    public void startServer(){

    }

    public void stopServer(){

    }

    String getServerLog(){
        return new String();
    }
}
