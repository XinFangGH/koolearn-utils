package com.koolearn.utils;

import java.util.Random;

public class RandomUtils {


    public static void main(String[] args) {
        //生成个数
        int count = 10;
        for (int i = 0; i < count; i++) {
            String random = getRandom(8);

            try {
                //入库
                System.out.println(random);
            } catch (Exception e) {
                System.out.println("捕捉主键冲突,重试");
                i--;
            }
        }
    }

    /**
     * 获取随机数(大小写字母+数字)
     *
     * @param count 随机数长度
     * @return java.lang.String
     * @author tangwenbo
     * @date 2020/2/18 13:23
     */
    private static String getRandom(int count) {
        StringBuilder id = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            char s = 0;
            int j = random.nextInt(3);
            switch (j) {
                case 0:
                    //随机生成数字 48-57
                    s = (char) (random.nextInt(57) % (57 - 48 + 1) + 48);
                    break;
                case 1:
                    //随机生成大写字母 65-90
                    s = (char) (random.nextInt(90) % (90 - 65 + 1) + 65);
                    break;
                case 2:
                    //随机生成小写字母 97-122
                    s = (char) (random.nextInt(122) % (122 - 97 + 1) + 97);
                    break;
                default:
                    break;
            }
            id.append(s);
        }
        return id.toString();
    }

}
