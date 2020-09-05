package com.webserver.core;

import com.webserver.servlet.HttpServlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletContext {
    private static Map<String, HttpServlet> servletMapping = new HashMap<>();

    static {
        initServletMapping();
    }

    private static void initServletMapping() {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read("config/servlets.xml");
            Element root = doc.getRootElement();
            List<Element> list = root.elements("servlet");
            for (Element e : list) {
                String key = e.attributeValue("path");
                Class<?> cls = Class.forName(e.attributeValue("className"));
                HttpServlet servlet = (HttpServlet) cls.newInstance();
                servletMapping.put(key,servlet);
            }
            System.out.println("servletMapping:"+servletMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpServlet getServlet(String path) {
        return servletMapping.get(path);
    }
}
