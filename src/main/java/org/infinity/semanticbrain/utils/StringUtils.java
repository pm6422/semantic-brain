package org.infinity.semanticbrain.utils;

public final class StringUtils {

    /**
     * 截取连续重复的非数字字符到指定限定值
     *
     * @param text        原文本
     * @param repeatLimit 重复字符限定值
     * @return 处理后文本
     */
    public static String truncateConsecutiveRepeatingNonDigitChar(String text, int repeatLimit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ) {
            int repeatCount = 1;
            for (int j = i + 1; j < text.length(); j++) {
                if (text.charAt(i) != text.charAt(j)) {
                    break;
                }
                repeatCount++;
            }

            if (!Character.isDigit(text.charAt(i)) && repeatCount > repeatLimit) {
                // Limit to an instance of specific length
                sb.append(org.apache.commons.lang3.StringUtils.repeat(text.charAt(i), repeatLimit));
            } else if (i + repeatCount <= text.length()) {
                sb.append(text.substring(i, i + repeatCount));
            }

            i += repeatCount;
        }
        return sb.toString();
    }
}
