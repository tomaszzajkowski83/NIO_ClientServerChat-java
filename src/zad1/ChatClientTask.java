/**
 * @author Zajkowski Tomasz S18325
 */

package zad1;

import java.util.List;

public class ChatClientTask implements Runnable {
    private ChatClient client;
    private List<String> messages;
    private int delay;

    private ChatClientTask(ChatClient c, List<String> msg, int wait) {
        client = c;
        messages = msg;
        delay = wait;
    }

    public static ChatClientTask create(ChatClient c, List<String> msg, int wait) {
        return new ChatClientTask(c, msg, wait);
    }

    @Override
    public void run() {
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
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    public ChatClient getClient(){
        return client;
    }
}
