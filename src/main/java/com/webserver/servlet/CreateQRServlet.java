package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qrcode.QRCodeUtil;

import java.io.ByteArrayOutputStream;


public class CreateQRServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CreateQRServlet.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        log.info("开始加载二维码...");
        String qrcode = request.getParameters("qrcode");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            QRCodeUtil.encode(qrcode,out);
            byte[] data = out.toByteArray();
            response.setData(data);
            response.putHeader("Content-Type","image/jpeg");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        log.info("加载二维码完毕!");
    }
}
