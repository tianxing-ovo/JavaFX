package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tianxing
 */
public class Server {
    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(16, 32, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(16));

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9001)) {
            while (true) {
                poolExecutor.execute(new ServerHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
