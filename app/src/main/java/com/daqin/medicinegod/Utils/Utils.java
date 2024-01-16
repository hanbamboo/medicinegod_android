package com.daqin.medicinegod.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.daqin.medicinegod.BuildConfig;
import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.NoticeActivity;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.RegisterActivity;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.entity.Notice;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mysql.jdbc.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Utils {


    public static int findIndexInList(List<String> list, String str) {
        if (list == null || list.size() == 0) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        //getSharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        return preferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
        editor.putBoolean(key, value);
        //调用apply方法将添加的数据提交，从而完成存储的动作
        editor.apply();
    }

    public static void putSet(Context context, String key, Set<String> set) {
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
        editor.putStringSet(key, set);
        //调用apply方法将添加的数据提交，从而完成存储的动作
        editor.apply();
    }

    public static Set<String> getSet(Context context, String key, String defaultValue) {
        //getSharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        return preferences.getStringSet(key, null);
    }

    /**
     * String类型 键值对 存
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void putString(Context context, String key, String value) {
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
        editor.putString(key, value);
        //调用apply方法将添加的数据提交，从而完成存储的动作
        editor.apply();
    }

    /**
     * String类型 键值对 取
     *
     * @param context      上下文
     * @param key          键
     * @param defaultValue 如果找不到内容则返回的默认内容
     * @return 返回取到的内容
     */
    public static String getString(Context context, String key, String defaultValue) {
        //getSharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        return preferences.getString(key, defaultValue);
    }

    /**
     * int类型 键值对 存
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void putInt(Context context, String key, int value) {
        //获取一个 SharedPreferences对象
        //第一个参数：指定文件的名字，只会续写不会覆盖
        //第二个参数：MODE_PRIVATE只有当前应用程序可以续写
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        //向其中添加数据，是什么数据类型就put什么，前面是键，后面是数据
        editor.putInt(key, value);
        //调用apply方法将添加的数据提交，从而完成存储的动作
        editor.apply();
    }

    /**
     * int类型 键值对 取
     *
     * @param context      上下文
     * @param key          键
     * @param defaultValue 如果找不到内容则返回的默认内容
     * @return 返回取到的内容
     */
    public static int getInt(Context context, String key, int defaultValue) {
        //getSharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(Constant.PREFERENCES_NAME, Context.MODE_PRIVATE);
        //然后通过键的方式取出，后边是如果找不到的默认内容
        return preferences.getInt(key, defaultValue);
    }

    /**
     * 从时间戳获得具体时间
     *
     * @param Value 时间戳
     * @return 如2020-04-04
     */
    public static String getStringFromDate(long Value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Value);
        return dateFormat.format(date);
    }

    /**
     * @param date  long类型的时间数据
     * @param style 返回的样式
     * @return 样式1 : 过期时间:XX年XX月XX天后过期
     * 样式2 : 还有XX年XX月XX天过期
     * 样式3：（1D） （2M） （3Y）
     */
    public static String getOutDateString(long date, int style) {
        //timeA  2022-03-01 药品的时间
        //timeB  2022-01-01 现在的时间
        String timeA = getStringFromDate(date);
        Calendar cl = Calendar.getInstance();
        String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + cl.get(Calendar.DAY_OF_MONTH);
        int[] outdate_res = getRemainTime(timeA, timeB);
        StringBuilder res = new StringBuilder("");
        if (style == 1) {
            res.append("过期时间:");
            res.append((outdate_res[0] == 0) ? "" : outdate_res[0] + "年");
            res.append((outdate_res[1] == 0) ? "" : outdate_res[1] + "月");
            res.append((outdate_res[2] == 0) ? "" : outdate_res[2] + "天");
            res.append("后过期");
        } else if (style == 2) {
            res.append("还有");
            res.append((outdate_res[0] == 0) ? "" : outdate_res[0] + "年");
            res.append((outdate_res[1] == 0) ? "" : outdate_res[1] + "月");
            res.append((outdate_res[2] == 0) ? "" : outdate_res[2] + "天");
            res.append("过期");
        } else if (style == 3) {
            outdate_res = getRemainTimeSHOW(timeA, timeB);
            res.append(" (");
            if (outdate_res[3] >= 0)
                res.append(outdate_res[3]).append("Y");
            else if (outdate_res[4] >= 0)
                res.append(outdate_res[4]).append("M");
            else if (outdate_res[5] >= 0)
                res.append(outdate_res[5]).append("D");
            res.append(")");
        }
        if (res.toString().equals("还有过期") || res.toString().equals("过期时间:后过期")) {
            return "已过期";
        }
        if (res.toString().equals(" ()") || (res.toString().equals(" (0Y)"))) {
            return "(0D)";
        }

        return res.toString();
    }

    /**
     * 判断时间是否过期
     *
     * @param timeA 药品时间
     * @param timeB 过期时间
     * @return -1 过期 0临期 1正常
     */
    public static int isTimeOut(String timeA, String timeB) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int res = 0;
        try {
            //将日期转成Date对象作比较
            Date fomatDate1 = dateFormat.parse(timeA);
            Date fomatDate2 = dateFormat.parse(timeB);

            Long time1 = fomatDate1.getTime();
            Long time2 = fomatDate2.getTime();

            int day = (int) ((time1 - time2) / (24 * 3600 * 1000));
            if (day <= 0) {
                res = -1;
            } else if (day <= 60) {
                res = 0;
            } else {
                res = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 取得md5
     *
     * @param sourceStr 加密的串
     * @param digit     返回位数
     * @return 返回 digit 位的md5
     */
    public static String getMD5(String sourceStr, int digit) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
//            System.out.println("MD5(" + sourceStr + ",32) = " + result);
//            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (digit == 16) {
            return result.substring(8, 24);
        } else {
            return result;
        }

    }


    public static boolean delMedicine(Context context, String keyid) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long status = db.delete(Constant.TABLE_NAME_MEDICINE, Constant.COLUMN_M_KEYID + "=?", new String[]{keyid});
        db.close();
        return status >= 1;
    }

    public static boolean delMedicine(Context context, String keyid, boolean isLocal) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_M_DELFLAG, 1);
        long status = db.update(Constant.TABLE_NAME_MEDICINE, values, Constant.COLUMN_M_KEYID + "=?", new String[]{keyid});
        db.close();
        return status >= 1;
    }

    public static int[] getRemainTimeSHOW(String timeA, String timeB) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int[] outdate = new int[]{0, 0, 0, 0, 0, 0};
        try {
            //将日期转成Date对象作比较
            Date fomatDate1 = dateFormat.parse(timeA);
            Date fomatDate2 = dateFormat.parse(timeB);

            assert fomatDate1 != null;
            assert fomatDate2 != null;
            Long time1 = fomatDate1.getTime();
            Long time2 = fomatDate2.getTime();


            int day =(int) (time1 - time2) / 86400000;
            if (day <= 0) {
                outdate = new int[]{0, 0, 0, 0, 0, 0};
            } else if (day <= 31) {
                outdate = new int[]{0, 0, 0, 0, 0, day};
            } else if (day / 31 <= 12) {
                outdate = new int[]{0, 0, 0, 0, day / 31, day % 31};
            } else {
                outdate = new int[]{0, 0, 0, day / 12, day / 12 / 31, day / 12 % 31};
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outdate;

    }

    public static Bitmap compressByQuality(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("info", "图片大小：" + bit.getByteCount());//10661184
        return bit;
    }

    /**
     * 获取药品剩余时间并返回数组
     *
     * @param timeA 药品时间
     * @param timeB 过期时间
     * @return 返回时间数组 {年 月 天 时 分 秒}
     */
    public static int[] getRemainTime(String timeA, String timeB) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int[] outdate = new int[]{0, 0, 0, 0, 0, 0};
        try {
            //将日期转成Date对象作比较
            Date fomatDate1 = dateFormat.parse(timeA);
            Date fomatDate2 = dateFormat.parse(timeB);

            assert fomatDate1 != null;
            assert fomatDate2 != null;
            Long time1 = fomatDate1.getTime();
            Long time2 = fomatDate2.getTime();


            int second = (int) ((time1 - time2) / 1000);

            //如有更好的方案则优化此处
            if (second > 0) {
                outdate[5] = second;
                if (outdate[5] >= 60) {
                    outdate[4] = outdate[5] / 60;
                    outdate[5] = outdate[5] % 60;
                    if (outdate[4] >= 60) {
                        outdate[3] = outdate[4] / 60;
                        outdate[4] = outdate[4] % 60;
                        if (outdate[3] > 24) {
                            outdate[2] = outdate[3] / 24;
                            outdate[3] = outdate[3] % 24;
                            if (outdate[2] > 30) {
                                outdate[1] = outdate[2] / 30;
                                outdate[2] = outdate[2] % 30;
                                if (outdate[1] > 12) {
                                    outdate[0] = outdate[1] / 12;
                                    outdate[1] = outdate[1] % 12;
                                }
                            }
                        }
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outdate;
    }


    public static Bitmap getBitmapFromResourse(Context context, int resourseId) {
        return BitmapFactory.decodeResource(context.getResources(), resourseId);

    }

    public static String getSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }

        return sb.toString().toUpperCase();
    }

    /**
     * @param Value 时间字串,如2020-04-26
     * @return long类型的时间戳
     * @getDateFromString
     */
    public static long getDateFromString(String Value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            Date fomatDate = dateFormat.parse(Value);
            assert fomatDate != null;
            time = fomatDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getNoticeTimeFromString(String Value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
        long time = 0;
        try {
            Date fomatDate = dateFormat.parse(Value);
            assert fomatDate != null;
            time = fomatDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = new Date(time);


        return dateFormat1.format(date);
    }

    /**
     * 从时间戳获得具体时间
     *
     * @param Value 时间戳
     * @return 如2020-04-04
     */
    public static String getStringFromTime(long Value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
        Date date = new Date(Value);
        return dateFormat.format(date);
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，不然返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * @param Value 时间字串,如2020年04月26日 15时23分17秒
     * @return long类型的时间戳
     * @getDateFromString
     */
    public static long getTimeFromString(String Value) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        long time = 0;
        try {
            Date fomatDate = dateFormat.parse(Value);
            assert fomatDate != null;
            time = fomatDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //创建对应的流对象
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);//将流对象与Bitmap对象进行关联。
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap getBitmapFromByte(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String getStringFromArrayList(ArrayList<String> list) {
        if (list == null || list.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str);
            sb.append("@@");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public static String getStringFromSet(Set<String> set) {
        if (set == null || set.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        //获取迭代器
        for (String s : set) {
            sb.append(s);
            sb.append("@@");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * startShareFile 调起分享页面
     *
     * @param context  上下文
     * @param fileName 文件路径
     * @param fileType 多种文件type
     */
    public static void startShareFile(Context context, String fileName, String fileType, String str) {

        if (Utils.isExists(fileName)) {
            File file = new File(fileName);
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType(fileType);
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(share, str));
        } else {
            Toast.makeText(context, "文件不存在，请重试", Toast.LENGTH_SHORT).show();
        }


    }

    public static boolean isExists(String filepath) {
        return new File(filepath).exists();

    }

    /**
     * startOpenFile 打开文件
     *
     * @param context  上下文
     * @param fileName 文件路径
     * @param filetype 多种文件type
     */
    public static void startOpenFile(Context context, String fileName, String filetype, String str) {

        if (Utils.isExists(fileName)) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, new File(fileName));
            intent.setDataAndType(uri, filetype);

            context.startActivity(Intent.createChooser(intent, str));
        } else {
            Toast.makeText(context, "文件不存在，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * insertHisExcel 插入导入导入功能的历史文件记录
     *
     * @param context  文
     * @param flag     是什么flag：导入/导入/模板
     * @param fileName 文件名
     * @param filePath 文件路径
     */
    public static void insertHisExcel(Context context, String flag, String fileName, String filePath) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_HE_FLAG, flag);
        values.put(Constant.COLUMN_HE_NAME, fileName);
        values.put(Constant.COLUMN_HE_FILEPATH, filePath);
        values.put(Constant.COLUMN_HE_TIME, Utils.getTimeWithG());
        db.insert(Constant.TABLE_NAME_HISTORY_EXCEL, null, values);
        db.close();
    }

    public static void insertNotice(Context context, Notice notice) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_N_ID, notice.getNotice_id());
        values.put(Constant.COLUMN_N_TITLE, notice.getNotice_title());
        values.put(Constant.COLUMN_N_CONTEXT, notice.getNotice_context());
        values.put(Constant.COLUMN_N_FROM, notice.getNotice_from());
        values.put(Constant.COLUMN_N_TO, notice.getNotice_to());
        values.put(Constant.COLUMN_N_TIME, notice.getNotice_time());
        values.put(Constant.COLUMN_N_CHECKD, notice.getNotice_check());
        values.put(Constant.COLUMN_N_LNAME, notice.getNotice_lname());
        db.replace(Constant.TABLE_NAME_NOTICE, null, values);
        db.close();
    }


    public static String getRandomKeyId() {
        String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
//        sb.append("M-");
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString().toUpperCase();
    }


    public static String getRandomSNAME() {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("药神用户");
        for (int i = 0; i < 6; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomLname() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getSerect(String lname, String sourceStr) throws Exception {
        sourceStr = "daqin" + sourceStr + "qin.@" + lname;
        sourceStr = getSHA256(sourceStr, "daqinMG.@");
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        result = result.substring(24, 32) + result.substring(8, 16);
        return result.toUpperCase();
    }

    public static String getBase64FromImg(byte[] data) {
        return new String(Base64.getEncoder().encode(data));

    }

    public static byte[] getImgFromBase64(String str) {
        return Base64.getDecoder().decode(str);
    }


    /**
     * @return 获取 yyyy-MM-dd 类型时间
     */
    public static String getDate() {

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);

    }

    public static int[] getMedicinesCount(Context context) {
        long now = Utils.getDateFromString(Utils.getDate());

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select `" + Constant.COLUMN_M_OUTDATE + "` from " + Constant.TABLE_NAME_MEDICINE + " where " + Constant.COLUMN_M_DELFLAG + " = 0 and `" + Constant.COLUMN_M_FROMWEB + "` = 0  ", null);
        int[] res = new int[4];
        int count = 0;
        while (cursor.moveToNext()) {
            count++;
            res[0] = count;
            long out = cursor.getLong(0);
            if (out - now <= 0) {
                res[3]++;
            } else if ((out - now) / 1000 <= 5356800 && out - now > 0) {
                res[2]++;
            } else {
                res[1]++;
            }
        }
        return res;


    }


    /**
     * @param context 上下文
     * @param title   通知标题
     * @param text    通知内容
     * @return 返回Notification
     */
    public static Notification getNotification(Context context, String title, String text) {
        return new Notification.Builder(context, "MedicineGod药神")
                .setContentTitle(title)
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notice)
                .setLargeIcon(Utils.getBitmapFromResourse(context, R.mipmap.icon))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 100, new Intent(context, NoticeActivity.class), 0))
                .build();
    }

    /**
     * @return 获取 yyyy年MM月dd日 HH时mm分ss秒 类型时间
     */
    public static String getTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return dateFormat.format(date);

    }

    /**
     * @return 获取 yyyy-MM-dd HH:mm:ss 类型时间
     */
    public static String getTimeWithG() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);

    }

    /**
     * 将blob转化为byte[],可以转化二进制流的
     *
     * @param blob
     * @return
     */
    public static byte[] blobToBytes(Blob blob) {
        InputStream is = null;
        byte[] b = null;
        try {
            is = blob.getBinaryStream();
            b = new byte[(int) blob.length()];
            is.read(b);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 通过BitmapShader实现圆形边框
     *
     * @param bitmap
     * @param outWidth  输出的图片宽度
     * @param outHeight 输出的图片高度
     * @param radius    圆角大小
     * @param boarder   边框宽度
     */
    public static Bitmap getRoundBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;

        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        //创建输出的bitmap
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //创建着色器
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //给着色器配置matrix
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        //创建矩形区域并且预留出border
        RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
        //把传入的bitmap绘制到圆角矩形区域内
        canvas.drawRoundRect(rect, radius, radius, paint);

        if (boarder > 0) {
            //绘制boarder
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(Color.GREEN);
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(boarder);
            canvas.drawRoundRect(rect, radius, radius, boarderPaint);
        }
        return desBitmap;
    }

//	public static void setImageViewCorner(ImageView v, Bitmap bitmap) {
//		v.setImageBitmap(bitmap);
//		v.setDrawingCacheEnabled(true);
//		v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
//		v.buildDrawingCache();
//		v.setImageBitmap(Utils.getRoundBitmapByShader(Bitmap.createBitmap(v.getDrawingCache()), v.getWidth(), v
//				.getHeight(), 20, 0));
//		v.setDrawingCacheEnabled(false);
//	}


    public static Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = null;
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }
    public static Bitmap getBitmapPress(Bitmap bitmap, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        Log.i("info", "图片大小：" + bit.getByteCount());//2665296  10661184
        return bit;
    }

    public static File setTempFile(Context content) {
        //自定义图片名称
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
        Log.i("file", " name : " + name);
        //定义图片存放的位置
        File tempFile = new File(content.getExternalCacheDir(), name);
        Log.i("file", " tempFile : " + tempFile);
        return tempFile;
    }

    public static boolean isNum(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 绘制条形码
     *
     * @param content       要生成条形码包含的内容
     * @param widthPix      条形码的宽度
     * @param heightPix     条形码的高度
     * @param isShowContent 否则显示条形码包含的内容
     * @return 返回生成条形的位图
     */
    public static Bitmap createBarcode(String content, int widthPix, int heightPix, boolean isShowContent) {
        if (content == null) {
            return null;
        }
        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错级别 这里选择最高H级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            // 图像数据转换，使用了矩阵转换 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
//             下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000; // 黑色
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;// 白色
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (isShowContent) {
                bitmap = showContent(bitmap, content);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 显示条形的内容
     *
     * @param bCBitmap 已生成的条形码的位图
     * @param content  条形码包含的内容
     * @return 返回生成的新位图, 它是 方法{@link # createQRCode(String, int, int, Bitmap)}返回的位图与新绘制文本content的组合
     */
    private static Bitmap showContent(Bitmap bCBitmap, String content) {
        if (content == null || null == bCBitmap) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);//设置填充样式
        paint.setTextSize(20);
//        paint.setTextAlign(Paint.Align.CENTER);
        //测量字符串的宽度
        int textWidth = (int) paint.measureText(content);
        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制字符串矩形区域的高度
        int textHeight = (int) (fm.bottom - fm.top);
        // x 轴的缩放比率
        float scaleRateX = bCBitmap.getWidth() / textWidth;
        paint.setTextScaleX(scaleRateX);
        //绘制文本的基线
        int baseLine = bCBitmap.getHeight() + textHeight;
        //创建一个图层，然后在这个图层上绘制bCBitmap、content
        Bitmap bitmap = Bitmap.createBitmap(bCBitmap.getWidth(), bCBitmap.getHeight() + 2 * textHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas();
        canvas.drawColor(Color.WHITE);
        canvas.setBitmap(bitmap);
        canvas.drawBitmap(bCBitmap, 0, 0, null);
        canvas.drawText(content, bCBitmap.getWidth() / 10, baseLine, paint);
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public static String getUrlEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }

    public static String getUrlDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");

    }

    /**
     * @param imgUrl 网络资源图片路径
     * @return Bitmap
     * 该方法调用时要放在子线程中
     * @netToLoacalBitmap 将网络资源图片转换为Bitmap
     */
    public static Bitmap netToLoacalBitmap(String imgUrl) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(imgUrl).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    public static String sendMailCode(String mailAddress) {
        Random random = new Random();
        String base = "98765432101234567890123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        String mailCode = sb.toString();
        try {
            Properties props = new Properties();
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.transport.protocol", "smtp");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.host", "smtp.163.com");
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constant.FromMail, Constant.FromSecret);
                }
            });
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(Constant.FromMail));
            msg.setSubject("【药神】绑定邮箱验证码");
            msg.setSentDate(new Date());
            System.out.println("mailcode" + mailCode);
            msg.setText("您的验证码是[" + mailCode + "]\n您正在进行邮箱绑定的操作，如非您的操作，请忽略此邮件。\n官方人员不会向您索要任何信息，请勿上当！");
            Transport transport = session.getTransport();
            transport.connect();
            transport.sendMessage(msg, new Address[]{new InternetAddress(mailAddress)});
            transport.close();
            return mailCode;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            System.out.println("send failed, exception: " + mex);
            return "";
        }
    }

