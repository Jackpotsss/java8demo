package org.example.other;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Demo {
    public static void main(String[] args) {

        byte[] srcBytes = "I'm Jackpot".getBytes(StandardCharsets.UTF_8);
        //编码
        String base64encodedString = Base64.getEncoder().encodeToString(srcBytes);
        System.out.println("Base64 编码字符串 (基本) :" + base64encodedString);
        //解码
        byte[] decode = Base64.getDecoder().decode(base64encodedString);
        System.out.println(new String(decode,StandardCharsets.UTF_8));

        String s = Base64.getUrlEncoder().encodeToString(srcBytes);
        System.out.println(s);
    }
}
