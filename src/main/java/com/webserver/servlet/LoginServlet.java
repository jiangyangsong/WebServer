package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("开始处理登录...");
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        try (
            RandomAccessFile raf = new RandomAccessFile("user.dat","r")
        ){
            for (int i = 0; i < raf.length(); i+=100) {
                raf.seek(i);
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data,"UTF-8").trim();
                if (name.equals(username)) {
                    raf.read(data);
                    String pwd = new String(data,"UTF-8").trim();
                    if (pwd.equals(password)) {
                        response.setEntity(new File("webapps/myweb/ps.html"));
                        return;
                    }
                    break;
                }
            }
            response.setEntity(new File("webapps/myweb/login_fail.html"));
        } catch (IOException e) {
           log.error(e.getMessage(),e);
        }
        log.info("处理登录完毕!");
    }
}
