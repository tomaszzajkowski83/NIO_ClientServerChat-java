/**
 * @author Zajkowski Tomasz S18325
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    InetSocketAddress serverSocketAdress;
    ServerSocketChannel serverChannel;
    StringBuilder log;
    ExecutorService exec;
    Selector selector;
    String timePattern = "HH:MM:SS.nnn";
    DateTimeFormatter formater;
    LocalTime time;
    Set keys;

    public ChatServer(String host, int port) {
        serverSocketAdress = new InetSocketAddress(host, port);
        formater = DateTimeFormatter.ofPattern(timePattern);
        //buf = ByteBuffer.allocateDirect(1024);
    }

    Runnable serAct = () -> {
        System.out.println("Server started");
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(serverSocketAdress);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            SelectionKey servKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            log = new StringBuilder();
            for (; ; ) {
                while (serverChannel.isOpen()) {
                    selector.select();
                    SelectionKey key;
                    keys = selector.selectedKeys();
                    Iterator iter = keys.iterator();
                    while (iter.hasNext()) {
                        key = (SelectionKey) iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {

                            //SocketChannel sc = serverChannel.accept();
                            //sc.configureBlocking(false);
                            //sc.register(selector, SelectionKey.OP_READ /*| SelectionKey.OP_WRITE*/);
                            //continue;

                            if (key.channel().isOpen()) {
                                SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
                                String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":").append(sc.socket().getPort()).toString();
                                sc.configureBlocking(false);
                                sc.register(selector, SelectionKey.OP_READ, address);
                            }
                            //ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome to NioChat!\n".getBytes());
                            //sc.write(welcomeBuf);
                            //welcomeBuf.rewind();
                            //System.out.println("accepted connection from: " /* + address ((ServerSocketChannel)key.channel()).accept().getRemoteAddress()*/);
                        }
                        if (key.isReadable()) {
                            /*
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            log.setLength(0);
                            byteBuffer.clear();
                            readLoop:
                            while (true) {
                                int n = clientChannel.read(byteBuffer);
                                if (n > 0) {
                                    byteBuffer.flip();
                                    CharBuffer cbuf = charset.decode(byteBuffer);
                                    while (cbuf.hasRemaining()) {
                                        char c = cbuf.get();
                                        if (c == '\r' || c == '\n') break readLoop;
                                        log.append(c);
                                    }
                                }
                            }
                            String[] req = log.toString().split(" ");
                            String cmd = req[0];

                            if (cmd.equals("HELLO")) {
                                message = req[1] + " logged in";
                                sb.append(message + "\n");
                            }
                            continue;
                            */
                            ByteBuffer buf = ByteBuffer.allocateDirect(1024);
                            SocketChannel ch = (SocketChannel) key.channel();
                            StringBuffer sb = new StringBuffer();

                            buf.clear();
                            int read = 0;
                            while ((read = ch.read(buf)) > 0) {
                                buf.flip();
                                byte[] bytes = new byte[buf.limit()];
                                buf.get(bytes);
                                sb.append(new String(bytes));
                                buf.clear();
                            }
                            time = LocalTime.now();
                            String msg = sb.toString();
                            sb.setLength(0);
                            if (read < 0) {
                                msg = key.attachment() + " left the chat.\n";
                                ch.close();
                            } else {
                                if (msg.startsWith("HELLO")) {
                                    String[] tmp = msg.split(" ");
                                    String id = tmp[1];
                                    key.attach(id);
                                    msg = "logged in";
                                }else if(msg.startsWith("BYE")){
                                    ((SocketChannel) key.channel()).finishConnect();
                                    msg = "logged out";
                                }
                            }
                            sb.trimToSize();
                            log.append(String.valueOf(time) +" "+ key.attachment()+": " +msg+ "\n");
                            broadcast(msg);
                        }
                        if (key.isWritable()) {
                            /*
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            byteBuffer = charset.encode(CharBuffer.wrap(message));
                            clientChannel.write(byteBuffer);
                            continue;
                            */
                            //System.out.println("MOżna pisać do kanału "/*+key.attachment().getClass().getName()*/);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    };

    private void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }

    public void startServer() {
        exec = Executors.newSingleThreadExecutor();
        exec.submit(serAct);
    }

    public void stopServer() {
        while (!keys.isEmpty()){
            System.out.println("Oprużniam kanaly");
        }
        exec.shutdownNow();
        System.out.println("Server stopped");
    }

    public String getServerLog() {
        //System.out.println(keys.isEmpty());
        return log.toString();
        //return new String();
    }

    private String getTime(long milis) {
        int ms = (int) milis % 1000;
        int s = ms / 10 % 60;
        int min = s % 60;
        //log.append(ms);
        return log.toString();
    }

}
