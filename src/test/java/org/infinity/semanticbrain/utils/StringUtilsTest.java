package org.infinity.semanticbrain.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testTruncateConsecutiveRepeatingNonDigitChar() {
        String str1 = "abcccdddddefgggggg";
        Assert.assertEquals("abcccddddefgggg", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str1, 4));

        String str2 = "cccccdddddefgggggg";
        Assert.assertEquals("ccccddddefgggg", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str2, 4));

        String str3 = "cccccdddddefggggggfffff";
        Assert.assertEquals("ccccddddefggggffff", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str3, 4));

        String str4 = "abcdeeefghi";
        Assert.assertEquals("abcdeeefghi", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str4, 4));

        String str5 = "111112222233344444";
        Assert.assertEquals("111112222233344444", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str5, 4));

        String str6 = "1234567890";
        Assert.assertEquals("1234567890", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str6, 4));

        String str7 = "11111";
        Assert.assertEquals("11111", StringUtils.truncateConsecutiveRepeatingNonDigitChar(str7, 4));
    }
}
