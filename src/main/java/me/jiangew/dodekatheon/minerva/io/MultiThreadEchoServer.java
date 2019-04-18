package me.jiangew.dodekatheon.minerva.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * "阻塞I/O + 多线程"模式
 * <p>
 * 多线程来支持多个客户端来访问服务器。
 * <p>
 * 存在问题：每次接收到新的连接都要新建一个线程，处理完成后销毁线程，代价大。当有大量地短连接出现时，性能比较低。
 * <p>
 * Author: Jiangew
 * Date: 11/11/2016
 */
public class MultiThreadEchoServer {
    private static final int DEFAULT_PORT = 7;

    public static void main(String[] args) throws IOException {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException e) {
            port = DEFAULT_PORT;
        }

        Socket clientSocket = null;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                clientSocket = serverSocket.accept();

                // multi thread
                new Thread(new EchoServerHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}