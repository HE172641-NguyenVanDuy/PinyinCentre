package com.pinyincentre.pinyin.util;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGeneratorUtil {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword(int length) {
        if (length < 8) length = 8;

        List<Character> charList = PASSWORD_ALLOW_BASE.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(charList);

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charList.get(random.nextInt(charList.size())));
        }
        return sb.toString();
    }
}
