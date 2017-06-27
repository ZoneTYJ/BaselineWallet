package com.vfinworks.vfsdk;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiaoshengke on 2016/9/12.
 */
public class Test {

    public static void main(String[] args) {
        int l = 4, h = 2;
        System.out.println(Math.sin(0.5235987755982989));

        //        String temp = "<form name='form1' method='post' action='null'><input
        // type='hidden' name='Retdesc' value='parameters error'></form><script>document.form1
        // .submit();</script>";
        //        String reg = "name\\s*\\=\\s*[\\w\'\"&&[^\\>]]+|value\\s*\\=\\s*[\\w\'\"\\s
        // &&[^\\>]]+";
        //        Pattern pattern = Pattern.compile (reg);
        //        Matcher matcher = pattern.matcher (temp);
        //        while (matcher.find ())
        //        {
        //            System.out.println (matcher.group ());
        //        }

        HashMap<String, String> map = new HashMap<String, String>();
        String temp =
                "<form id='frmBankID' name='frmBankName' method='post' action='WXPAY'><input " +
                        "type='hidden' name='appData'  value='{\"appid\":\"wx678ad9de0bf9d684\"," +
                        "\"noncestr\":\"7suSXkDBekVFCOra\",\"package\":\"Sign=WXPay\"," +
                        "\"partnerid\":\"1370335602\"," +
                        "\"prepayid\":\"wx20161107163123d7d3aab1a10285485948\"," +
                        "\"sign\":\"219FB78F43986418CC0A32F057401C09\"," +
                        "\"timestamp\":\"1478507403\"}' /></form>";

        String reg = "value\\s*=\\s*'\\{[\\s\\S]*\\}'";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(temp);
        while (matcher.find()) {
            String group = matcher.group();
            System.out.println(group);
            String value = group.split("\\'")[1];
            map.put("value", value);
        }
        System.out.println("map key会被覆盖，自己解决");
        System.out.println(map);
    }
}
