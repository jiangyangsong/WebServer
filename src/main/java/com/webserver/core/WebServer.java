package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private ServerSocket server;
    private ExecutorService threadPool;

    public WebServer() {
        try {
            System.out.println("正在启动服务端...");
            server = new ServerSocket(8077);
            threadPool = Executors.newFixedThreadPool(50);
            System.out.println("启动服务端完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                System.out.println("等待客户端连接...");
                Socket socket = server.accept();
                ClientHandler handler = new ClientHandler(socket);
                threadPool.execute(handler);
                System.out.println("一个客户端连接了!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }
}
