package com.iris.nioDemo.client;

import com.iris.nioDemo.server.NioServer;

public class ClientMain {
    public static void main(String[] args) {
        new NioClient().start();
    }
}
