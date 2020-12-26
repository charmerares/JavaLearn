package com.iris.nioDemo.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioClient {
    public void start(){
        try {
            SocketChannel socketChannel=SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),8090));
            socketChannel.configureBlocking(false);

            Selector selector=Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);

            while (selector.select()>0){
                Iterator<SelectionKey> selectionKeyIterator=selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()){
                    SelectionKey key=selectionKeyIterator.next();
                    selectionKeyIterator.remove();

                    if(key.isReadable()){
                        //here we don't concern about the read
                    }

                    if(key.isWritable()){
                        SocketChannel channel=(SocketChannel)key.channel();

                        ByteBuffer buffer=ByteBuffer.allocate(1024);
                        buffer.clear();
                        buffer.put("hello server".getBytes(StandardCharsets.UTF_8));
                        buffer.flip();
                        while(buffer.hasRemaining()){
                            channel.write(buffer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
