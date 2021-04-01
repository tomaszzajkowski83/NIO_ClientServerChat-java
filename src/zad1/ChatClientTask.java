/**
 * @author Zajkowski Tomasz S18325
 */

package zad1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ChatClientTask implements Runnable {
    private ChatClient client;
    private List<String> messages;
    private int delay;
    private Future task;
    ExecutorService exec;

    private ChatClientTask(ChatClient c, List<String> msg, int wait) {
        client = c;
        messages = msg;
        delay = wait;
        exec = Executors.newSingleThreadExecutor();
        task = exec.submit(() -> {
            while (true) {
            }
        });
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
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            Thread.currentThread().interrupt();
            task.cancel(true);
        }
    }

    public ChatClient getClient() {
        return client;
    }

    public void get() throws InterruptedException, ExecutionException {
        //System.out.println(Thread.currentThread().getName() + " .....czy zako nczony?");
        while (!task.isDone()) {
            //System.out.println("Calculating...");
            Thread.sleep(300);
        }
        //System.out.println("Klient zakonczył dzialanie....");
        exec.shutdownNow();
    }
}
