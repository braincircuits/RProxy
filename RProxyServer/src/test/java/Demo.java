import java.io.IOException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Demo {
    public static void main(String[] args) throws IOException {
        ConcurrentLinkedQueue<Object> clientChannel = new ConcurrentLinkedQueue<>();
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        clientChannel.add(3);
        System.out.println(clientChannel.poll());
        System.out.println(clientChannel.poll());

    }
}
