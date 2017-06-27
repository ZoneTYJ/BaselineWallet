package com.vfinworks.vfsdk.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.vfinworks.vfsdk.R;
import com.vfinworks.vfsdk.activity.login.LoginActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private String TAG = this.getClass().getName();
    private Context context;
    private static Utils mInstance;

    private Utils() {
    }

    public static synchronized Utils getInstance() {
        if (mInstance == null) {
            mInstance = new Utils();
        }
        return mInstance;
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 把服务器返回的json数据转换为类型为type的JavaBean
     */
    public <T> T json2Object(String response, Class<T> clazz) {
        if (TextUtils.isEmpty(response))
            return null;
        T result = null;
        try {
            result = new Gson().fromJson(response, clazz);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

    /*
     * hashmap 转json
     */
    public String hashmap2Json(Map<String, String> map) {
        String mapToJson = new Gson().toJson(map);
        return mapToJson;
    }

    /**
     * 格式化金额
     */
    public String formatMoney(String money) {
        DecimalFormat format = new DecimalFormat("##0.00");
        return format.format(Double.parseDouble(money));
    }

    /**
     * 根据手机的屏幕属性从 dip 的单位 转成为 px(像素)
     */
    public float dip2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.density;
    }

    /**
     * 根据手机的屏幕属性从 px(像素) 的单位 转成为 dip
     */
    public float px2dip(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value / metrics.density;
    }

    /**
     * 根据手机的屏幕属性从 sp的单位 转成为px(像素)
     */
    public float sp2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.scaledDensity;
    }

    /**
     * 根据手机的屏幕属性从 px(像素) 的单位 转成为 sp
     */
    public float px2sp(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value / metrics.scaledDensity;
    }

    /**
     * 获取手机唯一编号——IMEI
     *
     * @return 获取手机的唯一编号，如果获取直接返回，如果获取不到返回null
     */
    public String getPhoneDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phoneImei = telephonyManager.getDeviceId();
        if (!TextUtils.isEmpty(phoneImei)) {
            // 如果存在，直接返回
            return phoneImei;
        } else {
            return null;
        }
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 验证输入手机号码
     *
     * @param str 认证的字符串
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public boolean isMobile(String str) {
        String regex = "^1\\d{10}$";
        return match(regex, str);
    }

    /**
     * 功能：判断字符串是否为数字
     */
    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为日期格式
     */
    public boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|" +
                        "(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))" +
                        "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|" +
                        "([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))" +
                        "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|" +
                        "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|" +
                        "(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|" +
                        "([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }

    /*
     * 验证身份证号码
     */
    public String IDCardValidate(String IDStr) {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String Ai = "";

        if (IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为18位!";
            return errorInfo;
        }

        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "18位身份证号码除最后一位外,都应为数字!";
            return errorInfo;
        }

        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效!";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围!";
                return errorInfo;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效!";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效!";
            return errorInfo;
        }

        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误!";
            return errorInfo;
        }

        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效, 不是合法的身份证号码!";
                return errorInfo;
            }
        } else {
            return "";
        }
        return "";
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    public String formatMaskCardNo(String cardNo) {
        StringBuffer strb = new StringBuffer(cardNo);
        StringBuffer strStar = new StringBuffer();
        for (int i = 0; i < cardNo.length() - 4; i++) {
            strStar.append("*", 0, 1);
        }

        return strb.replace(0, cardNo.length() - 4, strStar.toString()).toString();
    }

    /**
     * 获取导航栏高度
     */
    public int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public int getBankDrawableIcon(String key) {
        if(key==null){
            return -1;
        }
        if (key.equals("CCB")) {
            return R.drawable.vf_js_icon;
        } else if (key.equals("CMB")) {
            return R.drawable.vf_zs_icon;
        } else if (key.equals("CEB")) {
            return R.drawable.vf_gd_icon;
        } else if (key.equals("ABC")) {
            return R.drawable.vf_nongye;
        } else if (key.equals("ICBC")) {
            return R.drawable.vf_gongshang;
        } else if (key.equals("BOC")) {
            return R.drawable.vf_zhongguo;
        } else if (key.equals("CMBC")) {
            return R.drawable.vf_minsheng;
        } else if (key.equals("CIB")) {
            return R.drawable.vf_xingye;
        } else if (key.equals("BCM")) {
            return R.drawable.vf_jiaotong;
        } else if (key.equals("GDB")) {
            return R.drawable.vf_guangfa;
        }
        return -1;
    }


    public static String stringFormat(String time) {
        return String.format("%s-%s-%s %s:%s:%s",
                time.substring(0, 4), time.substring(4, 6), time.substring(6, 8), time.substring
                        (8, 10), time.substring(10, 12), time.substring(12, 14));
    }

    public static Map<String, String> getHTMLFormMaps(String html) {
        HashMap<String, String> map = new HashMap<String, String>();
        String temp = html;
        String reg = "value\\s*=\\s*'\\{[\\s\\S]*\\}'";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(temp);
        while (matcher.find()) {
            String group = matcher.group();
            String value = group.split("\\'")[1];
            map.put("value", value);
        }
        return map;
    }

    public static String getAliformMaps(String html) {
        String temp = html;
        String reg = "action\\=\'(.*?)\'";
        String s="";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(temp);
        while (matcher.find()) {
            String group = matcher.group();
            s = group.split("action='")[1];
            s = s.substring(0,s.length() - 1);
            return s;
        }
        return s;
    }

    public static String getRandom(){
        return String.valueOf(Math.round(Math.random() * 10000000));
    }

    public static String getOnlyValue(){
        return SharedPreferenceUtil.getInstance().getStringValueFromSP(LoginActivity.ACCOUNT) + System.currentTimeMillis();
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 验证Email
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }
}
