package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    InetSocketAddress serverSocketAdress;
    ServerSocketChannel serverChannel;
    StringBuilder log;
    ExecutorService servExec;
    Selector selector;
    String timePattern = "HH:MM:SS.nnn";
    DateTimeFormatter formater;
    LocalTime time;
    Set<SelectionKey> keys;
    String toClose = "";
    List<String> replies;
    private static Charset charset = Charset.forName("UTF-8");

    public ChatServer(String host, int port) {
        serverSocketAdress = new InetSocketAddress(host, port);
        formater = DateTimeFormatter.ofPattern(timePattern);
        servExec = Executors.newSingleThreadExecutor();
        replies = new ArrayList<>();
    }

    Runnable serAct = () -> {
        System.out.println("Server started");
        Thread.currentThread().setName("Server..");
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(serverSocketAdress);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            log = new StringBuilder();
            while (serverChannel.isOpen()) {
                selector.selectNow();
                SelectionKey key;
                keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        SocketChannel cc = serverChannel.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        continue;
                    }
                    if (key.isReadable()) {
                        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
                        SocketChannel ch = (SocketChannel) key.channel();
                        StringBuffer sb = new StringBuffer();

                        buf.clear();
                        int read = 0;

                        sb.setLength(0);
                        while ((read = ch.read(buf)) > 0) {
                            buf.flip();
                            byte[] bytes = new byte[buf.limit()];
                            buf.get(bytes);
                            sb.append(new String(bytes));
                            buf.clear();
                        }
                        for (String msg : sb.toString().split("\n")) {

                            time = LocalTime.now();                            if (read < 0) {
                                msg = key.attachment() + " left the chat.\n";
                                ch.close();
                            } else {
                                if (msg.startsWith("HELLO")) {
                                    String[] tmp = msg.split(" ");
                                    String id = tmp[1];
                                    key.attach(id);
                                    msg = "logged in";
                                }
                                else if (msg.contains("BYE")) {
                                    msg = key.attachment() + ": logged out";
                                    log.append(time + " " + msg + "\n");
                                    toClose = key.attachment().toString();
                                    broadcast(msg + "\n");

                                    continue;
                                } else if (msg.contains("FIN")) {
                                    continue;
                                }
                            }
                            log.append(time + " " + key.attachment() + ": " + msg + "\n");
                            broadcast(key.attachment() + ": " + msg + "\n");
                            continue;
                        }
                        continue;
                    }
                    if (key.isWritable()) {
                        continue;
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
            if (key.isValid() && key.isWritable()) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                if (key.attachment()!=null && key.attachment().toString().equals(toClose)) {
                    key.cancel();
                }
                msgBuf.rewind();
            }
        }
    }

    public void startServer() {
        servExec.submit(serAct);
    }

    public void stopServer() {
        try {
            serverChannel.close();
        } catch (IOException e) {
            System.out.println("Zamkniety kanal servera");
            e.printStackTrace();
        }
        servExec.shutdownNow();
        System.out.println("Server stopped");
    }

    public String getServerLog() {
        return log.toString();
    }
}
