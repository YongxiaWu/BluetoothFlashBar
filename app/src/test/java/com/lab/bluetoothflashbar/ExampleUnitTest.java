package com.lab.bluetoothflashbar;

import com.yyb.Hzk16Uttils;

import org.junit.Test;

import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String a = "哦";
        byte[] bs = Hzk16Uttils.readSingle(a);
        System.out.println(Arrays.toString(bs));
    }
}