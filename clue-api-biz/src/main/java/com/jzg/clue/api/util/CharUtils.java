package com.jzg.clue.api.util;

/**
 * @author mush
 * @time 2017/6/8 15:40
 * @describe 字符串的处理工具类
 */
public class  CharUtils {
    /**
     * 填充字符串
     *
     * @param originalStr 原字符串 001
     * @param len    目标字符串长度 3
     * @param type   往原字符串添加的位置0：右-->001XXX，1：左-->XXX001
     * @param alexin 补位字符 X
     * @return 目标字符串
     */
    public static String paddingChar(String originalStr, int len,int type,char alexin) {
        String str = "";
        int strlen = originalStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        if(type==1){//左填充
            str = str + originalStr;
        }else{//右填充
            str = originalStr + str;
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(CharUtils.paddingChar(String.valueOf(4), 11, 1,'0'));
    }
}
