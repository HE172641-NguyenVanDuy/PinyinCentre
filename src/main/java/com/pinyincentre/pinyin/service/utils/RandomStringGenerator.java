package com.pinyincentre.pinyin.service.utils;

import java.util.Random;

public class RandomStringGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@.";

    public static String generate() {
        Random random = new Random();
        int length = random.nextInt(7) + 6; // Random từ 6 đến 10 (6 + 0~4)

        String allChars = LOWERCASE + UPPERCASE + DIGITS;

        StringBuilder sb = new StringBuilder();

        // Generate (length - 1) ký tự đầu
        for (int i = 0; i < length - 1; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Ký tự đặc biệt ở cuối
        sb.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generate());
    }
}
