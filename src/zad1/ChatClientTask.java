/**
 * @author Zajkowski Tomasz S18325
 */

package zad1;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class ChatClientTask extends FutureTask {
    private ChatClient client;
    private List<String> messages;
    private int delay;

    private ChatClientTask(ChatClient c, List<String> msg, int wait) {
        super(c.clientListener);
        if (wait == 0) {
            c.isStarving = true;
        }
        client = c;
        messages = msg;
        delay = wait;
        task.start();
    }

    public static ChatClientTask create(ChatClient c, List<String> msg, int wait) {
        return new ChatClientTask(c, msg, wait);
    }

    Thread task = new Thread(() -> {
        Thread.currentThread().setName(client.id);
        try {
            client.login();
            if (delay != 0)
                Thread.sleep(delay);
            for (String mes : messages) {
                client.send(mes);
                if (delay != 0)
                    Thread.sleep(delay);
            }
            client.logout();
            if (delay != 0)
                Thread.sleep(delay);
            client.send("FIN");
        } catch (InterruptedException ex) {
            System.out.println(ex);
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
        }
    });

    public ChatClient getClient() {
        return client;
    }

    private boolean clientIsOpen() {
        return client.chanel.isOpen();
    }
}
