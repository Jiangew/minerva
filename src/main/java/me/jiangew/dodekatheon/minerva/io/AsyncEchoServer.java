package me.jiangew.dodekatheon.minerva.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;

/**
 * "异步I/O"模式
 * <p>
 * Java SE 7 版本之后，引入了异步 I/O（NIO.2） 的支持，为构建高性能的网络应用提供了一个利器。
 * <p>
 * Author: Jiangew
 * Date: 12/11/2016
 */
public class AsyncEchoServer {
    private static final int DEFAULT_PORT = 7;

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
        }

        ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        // create asynchronous server socket channel bound to the default group
        try (AsynchronousServerSocketChannel asyncServerSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (asyncServerSocketChannel.isOpen()) {
                // set some options
                asyncServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asyncServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                // bind the server socket channel to local address
                asyncServerSocketChannel.bind(new InetSocketAddress(port));
                // display a waiting message while ... waiting clients
                System.out.println("Waiting for connections ...");

                while (true) {
                    Future<AsynchronousSocketChannel> asyncSocketChannelFuture = asyncServerSocketChannel.accept();
                    try {
                        final AsynchronousSocketChannel asyncSocketChannel = asyncSocketChannelFuture.get();
                        Callable<String> worker = () -> {
                            String host = asyncSocketChannel.getRemoteAddress().toString();
                            System.out.println("Incoming connection from: " + host);

                            final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                            // transmitting data
                            while (asyncSocketChannel.read(buffer).get() != -1) {
                                buffer.flip();
                                asyncSocketChannel.write(buffer).get();
                                if (buffer.hasRemaining()) {
                                    buffer.compact();
                                } else {
                                    buffer.clear();
                                }
                            }
                            asyncSocketChannel.close();
                            System.out.println(host + "was successfully served!");
                            return host;
                        };
                        taskExecutor.submit(worker);
                    } catch (InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                        System.err.println("\n Server is shutting down ...");
                        // this will make the executor accept no new threads
                        // and finish all existing threads in the queue
                        taskExecutor.shutdown();
                        // wait until all threads are finished
                        while (!taskExecutor.isTerminated()) {
                            // nothing to do
                        }
                        break;
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
