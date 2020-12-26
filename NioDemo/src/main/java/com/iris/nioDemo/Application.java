package com.iris.nioDemo;

import com.iris.nioDemo.server.NioServer;

public class Application {
    public static void main(String[] args) {
        new NioServer(8090).start();
    }
}
