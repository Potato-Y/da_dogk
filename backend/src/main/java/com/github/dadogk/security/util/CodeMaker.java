package com.github.dadogk.security.util;

import java.util.Random;

public class CodeMaker {
    private static final String CHARACTERS = "0123456789";
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 10;

    public static String createCode() {
        Random random = new Random();
        int length = 5;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
