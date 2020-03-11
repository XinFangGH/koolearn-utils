package com.koolearn.utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Xml2Json {


    public static void main(String[] args) throws Exception {
        Map<String, String> data = new HashMap<String, String>(8);

        //1.创建Reader对象
        SAXReader reader = new SAXReader();
        //2.加载xml
        Document document = reader.read(new File("C:\\Users\\XinFang\\Desktop\\新建文件夹\\y.xml"));
        //3.获取根节点
        Element rootElement = document.getRootElement();
        System.out.println("根节点名称:" + rootElement.getName());
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            elementList(element);
        }


//        Iterator iterator = rootElement.elementIterator();
//        while (iterator.hasNext()) {
//            Element stu = (Element) iterator.next();
//            System.out.println("当前节点名称:" + stu.getName());
//            getElement(stu);
//        }

    }


    private static void elementList(Element element) {
        List<Element> elements = element.elements();
        if (!CollectionUtils.isEmpty(elements)) {
            for (Element child : elements) {
                List<Attribute> attributeList = child.attributes();
                for (Attribute attr : attributeList) {
                    System.out.println("属性名:" + attr.getName() + " --- " + attr.getValue());
                }
                List<Element> elementList = child.elements();
                for (Element ele : elementList) {
                    System.out.println("元素名:" + ele.getName() + " --- " + ele.getText());
                }
                elementList(child);
            }
        }


    }


    /**
     * 递归获取子级标签
     *
     * @param element
     * @author tangwenbo
     * @date 2020/3/10 18:18
     */
    private static void getElement(Element element) {
        Iterator iterator = element.elementIterator();
        if (iterator != null && iterator.hasNext()) {
            Element stu = (Element) iterator.next();
            System.out.println("子标签名称:" + stu.getName() + ";子标签值:" + stu.getText());
            getElement(stu);
        }

    }
}

