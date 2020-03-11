package com.koolearn.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class UtilsApplication {

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
        //选择框
        JRadioButton gb = new JRadioButton("简体中文(GB18030)", true);
        JRadioButton utf = new JRadioButton("简体中文(UTF-8)");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(gb);
        buttonGroup.add(utf);
        panel.add(gb);
        panel.add(utf);

        JButton openBtn = new JButton("选择文件");
        //鼠标点击事件
        openBtn.addActionListener(e -> showFileOpenDialog(jf, msgTextArea, gb, utf));

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
    private static void showFileOpenDialog(Component parent, JTextArea msgTextArea, JRadioButton gb, JRadioButton utf) {
        //判断当前编码格式,默认gb18030
        String encoded = "gb18030";
        if (utf.isSelected()) {
            encoded = "utf-8";
        }
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
        fileChooser.setFileFilter(new FileNameExtensionFilter("lrc", "lrc"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            File[] files = fileChooser.getSelectedFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        writeJson(file, encoded);
                    } catch (Exception e) {
                        msgTextArea.append("文件转换失败,异常原因:" + e.toString() + "\n\n");
                        return;
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
    private static void writeJson(File file, String encoded) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoded));
        String tempStr;
        List<SrtJsonDomain> list = new ArrayList<>();
        int sortNum = 1;
        while ((tempStr = reader.readLine()) != null) {
            //[开头并切长度大于10表示可以读取
            if (tempStr.startsWith("[") && tempStr.toCharArray().length > 10) {
                String substring = tempStr.substring(1, 9);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SS");
                LocalTime parse = null;
                try {
                    parse = LocalTime.parse("00:" + substring, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    System.out.println("当前日期转换失败,表示不是正文,略过");
                }
                //不为空表示可以计算
                if (parse != null) {
                    SrtJsonDomain srtJsonDomain = new SrtJsonDomain();
                    int chineseSub = 0;
                    //获取文字信息
                    String text = tempStr.substring(10).trim();
                    char[] chars = text.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        char aChar = chars[i];
                        if (String.valueOf(aChar).matches("[\u4e00-\u9fa5]")) {
                            chineseSub = i;
                            break;
                        }
                    }
                    System.out.println("text:" + text);
                    if (chineseSub == 0) {
                        throw new Exception("当前文字编码识别失败,当前编码:" + encoded);
                    }
                    //英文
                    String english = text.substring(0, chineseSub);
                    srtJsonDomain.setEntext(english);
                    //中文
                    String chinese = text.substring(chineseSub);
                    srtJsonDomain.setCntext(chinese);
                    //时间
                    int time = parse.get(ChronoField.MILLI_OF_DAY);
                    srtJsonDomain.setStart(time);
                    srtJsonDomain.setSortNum(sortNum);
                    sortNum++;
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
