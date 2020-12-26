package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AioServer {
    private int port;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel serverSocketChannel;

    public AioServer(int port) {
        this.port = port;
    }

    public void start(){
        try {
             channelGroup=AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(),1);
             serverSocketChannel=AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.bind(new InetSocketAddress(port));

                serverSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AioServer>() {
                @Override
                public void completed(AsynchronousSocketChannel result, AioServer attachment) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    buffer.clear();
                    try {
                        result.read(buffer).get();
                        buffer.flip();
                        System.out.println("receive "+ Charset.forName("utf-8").decode(buffer));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        attachment.serverSocketChannel.accept(attachment,this);
                    }
                }

                @Override
                public void failed(Throwable exc, AioServer attachment) {

                }
            });
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
