package zad1;

import java.util.List;
import java.util.concurrent.*;

public class ChatClientTask extends FutureTask<String> {
    private ChatClient client;

    private ChatClientTask(ChatClient c, Callable<String> callable) {
        super(callable);
        client = c;
    }

    public static  ChatClientTask create(ChatClient c, List<String> msg, int wait) {
        Callable<String> task = () -> {
            Thread.currentThread().setName(c.id);
            try {
                c.login();
                if (wait != 0)
                    Thread.sleep(wait);
                for (String mes : msg) {
                    if(Thread.interrupted()){
                        return c.id + " task interrupted";
                    }
                    c.send(mes);
                    if (wait != 0)
                        Thread.sleep(wait);
                }
                c.logout();
                if (wait != 0)
                    Thread.sleep(wait);
            } catch (InterruptedException ex) {
                return c.id + " task interrupted";
            }
            return c.id + " task completed";
        };
        return new ChatClientTask(c, task);
    }

    public ChatClient getClient() {
        return client;
    }

}
