/**
 *
 *  @author Zajkowski Tomasz S18325
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {

    InetSocketAddress serverSocketAdress;
    ServerSocketChannel serverChannel;
    ByteBuffer byteBuffer;
    StringBuffer log;
    StringBuilder sb;
    String message;
    private Charset charset  = Charset.forName("ISO-8859-2");


    public ChatServer(String host, int port){
        //this.host = host;
        //this.port = port;
        serverSocketAdress = new InetSocketAddress(host, port);
    }

    public void startServer() {
        Runnable serAct = new Runnable() {
            @Override
            public void run() {
                try {
                    serverChannel = ServerSocketChannel.open();
                    serverChannel.socket().bind(serverSocketAdress);
                    byteBuffer = ByteBuffer.allocateDirect(1024);
                    log = new StringBuffer();
                    sb = new StringBuilder();
                    serverChannel.configureBlocking(false);
                    Selector selector = Selector.open();
                    SelectionKey servlKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                    for (; ; ) {
                        selector.select();
                        Set keys = selector.selectedKeys();
                        Iterator iter = keys.iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = (SelectionKey) iter.next();
                            iter.remove();
                            if (key.isAcceptable()) {
                                SocketChannel clientChannel = serverChannel.accept();
                                clientChannel.configureBlocking(false);
                                clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                continue;
                            }
                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                log.setLength(0);
                                byteBuffer.clear();
                                readLoop:
                                while (true) {
                                    int n = clientChannel.read(byteBuffer);
                                    if (n > 0) {
                                        byteBuffer.flip();
                                        CharBuffer cbuf = charset.decode(byteBuffer);
                                        while(cbuf.hasRemaining()) {
                                            char c = cbuf.get();
                                            if (c == '\r' || c == '\n') break readLoop;
                                            log.append(c);
                                        }
                                    }
                                }
                                String[] req = log.toString().split(" ");
                                String cmd = req[0];

                                if (cmd.equals("HELLO")) {
                                    message = req[1]+" logged in";
                                    sb.append(message+"\n");
                                }
                                continue;
                            }
                            if (key.isWritable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                byteBuffer = charset.encode(CharBuffer.wrap(message));
                                clientChannel.write(byteBuffer);
                                continue;
                            }
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


        };

        Thread serv = new Thread(serAct);
        serv.start();
    }

    public void stopServer(){

    }

    String getServerLog(){
        return sb.toString();
    }

}
