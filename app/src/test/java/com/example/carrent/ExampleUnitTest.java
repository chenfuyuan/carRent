package com.example.carrent;

import com.example.carrent.utils.EditCheckUtils;
import com.example.carrent.utils.OkHttpUtils;
import com.example.carrent.vo.SignUpVo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEditUtils() {
        EditCheckUtils.checkNull("name","1805951006");
    }

    @Test
    public void testOkHttpUtils() {
        SignUpVo signUpVo = new SignUpVo();
        signUpVo.setPhone("18059851006");
        signUpVo.setPassword("123456");
        signUpVo.setAuthCode("123456");
        signUpVo.setName("chenfuyuan");

        OkHttpUtils.postRequestBuild("signin", signUpVo);
    }
}