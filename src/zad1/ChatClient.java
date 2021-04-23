/**
 * @author Zajkowski Tomasz S18325
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public class ChatClient {

    private InetSocketAddress serverAdress;
    public SocketChannel chanel;
    public String id;
    StringBuffer chatDialog;
    public boolean running = true, isStarving = false;

    public ChatClient(String host, int port, String id) {
        serverAdress = new InetSocketAddress(host, port);
        this.id = id;
        connect();
        chatDialog = new StringBuffer();
    }

    Callable<Integer> clientListener = () -> {
        Thread.currentThread().setName(id + " listener...");
        while (running) {
            if (isStarving) {
                while (!read()) {
                    Thread.sleep(0);
                }
                continue;
            }
            Thread.sleep(0);
        }
        return 1;
    };

    public void login() {
        chatDialog.append("=== " + id + " chat view\n");
        send("HELLO " + id);
    }

    public void logout() throws IOException {
        send("BYE");
    }

    private void errorLog(Exception exe) {
        chatDialog.append("*** " + exe.toString());
    }

    public void send(String req) {
        try {
            req += "\n";
            byte[] bufor = req.getBytes();
            ByteBuffer bufbuf = ByteBuffer.wrap(bufor);
            while (bufbuf.hasRemaining()) {
                chanel.write(bufbuf);
            }
            if (chanel.isOpen()) {
            }
        } catch (IOException ex) {
            errorLog(ex);
        }
        if (!isStarving) {
            while (!read()) {
                int i = 0;
            }
        }
    }

    private boolean read() {
        ByteBuffer inBuff = ByteBuffer.allocateDirect(256);
        try {
            while (chanel.read(inBuff) == 0) {
                return false;
            }
            inBuff.flip();
            byte[] tmp = new byte[inBuff.limit()];
            inBuff.get(tmp);
            String resp = new String(tmp);
            chatDialog.append(resp);
            if (resp.contains(id + ": logged out\n")) {
                chanel.close();
                running = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public String getChatView() {
        return chatDialog.toString();
    }

    private void connect() {
        try {
            chanel = SocketChannel.open();
            chanel.configureBlocking(false);
            if (!chanel.connect(serverAdress)) {
                while (!chanel.finishConnect()) {
                    System.out.println("connecting...");
                }
            }
            while (!chanel.finishConnect()) {
                System.out.println("Connecting....");
            }
        } catch (UnknownHostException he) {
            errorLog(he);
        } catch (IOException ie) {
            errorLog(ie);
        }
    }
}