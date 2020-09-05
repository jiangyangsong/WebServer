package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class RegServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(RegServlet.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("开始处理注册...");
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        String nickname = request.getParameters("nickname");
        String ageStr = request.getParameters("age");

        if (username == null || password == null ||
            nickname == null || ageStr == null ||
            !ageStr.matches("\\d{1,2}")
        ) {
            response.setEntity(new File("webapps/myweb/reg_fail.html"));
            return;
        }

        System.out.println(username+","+password+","+nickname+","+ageStr);
        int age = Integer.parseInt(ageStr);

        try (
            RandomAccessFile raf = new RandomAccessFile("user.dat","rw")
        ){
            for (int i = 0; i < raf.length(); i+=100) {
                raf.seek(i);
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data,"UTF-8").trim();
                if (name.equals(username)) {
                    response.setEntity(new File("webapps/myweb/have_name.html"));
                    return;
                }
            }

            raf.seek(raf.length());
            byte[] data = Arrays.copyOf(username.getBytes("UTF-8"),32);
            raf.write(data);

            data = Arrays.copyOf(password.getBytes("UTF-8"),32);
            raf.write(data);

            data = Arrays.copyOf(nickname.getBytes("UTF-8"),32);
            raf.write(data);

            raf.writeInt(age);
            response.setEntity(new File("webapps/myweb/reg_success.html"));
        }catch (IOException e) {
            log.error(e.getMessage(),e);
        }

        log.info("处理注册完毕!");
    }
}
