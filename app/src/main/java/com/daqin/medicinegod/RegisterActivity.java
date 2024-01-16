package com.daqin.medicinegod;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityRegisterBinding;
import com.daqin.medicinegod.entity.Notice;
import com.mysql.jdbc.Util;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    View root;
    static String mailCode = "xxxxxx";
    final int REQUEST_CODE_CHOOSE_IMG = 100;
    final int REQUEST_CODE_CROP = 102;
    Uri uripath = null;
    int status_head = 0;
    final DatabaseHelper dbHelper = new DatabaseHelper(RegisterActivity.this);
    boolean flag = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

//        ImageButton id_register_head = binding.idRegisterHead;
        RoundImageView id_register_head = binding.idRegisterHead;

        id_register_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                //intent.setType("image/*");
////                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
            }
        });
        ImageButton id_register_head_crop = binding.idRegisterHeadCrop;
        id_register_head_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageCrop(uripath);
            }
        });
        Button id_register_tologin = binding.idRegisterTologin;
        id_register_tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        EditText id_register_lname = binding.idRegisterLname;
        id_register_lname.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }});

        id_register_lname.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // edit.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = id_register_lname.getCompoundDrawables()[2];
                // 右边有图片
                if (drawable != null) {
                    // 是点击事件
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // 手指按下位置在图片内
                        if (event.getX() > id_register_lname.getWidth() - id_register_lname.getPaddingRight() - drawable.getIntrinsicWidth()) {
                            id_register_lname.setText(Utils.getRandomLname());
                        }
                    }
                }
                return false;
            }
        });
        ImageButton id_register_head_default = binding.idRegisterHeadDefault;
        id_register_head_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status_head) {
                    case 0:
                        id_register_head.setImageResource(R.mipmap.me_woman_default);
                        id_register_head_default.setImageResource(R.mipmap.me_man_default);
                        status_head = 1;
                        break;
                    case 1:
                        id_register_head.setImageResource(R.mipmap.me_man_default);
                        id_register_head_default.setImageResource(R.mipmap.me_woman_default);
                        status_head = 0;
                        break;
                    case 2:
                        id_register_head.setImageResource(R.mipmap.me_man_default);
                        id_register_head_default.setImageResource(R.mipmap.me_woman_default);
                        status_head = 0;
                        uripath = null;
                        break;
                }
            }
        });


        EditText id_register_email = binding.idRegisterEmail;
        id_register_email.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.toString(source.charAt(i)).equals("_")
                            && !Character.toString(source.charAt(i)).equals("@")
                            && !Character.toString(source.charAt(i)).equals(".")) {
                        return "";
                    }
                }
                return null;
            }
        }});
        id_register_email.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // edit.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = id_register_email.getCompoundDrawables()[2];
                // 右边有图片
                if (drawable != null) {
                    // 是点击事件
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // 手指按下位置在图片内
                        if (event.getX() > id_register_email.getWidth() - id_register_email.getPaddingRight() - drawable.getIntrinsicWidth()) {
                            id_register_email.setText("");
                        }
                    }
                }
                return false;
            }
        });

        Button id_register_send = binding.idRegisterSend;
        id_register_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_register_send.setEnabled(false);
                String mail = id_register_email.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection conn = JdbcUtil.getConnection();
                        PreparedStatement pps = null;
                        ResultSet resultSet = null;
                        try {
                            pps = conn.prepareStatement("SELECT MAIL FROM USERINFO WHERE MAIL = ?");
                            pps.setString(1, mail);
                            resultSet = pps.executeQuery();
                            if (resultSet.next()) {
                                Looper.prepare();
                                id_register_send.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        id_register_send.setEnabled(true);
                                    }
                                });
                                AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("发生错误:")
                                        .setMessage("邮件已存在，请更换/找回！")
                                        .setPositiveButton("好", null)
                                        .create();
                                dialog.show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Matcher matcher = Constant.FromMailPattern.matcher(mail);
                                if (matcher.matches()) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mailCode = Utils.sendMailCode(mail);
                                            if (mailCode.equals("")) {
                                                AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                                        .setTitle("发生错误:")
                                                        .setMessage("邮件发送失败，请重试！")
                                                        .setPositiveButton("好", null)
                                                        .create();
                                                dialog.show();
                                                id_register_send.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        id_register_send.setEnabled(true);
                                                    }
                                                });
                                            } else {
                                                Timer timer = new Timer();
                                                timer.schedule(new TimerTask() {
                                                    int countdown = 60;

                                                    @Override
                                                    public void run() {
                                                        id_register_send.post(new Runnable() {
                                                            @SuppressLint("SetTextI18n")
                                                            @Override
                                                            public void run() {
                                                                if (countdown < 2) {
                                                                    id_register_send.setText("获取验证码");
                                                                    id_register_send.setTextColor(Color.rgb(159, 209, 161));
                                                                    id_register_send.setEnabled(true);
                                                                    timer.cancel();
                                                                } else {
                                                                    countdown--;
                                                                    id_register_send.setText("请等待" + countdown + "秒");
                                                                    id_register_send.setTextColor(Color.rgb(100, 100, 100));
                                                                }
                                                            }
                                                        });
                                                    }
                                                }, 0, 1000);
                                            }
                                        }
                                    }).start();
                                    Looper.loop();

                                } else {
                                    flag = false;
                                    mailCode = "xxxxxx";
                                    id_register_send.setEnabled(true);
                                    Looper.prepare();
                                    AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                            .setTitle("发生错误:")
                                            .setMessage("邮箱格式不正确！")
                                            .setPositiveButton("好", null)
                                            .create();
                                    dialog.show();
                                    Looper.loop();
                                }
                            }
                        } catch (SQLException throwables) {
                            flag = false;
                            mailCode = "xxxxxx";
                            id_register_send.setEnabled(true);
                            throwables.printStackTrace();
                        } finally {
                            JdbcUtil.close(conn, null, pps, resultSet);
                        }
                    }
                }).start();


            }
        });
        EditText id_register_pwd = binding.idRegisterPwd;
        EditText id_register_emailcode = binding.idRegisterEmailcode;
        id_register_pwd.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.toString(source.charAt(i)).equals("_")
                            && !Character.toString(source.charAt(i)).equals("@")
                            && !Character.toString(source.charAt(i)).equals(".")) {
                        return "";
                    }
                }
                return null;
            }
        }});
        id_register_emailcode.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }});
        Button id_register_register = binding.idRegisterRegister;
        id_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {

                    InputMethodManager imm = (InputMethodManager) RegisterActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(RegisterActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    }

                    if (id_register_lname.getText().toString().trim().length() == 0 ||
                            id_register_lname.getText().toString().trim().length() < 6
                            || id_register_lname.getText().toString().trim().length() > 12) {
                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("发生错误:")
                                .setMessage("登录名格式错误！\n格式:6-12位中英文字符")
                                .setPositiveButton("好", null)
                                .create();
                        dialog.show();
                    } else if (!(id_register_emailcode.getText().toString().trim().toUpperCase().equals(mailCode)) ||
                            id_register_emailcode.getText().toString().length() == 0) {
                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("发生错误:")
                                .setMessage("邮箱验证码错误！\n格式:6位数字")
                                .setPositiveButton("好", null)
                                .create();
                        dialog.show();
                    } else if (id_register_pwd.getText().toString().trim().length() == 0 ||
                            id_register_pwd.getText().toString().trim().length() < 6
                            || id_register_pwd.getText().toString().trim().length() > 18) {
                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("发生错误:")
                                .setMessage("密码格式错误！\n格式:6-18位密码，允许_ . @")
                                .setPositiveButton("好", null)
                                .create();
                        dialog.show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Connection conn = JdbcUtil.getConnection();
                                PreparedStatement pps = null;
                                ResultSet resultSet = null;
                                try {
                                    conn.setAutoCommit(false);


                                    pps = conn.prepareStatement("SELECT LNAME FROM USERINFO WHERE LNAME = ?");
                                    pps.setString(1, id_register_lname.getText().toString().trim());
                                    resultSet = pps.executeQuery();
                                    if (resultSet.next()) {
                                        Looper.prepare();
                                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                                .setTitle("发生错误:")
                                                .setMessage("登录名已存在，请更换新用户名！\n如果您是刚刚注册，请尝试直接登录！如果有问题请联系管理员重新注册！")
                                                .setPositiveButton("好", null)
                                                .create();
                                        dialog.show();
                                        Looper.loop();
                                    } else {
                                        String lname = id_register_lname.getText().toString().trim();
                                        String pwd = id_register_pwd.getText().toString().trim();
                                        String mail = id_register_email.getText().toString().trim();
                                        Bitmap img = Utils.getBitmapFromView(id_register_head);
                                        Calendar cl = Calendar.getInstance();
                                        String today = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + cl.get(Calendar.DAY_OF_MONTH);
                                        long rgtime = Utils.getDateFromString(today);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Connection connection = null;
                                                PreparedStatement pps = null;

                                                byte[] head = Utils.getBytesFromBitmap(img);
                                                String sname = Utils.getRandomSNAME();
                                                try {
                                                    String date = Utils.getDate();
                                                    connection = JdbcUtil.getConnection();
                                                    connection.setAutoCommit(false);
                                                    String serc = Utils.getSerect(lname, pwd);
                                                    pps = connection.prepareStatement("INSERT INTO USERINFO (LNAME,SNAME,PWD,HEAD,FRIEND,MAIL,RGTIME,ONLINE,POINT,POINTHISTORY,CLOUDYU,VIP,VIPYU,SIGNIN,MYSTYLE)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                                                    pps.setString(1, lname);
                                                    pps.setString(2, sname);
                                                    pps.setString(3, serc);
                                                    pps.setBytes(4, head);
                                                    pps.setString(5, "{\"count\":\"0\",\"friendlist\":{}}");
                                                    pps.setString(6, mail);
                                                    pps.setLong(7, rgtime);
                                                    pps.setInt(8, 0);
                                                    pps.setInt(9, 100);
                                                    pps.setString(10, "新用户首次登陆|100|" + date);
                                                    pps.setInt(11, 150);
                                                    pps.setInt(12, 0);
                                                    pps.setInt(13, 0);
                                                    pps.setString(14, date);
                                                    pps.setString(15, "用一句话介绍一下自己吧~");
                                                    pps.executeUpdate();
                                                    pps = connection.prepareStatement(Utils.getDatabaseSQL(lname));
                                                    pps.executeUpdate();
                                                    pps = connection.prepareStatement(Utils.getNoticeSQL(lname));
                                                    pps.executeUpdate();
                                                    pps = connection.prepareStatement(Utils.getStartSQL(lname, sname));
                                                    pps.executeUpdate();
                                                    connection.commit();

                                                    Utils.insertNotice(
                                                            RegisterActivity.this,
                                                            new Notice(100000,
                                                                    sname + "，欢迎来到药神！",
                                                                    "     药神是一款药品管理服务类APP，通过不定量输入（导入）自己的药品，对加入的药品实现可视化管理，具有过期提醒、用法用量等多种功能。\n     还可以通过自带校园内部社区发布闲置、借药等需求信息等其他功能。\n     现在，您可以自行探索，有其他疑问请咨询在线客服！",
                                                                    "100000",
                                                                    lname,
                                                                    date,
                                                                    "0",
                                                                    lname));


                                                    //数据插入本地
                                                    Utils.putInt(RegisterActivity.this, Constant.MG_LOGIN, 1);
                                                    Utils.putString(RegisterActivity.this, Constant.MG_LOGIN_LNAME, lname);
                                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                    ContentValues values = new ContentValues();
                                                    values.put(Constant.COLUMN_U_LNAME, lname);
                                                    values.put(Constant.COLUMN_U_SNAME, sname);
                                                    values.put(Constant.COLUMN_U_PWD, serc);
                                                    values.put(Constant.COLUMN_U_HEAD, head);
                                                    values.put(Constant.COLUMN_U_FRIEND, "{\"count\":\"0\",\"friendlist\":{}}");
                                                    values.put(Constant.COLUMN_U_PHONE, "");
                                                    values.put(Constant.COLUMN_U_MAIL, mail);
                                                    values.put(Constant.COLUMN_U_RGTIME, rgtime);
                                                    values.put(Constant.COLUMN_U_ONLINE, 0);
                                                    values.put(Constant.COLUMN_U_POINT, 100);
                                                    values.put(Constant.COLUMN_U_MYSTYLE, "用一句话介绍一下自己吧~");
                                                    values.put(Constant.COLUMN_U_POINTHISTORY, "新用户首次登陆|100|" + date);
                                                    values.put(Constant.COLUMN_U_VIP, 0);
                                                    values.put(Constant.COLUMN_U_VIPYU, 0L);
                                                    values.put(Constant.COLUMN_U_CLOUDYU, 150);
                                                    values.put(Constant.COLUMN_U_SIGNIN, date);
                                                    long status_inset = db.replace(Constant.TABLE_NAME_USER, null, values);
                                                    if (status_inset >= 1) {
                                                        Looper.prepare();
                                                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                                                .setTitle("完成:")
                                                                .setMessage("注册成功")
                                                                .setPositiveButton("进入药神", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        finish();
                                                                    }
                                                                })
                                                                .create();
                                                        dialog.setCancelable(false);
                                                        dialog.show();
                                                        Log.d(Constant.TABLE_NAME_USER, "insert successful");
                                                        Looper.loop();
                                                    } else {
                                                        db.close();
                                                        Utils.putInt(RegisterActivity.this, Constant.MG_LOGIN, 0);
                                                        Looper.prepare();
                                                        AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                                                .setTitle("错误:")
                                                                .setMessage("注册成功，但出现未知错误，请直接登陆")
                                                                .setPositiveButton("进入药神", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        finish();
                                                                    }
                                                                })
                                                                .create();
                                                        dialog.setCancelable(false);
                                                        dialog.show();
                                                        Log.d(Constant.TABLE_NAME_USER, "insert error");
                                                        Looper.loop();
                                                    }
                                                } catch (Exception throwables) {
                                                    flag = false;

                                                    if (connection != null) {
                                                        //事务回滚，取消当前事务
                                                        try {
                                                            connection.rollback();
                                                        } catch (SQLException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    throwables.printStackTrace();
                                                    Looper.prepare();
                                                    AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                                            .setTitle("发生错误:")
                                                            .setMessage("网络错误，请重试")
                                                            .setPositiveButton("好", null)
                                                            .create();
                                                    dialog.show();
                                                    Looper.loop();
                                                } finally {
                                                    JdbcUtil.close(connection, null, pps, null);
                                                }
                                            }

                                        }).start();
                                    }
                                } catch (SQLException throwables) {
                                    flag = false;
                                    try {
                                        //事务回滚，取消当前事务
                                        conn.rollback();
                                    } catch (SQLException e) {
                                        flag = false;
                                        e.printStackTrace();
                                    }
                                    Looper.prepare();
                                    AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                                            .setTitle("发生错误:")
                                            .setMessage("网络错误，请重试")
                                            .setPositiveButton("好", null)
                                            .create();
                                    dialog.show();
                                    Looper.loop();
                                    throwables.printStackTrace();
                                } finally {
                                    flag = false;
                                    JdbcUtil.close(conn, null, pps, resultSet);
                                }
                            }
                        }).start();
                    }
                }

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RoundImageView b_chooseImg = root.findViewById(R.id.id_register_head);
        ImageButton id_register_head_default = binding.idRegisterHeadDefault;
        if (requestCode == REQUEST_CODE_CHOOSE_IMG) {//判断是不是我们选择图片按钮的回调
            if (resultCode == Activity.RESULT_OK && null != data) {
                try {
                    uripath = data.getData();
//                    b_chooseImg.setImageURI(uri);
                    ContentResolver cr = this.getContentResolver();
//                System.out.println(Utils.getBase64(cr,uri));
                    Bitmap img = BitmapFactory.decodeStream(cr.openInputStream(uripath));
                    b_chooseImg.setImageBitmap(img);
                    status_head = 2;
                    id_register_head_default.setImageResource(R.mipmap.me_man_default);
                } catch (Exception e) {
                    uripath = null;
                    Toast.makeText(RegisterActivity.this, "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap img = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    b_chooseImg.setImageBitmap(img);
                    status_head = 2;
                    id_register_head_default.setImageResource(R.mipmap.me_man_default);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "裁剪图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    //剪切图片
    //originUri--原始图片的Uri；
    //mDestinationUri--目标裁剪的图片保存的Uri
    private void startImageCrop(Uri uri) {
        if (uri == null) {
            AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("发生错误:")
                    .setMessage("暂无图片，请选择图片")
                    .setPositiveButton("好", null)
                    .create();
            dialog.show();
            return;
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setMaxBitmapSize(100);
        options.setMaxScaleMultiplier(6);
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "MgCropImage.jpeg"));
        UCrop.of(uri, mDestinationUri)
                .withOptions(options)
                .useSourceImageAspectRatio()
                .withAspectRatio(1, 1)
                .start(this, REQUEST_CODE_CROP);
    }
}

