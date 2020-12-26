package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AioClient {
    private AsynchronousSocketChannel socketChannel;

    public AioClient() {
        try {
            socketChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8090)
                    , null, new CompletionHandler<Void, Object>() {
                        @Override
                        public void completed(Void result, Object attachment) {
                            String msg = "hello world";
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.clear();
                            buffer.put(msg.getBytes(StandardCharsets.UTF_8));
                            buffer.flip();

                            if (buffer.hasRemaining()) {
                                socketChannel.write(buffer);
                            }
                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {

                        }
                    });
            System.in.read();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
