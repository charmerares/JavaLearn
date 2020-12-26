package com.iris.nioDemo.server;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioServer {
    private int port;

    public NioServer(int port) {
        this.port = port;
    }

    public void start(){
        try {
            // first we initialize the socket config
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket=serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));

            // then we initialize the selector
            Selector serverSelector=Selector.open();
            serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

            //let's see what event happened
            while (serverSelector.select()>0){
                Iterator<SelectionKey> eventKeys = serverSelector.selectedKeys().iterator();
                while(eventKeys.hasNext()){
                    //we don't want to handle the event twice at the same time
                    SelectionKey key=eventKeys.next();
                    eventKeys.remove();

                    if(key.isAcceptable()){
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel=serverSocketChannel.accept();
                        if(socketChannel==null){
                            continue;
                        }

                        socketChannel.configureBlocking(false);
                        socketChannel.register(serverSelector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);

                        //maybe we should say hello?
                        ByteBuffer buffer=ByteBuffer.allocate(1024);
                        buffer.put("hello client".getBytes(StandardCharsets.UTF_8));
                        buffer.flip();
                        socketChannel.write(buffer);
                    }
                    if(key.isReadable()){
                        SocketChannel socketChannel=(SocketChannel)key.channel();

                        ByteBuffer buffer=ByteBuffer.allocate(1024);

                        buffer.clear();
                        socketChannel.read(buffer);
                        buffer.flip();

                        String msgData= StandardCharsets.UTF_8.decode(buffer).toString();
                        System.out.println(msgData);
                    }

                    if(key.isWritable()){
                        SocketChannel socketChannel=(SocketChannel)key.channel();
                        String message=(String)key.attachment();
                        if(null==message){
                            continue;
                        }

                        key.attach(null);
                        System.out.println("clean the key attachment");
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
