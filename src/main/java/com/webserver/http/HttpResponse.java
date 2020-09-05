package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {
    private Socket socket;
    private OutputStream out;
    private String stateReason = "OK";
    private int stateCode = 200;
    private File entity;
    private byte[] data;
    private Map<String,String> headers = new HashMap<>();

    public HttpResponse(Socket socket) {
        try {
            this.socket = socket;
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        sendResponseLine();
        sendHeader();
        sendContent();
    }

    private void sendResponseLine() {
        try {
            System.out.println("ResponseLine:开始发送状态行...");
            String line = "HTTP/1.1"+" "+stateCode+" "+stateReason;
            System.out.println("状态行:"+line);
            println(line);
            System.out.println("ResponseLine:状态行发送完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHeader() {
        try {
            System.out.println("Header:开始发送响应头...");
            Set<Entry<String,String>> set = headers.entrySet();
            for (Entry<String, String> e : set) {
                String key = e.getKey();
                String value = e.getValue();
                String line = key+": "+value;
                System.out.println("响应头:"+line);
                println(line);
            }
            println("");
            System.out.println("Header:发送响应头完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putHeader(String key, String value) {
        this.headers.put(key,value);
    }

    private void sendContent() {
        if (data != null) {
            try {
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (entity != null) {
            try (
                FileInputStream fis = new FileInputStream(entity)
            ){
                int len;
                byte[] data = new byte[1024];
                while ((len = fis.read(data)) != -1) {
                    out.write(data,0,len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void println(String line) throws IOException {
        out.write(line.getBytes("UTF-8"));
        out.write(13);
        out.write(10);
    }

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
