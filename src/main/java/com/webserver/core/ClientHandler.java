package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(socket);

            String path = request.getRequestURI();
            System.out.println("抽象路径:"+path);
            HttpServlet servlet = ServletContext.getServlet(path);
            if (servlet != null) {
                servlet.service(request,response);
            } else {
                File file = new File("webapps"+path);
                if (file.isFile() && file.exists()) {
                    log.info("资源已找到!");
                    response.setEntity(file);
                } else {
                    log.info("资源不存在!");
                    response.setStateCode(404);
                    response.setStateReason("NotFound");
                    response.setEntity(new File("webapps/root/404.html"));
                }
            }
            response.flush();
            log.info("响应客户端完毕!");
        } catch (EmptyRequestException e) {
            log.error("空请求...");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
    }
}
