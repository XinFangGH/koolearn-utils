package com.koolearn.utils;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UtilsApplication {

    /**
     * 转换工具
     *
     * @author tangwenbo
     * @date 2020/3/26 5:43 下午
     */
    public static void main(String[] args) {
        Srt2Json.srt2JsonStart();
    }

}
