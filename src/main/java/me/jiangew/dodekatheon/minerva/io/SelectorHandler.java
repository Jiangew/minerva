package me.jiangew.dodekatheon.minerva.io;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 打开一个Selector，注册一个通道注册到这个Selector上(通道的初始化过程略去)，然后持续监控这个Selector的四种事件（接受，连接，读，写）是否就绪。
 * <p>
 * Author: Jiangew
 * Date: 21/02/2017
 */
public class SelectorHandler {

    public static void main(String[] args) {
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_READ);

            while (true) {
                int readChannels = selector.select();
                if (readChannels == 0) continue;

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        // a connection was accepted by a ServerSocketChannel
                    } else if (key.isConnectable()) {
                        // a connection was established with a remote server
                    } else if (key.isReadable()) {
                        // a channel is ready for reading
                    } else if (key.isWritable()) {
                        // a channel is ready for writing
                    } else {
                        // do nothing
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
