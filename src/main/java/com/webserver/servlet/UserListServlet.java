package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.vo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UserListServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UserListServlet.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("开始加载用户列表...");
        List<User> list = new ArrayList<>();
        try (
            RandomAccessFile raf = new RandomAccessFile("user.dat","r")
        ){
            for (int i = 0; i < raf.length(); i+=100) {
                byte[] data = new byte[32];
                raf.read(data);
                String username = new String(data,"UTF-8").trim();

                raf.read(data);
                String password = new String(data,"UTF-8").trim();

                raf.read(data);
                String nickname = new String(data,"UTF-8").trim();

                int age = raf.readInt();
                list.add(new User(username,password,nickname,age));
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }

        FileTemplateResolver re = new FileTemplateResolver();
        re.setTemplateMode("html");
        re.setCharacterEncoding("UTF-8");
        TemplateEngine te = new TemplateEngine();
        te.setTemplateResolver(re);
        Context context = new Context();
        context.setVariable("users",list);
        String html = te.process("webapps/myweb/userList.html",context);
        System.out.println(html);
        try {
            response.setData(html.getBytes("UTF-8"));
            response.putHeader("Content-Length","text/html");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(),e);
        }
        log.info("加载用户列表完毕!");
    }
}
