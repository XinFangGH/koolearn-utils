package com.koolearn.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * srt文件转json工具类
 *
 * @author tangwenbo
 * @date 2020/2/17 13:41
 */
public class Srt2Json {

    public static void main(String[] args) {
        final JFrame jf = new JFrame("转换窗口");
        jf.setSize(400, 250);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        // 创建文本区域, 用于显示相关信息
        final JTextArea msgTextArea = new JTextArea(10, 30);
        msgTextArea.setLineWrap(true);
        panel.add(msgTextArea);

        JButton openBtn = new JButton("选择文件");
        //鼠标点击事件
        openBtn.addActionListener(e -> showFileOpenDialog(jf, msgTextArea));

        panel.add(openBtn);

        jf.setContentPane(panel);
        jf.setVisible(true);
    }


    /**
     * 选择文件事件方法
     *
     * @param parent      父级窗口
     * @param msgTextArea 信息显示区域
     * @author tangwenbo
     * @date 2020/2/14 15:23
     */
    private static void showFileOpenDialog(Component parent, JTextArea msgTextArea) {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();
        // 设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));
        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setApproveButtonText("转为json文件");
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("srt", "srt"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            File[] files = fileChooser.getSelectedFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        writeJson(file);
                    } catch (Exception e) {
                        msgTextArea.append("文件转换失败,异常原因:" + e.toString());
                    }
                    String srtPath = file.getAbsolutePath();
                    String jsonPath = srtPath.substring(0, srtPath.lastIndexOf(".")) + "_json.json";
                    msgTextArea.append("文件:" + srtPath + "转换完成,转换后位置:" + jsonPath + "\n\n");
                }
            }
        }
    }

    /**
     * 写文件方法
     *
     * @param file 原文件
     * @author tangwenbo
     * @date 2020/2/14 17:39
     */
    private static void writeJson(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
        String tempStr;
        List<SrtJsonDomain> list = new ArrayList<>();
        while ((tempStr = reader.readLine()) != null) {
            //5行一个循环读取
            SrtJsonDomain srtJsonDomain = new SrtJsonDomain();
            int count = 5;
            for (int i = 0; i < count; i++) {
                //读取序号
                if (i == 0) {
                    srtJsonDomain.setSortNum(Integer.valueOf(tempStr));
                } else if (i == 1) {
                    //读取时间
                    tempStr = reader.readLine();
                    String[] split = tempStr.split(" --> ");
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
                    LocalTime start = LocalTime.parse(split[0], dateTimeFormatter);
                    srtJsonDomain.setStart(start.get(ChronoField.MILLI_OF_DAY));
                    LocalTime end = LocalTime.parse(split[1], dateTimeFormatter);
                    srtJsonDomain.setEnd(end.get(ChronoField.MILLI_OF_DAY));
                } else if (i == 2) {
                    //读取中文信息
                    tempStr = reader.readLine();
                    srtJsonDomain.setCntext(tempStr);
                } else if (i == 3) {
                    //读取英文信息
                    tempStr = reader.readLine();
                    srtJsonDomain.setEntext(tempStr);
                } else {
                    //换行符,不做处理
                    tempStr = reader.readLine();
                    list.add(srtJsonDomain);
                }
            }
        }
        reader.close();
        String json = JSON.toJSONString(list);
        //生成的文件名
        String srtPath = file.getAbsolutePath();
        String jsonPath = srtPath.substring(0, srtPath.lastIndexOf(".")) + "_json.json";
        FileOutputStream fileOutputStream = new FileOutputStream(jsonPath);
        fileOutputStream.write(json.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * json实体类
     *
     * @author tangwenbo
     * @date 2020/2/14 17:21
     */
    @Data
    private static class SrtJsonDomain {
        //字幕序号
        private Integer sortNum;
        //英文字幕
        private String entext;
        //中文字幕
        private String cntext;
        //开始时间
        private long start;
        //结束时间
        private long end;
    }
}
