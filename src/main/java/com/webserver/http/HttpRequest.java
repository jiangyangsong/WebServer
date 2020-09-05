package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Socket socket;
    private InputStream in;
    private String method;
    private String uri;
    private String protocol;
    private Map<String,String> headers = new HashMap<>();
    private String requestURI;
    private String queryString;
    private Map<String,String> parameters = new HashMap<>();

    public HttpRequest(Socket socket) throws EmptyRequestException {
        try {
            this.socket = socket;
            in = socket.getInputStream();
            parseRequestLine();
            parseHeader();
            parseContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestLine() throws EmptyRequestException {
        System.out.println("RequestLine:开始解析请求行...");
        try {
            String line = readLine();
            if (line.isEmpty()) {
                throw new EmptyRequestException();
            }
            System.out.println("请求行:"+line);
            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];
            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);
            parseUri();
            System.out.println("requestURI:"+requestURI);
            System.out.println("queryString:"+queryString);
            System.out.println("parameters:"+parameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("RequestLine:解析请求行完毕!");
    }

    private void parseUri(){
        try {
            uri = URLDecoder.decode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!uri.contains("?")) {
            requestURI = uri;
        } else {
            String[] data = uri.split("\\s");
            requestURI = data[0];
            if (data.length>1) {
                queryString = data[1];
                parseParameter(queryString);
            }
        }
    }

    private void parseParameter(String line) {
        String[] data = line.split("&");
        for (String pairs : data) {
            String[] arr = pairs.split("=");
            if (arr.length>1) {
                parameters.put(arr[0],arr[1]);
            } else {
                parameters.put(arr[0],null);
            }
        }
    }

    private void parseHeader() {
        System.out.println("Header:开始解析消息头...");
        try {
            while (true) {
                String line = readLine();
                if (line.isEmpty()) {
                    break;
                }
                String[] data = line.split(":\\s");
                headers.put(data[0],data[1]);
            }
            System.out.println("headers:"+headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Header:解析消息头完毕!");
    }

    private void parseContent() {
        System.out.println("Content:开始解析消息正文...");
        if ("post".equalsIgnoreCase(method)) {
            if (headers.containsKey("Content-Length")) {
                int len = Integer.parseInt(headers.get("Content-Length"));
                byte[] data = new byte[len];
                try {
                    in.read(data);
                    if (headers.containsKey("Content-Type")) {
                        String contentType = headers.get("Content-Type");
                        if ("application/x-www-form-urlencoded".equals(contentType)) {
                            String line = new String(data,"ISO8859-1");
                            line = URLDecoder.decode(line,"UTF-8");
                            System.out.println("转码后正文:"+line);
                            parseParameter(line);
                            System.out.println("parameters:"+parameters);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Content:解析消息正文完毕!");
    }

    private String readLine() throws IOException {
        int d;
        char pre = 'a',cur = 'a';
        StringBuilder builder = new StringBuilder();
        while ((d = in.read()) != -1) {
            cur = (char) d;
            if (pre == 13 && cur == 10) {
                break;
            }
            builder.append(cur);
            pre = cur;
        }
        return builder.toString().trim();
    }

    public String getParameters(String name) {
        return parameters.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }
}