//	public static String getMedicineMD5(String keyid, String name) {
//		try {
//			keyid = "MD5-M" + keyid + "KEYID-" + name;
//			// 生成一个MD5加密计算摘要
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			// 计算md5函数
//			md.update(keyid.getBytes());
//			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
//			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
//			//一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
//			String key = new BigInteger(1, md.digest()).toString(16).toUpperCase();
//			return key.substring(0, 8) + key.substring(16, 24);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

    public static String getDatabaseSQL(String lname) {
        return "create table If Not Exists `" + lname + "`(" +
                Constant.COLUMN_M_KEYID + " VARCHAR(20)  NOT NULL PRIMARY KEY," +
                Constant.COLUMN_M_UID + " VARCHAR(20) NOT NULL,`" +
                Constant.COLUMN_M_NAME + "` TEXT NOT NULL," +
                Constant.COLUMN_M_IMAGE + " LONGBLOB NOT NULL, " +
                Constant.COLUMN_M_DESCRIPTION + " LONGTEXT NOT NULL," +
                Constant.COLUMN_M_OUTDATE + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_M_OTC + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_M_BARCODE + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_M_YU + " VARCHAR(20) NOT NULL," +
                Constant.COLUMN_M_ELABEL + " LONGTEXT NOT NULL," +
                Constant.COLUMN_M_LOVE + " INT(2) NOT NULL," +
                Constant.COLUMN_M_SHARE + " LONGTEXT NOT NULL," +
                Constant.COLUMN_M_MUSE + " LONGTEXT NOT NULL," +
                Constant.COLUMN_M_COMPANY + " TEXT NOT NULL," +
                Constant.COLUMN_M_DELFLAG + " INT(2) NOT NULL," +
                Constant.COLUMN_M_SHOWFLAG + " INT(2) NOT NULL," +
                Constant.COLUMN_M_FROMWEB + " INT(2) NOT NULL,`" +
                Constant.COLUMN_M_GROUP + "` VARCHAR(20) NOT NULL" +
//				Constant.COLUMN_M_MD5KEY + " VARCHAR(20) NOT NULL" +
                ")ENGINE = InnoDB";
    }

    public static String getNoticeSQL(String lname) {
        return "create table If Not Exists `" + lname + "NOTICE`(`" +
                Constant.COLUMN_N_ID + "` int(20)  NOT NULL PRIMARY KEY AUTO_INCREMENT,`" +
                Constant.COLUMN_N_TITLE + "` VARCHAR(100) NOT NULL,`" +
                Constant.COLUMN_N_CONTEXT + "` VARCHAR(255) NOT NULL,`" +
                Constant.COLUMN_N_FROM + "` VARCHAR(100) NOT NULL,`" +
                Constant.COLUMN_N_TO + "` VARCHAR(100) NOT NULL,`" +
                Constant.COLUMN_N_TIME + "` VARCHAR(20) NOT NULL,`" +
                Constant.COLUMN_N_CHECKD + "` INT(2) NOT NULL " +
                ")ENGINE = InnoDB AUTO_INCREMENT=100000";
    }

    public static String getStartSQL(String lname, String sname) {
        return "replace into `" + lname + "NOTICE`(`" +
                Constant.COLUMN_N_TITLE + "`,`" +
                Constant.COLUMN_N_CONTEXT + "`,`" +
                Constant.COLUMN_N_FROM + "`,`" +
                Constant.COLUMN_N_TO + "`,`" +
                Constant.COLUMN_N_TIME + "`,`" +
                Constant.COLUMN_N_CHECKD + "`)values('" +
                sname + "，欢迎来到药神！','" +
                "     药神是一款药品管理服务类APP，通过不定量输入（导入）自己的药品，对加入的药品实现可视化管理，具有过期提醒、用法用量等多种功能。\n     还可以通过自带校园内部社区发布闲置、借药等需求信息等其他功能。\n     现在，您可以自行探索，有其他疑问请咨询在线客服！','" +
                "100000','" +
                lname + "','" +
                Utils.getTimeWithG() + "','0')";
    }

    public static String[] getMedicineColumn_cn() {
        return new String[]{
//                Constant.COLUMN_M_KEYID_CN
//                , Constant.COLUMN_M_UID_CN
                Constant.COLUMN_M_NAME_CN
                , Constant.COLUMN_M_IMAGE_CN
                , Constant.COLUMN_M_DESCRIPTION_CN
                , Constant.COLUMN_M_OUTDATE_CN
                , Constant.COLUMN_M_OTC_CN
                , Constant.COLUMN_M_BARCODE_CN
                , Constant.COLUMN_M_YU_CN
                , Constant.COLUMN_M_ELABEL_CN
//                , Constant.COLUMN_M_LOVE_CN
//                , Constant.COLUMN_M_SHARE_CN
                , Constant.COLUMN_M_MUSE_CN
                , Constant.COLUMN_M_COMPANY_CN
//                , Constant.COLUMN_M_DELFLAG_CN
//                , Constant.COLUMN_M_SHOWFLAG_CN
//                , Constant.COLUMN_M_FROMWEB_CN
                , Constant.COLUMN_M_GROUP_CN};
    }

    public static String[] getMedicineColumn_origin() {
        return new String[]{
//                Constant.COLUMN_M_KEYID
//                , Constant.COLUMN_M_UID
                Constant.COLUMN_M_NAME
                , Constant.COLUMN_M_IMAGE
                , Constant.COLUMN_M_DESCRIPTION
                , Constant.COLUMN_M_OUTDATE
                , Constant.COLUMN_M_OTC
                , Constant.COLUMN_M_BARCODE
                , Constant.COLUMN_M_YU
                , Constant.COLUMN_M_ELABEL
//                , Constant.COLUMN_M_LOVE
//                , Constant.COLUMN_M_SHARE
                , Constant.COLUMN_M_MUSE
                , Constant.COLUMN_M_COMPANY
//                , Constant.COLUMN_M_DELFLAG
//                , Constant.COLUMN_M_SHOWFLAG
//                , Constant.COLUMN_M_FROMWEB
                , Constant.COLUMN_M_GROUP};
    }

    private static float scale;

    public static int dp2px(Context context, float dpValue) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */

    public static int px2dp(Context context, float pxValue) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();

    }


    /**
     * 显示正在过程化的对话框
     */
    public static void showProgressDialog(Context context, String title, String msg, int max, int rise) {
        final ProgressDialog dialog = new ProgressDialog(context);
        final Timer timer = new Timer();
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(msg);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog dialoga = new AlertDialog.Builder(context)
                        .setTitle("提示：")
                        .setCancelable(false)
                        .setMessage("真的要取消吗？(后台仍在进行中)")
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timer.cancel();
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.show();

                            }
                        })
                        .create();
                dialoga.show();
            }
        });
        dialog.setMax(max);

        timer.schedule(new TimerTask() {
            int progress = 0;

            @Override
            public void run() {
                dialog.setProgress(progress += rise);
                if (progress > max) {
                    timer.cancel();
                    dialog.dismiss();
                }
            }
        }, 0, 1000);

        dialog.show();


    }

}



