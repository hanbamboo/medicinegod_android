package com.daqin.medicinegod;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.ExcelUtils;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityAddBinding;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.zlylib.fileselectorlib.FileSelector;
import com.zlylib.fileselectorlib.utils.Const;
import com.zlylib.fileselectorlib.bean.EssFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class AddActivity extends AppCompatActivity {
    private ActivityAddBinding binding;
    View root;
    long outDate = 0L;
    final int REQUEST_CODE_CHOOSE_IMG = 100;
    final int REQUEST_CODE_CHOOSE_FILE = 103;
    final int REQUEST_CODE_SCAN = 101;
    final int REQUEST_CODE_CROP = 102;


    Uri imgUri = null;
    Bitmap img = null;
    boolean img_default = false;
    String otc = "";
    String group = Constant.COLUMN_G_DEFAULT;
    int usage_1_util = 0, usage_3_util = 0;
    ArrayList<String> elabel = new ArrayList<>();
    final DatabaseHelper dbHelper = new DatabaseHelper(AddActivity.this);
    View dialogV = null;

    List<String> group_list = new ArrayList<>();
    String[] group_list_z = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group_list.clear();
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Set<String> set_tmp = Utils.getSet(this, Constant.GROUPLISTKEY, null);
        if (set_tmp == null || set_tmp.size() == 0) {
            group_list = new ArrayList<>();
            group_list.add(Constant.COLUMN_G_ADD);
            group_list.add(Constant.COLUMN_G_DEFAULT);
        } else {
            group_list.add(Constant.COLUMN_G_ADD);
            group_list.addAll(set_tmp);
        }
        if (Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT) == -1) {
            group_list.add(group_list.size(), Constant.COLUMN_G_DEFAULT);
        }


        group_list_z = new String[group_list.size()];
        group_list.toArray(group_list_z);
        TextView t_usage_1_util = root.findViewById(R.id.id_add_usage_1_util);
        TextView t_usage_2_util = root.findViewById(R.id.id_add_usage_2_util);
        TextView t_usage_3_util = root.findViewById(R.id.id_add_usage_3_util);
        Spinner s_group = root.findViewById(R.id.id_add_group);

        ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(this, R.layout.spanner_style_b, group_list_z);
        //建立Adapter并且绑定数据源
        //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
        s_group.setAdapter(adapter_group);//绑定Adapter到控件
        s_group.setSelection(Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT));

        s_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (group_list_z[i].equals(Constant.COLUMN_G_ADD)) {
                    s_group.setSelection(Utils.findIndexInList(group_list, Constant.COLUMN_G_DEFAULT));


                    dialogV = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_medicine_addgroup, null, false);
                    final AlertDialog dialoggg = new AlertDialog.Builder(AddActivity.this).setView(dialogV).create();


                    TextView id_dialog_medicine_ag_title = dialogV.findViewById(R.id.id_dialog_medicine_ag_title);
                    EditText id_dialog_medicine_ag_text = dialogV.findViewById(R.id.id_dialog_medicine_ag_text);
                    Button id_dialog_medicine_ag_ok = dialogV.findViewById(R.id.id_dialog_medicine_ag_ok);
                    Button id_dialog_medicine_ag_back = dialogV.findViewById(R.id.id_dialog_medicine_ag_back);


                    id_dialog_medicine_ag_text.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            id_dialog_medicine_ag_title.setText("请填写您想要分组名(" + s.length() + "/5)");
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    id_dialog_medicine_ag_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            5)});
                    id_dialog_medicine_ag_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
                                    .setTitle("提示:")
                                    .setMessage("确认要关闭吗？")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoga, int which) {
                                            dialoggg.dismiss();
                                        }
                                    })
                                    .setNegativeButton("返回", null)
                                    .create();
                            tips.show();

                        }
                    });


                    id_dialog_medicine_ag_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String str = id_dialog_medicine_ag_text.getText().toString().trim();

                            if (str.length() == 0) {
                                Toast.makeText(AddActivity.this, "分组名不能为空", Toast.LENGTH_SHORT).show();
                            } else {


                                group_list.add(1, str);
                                group_list_z = new String[group_list.size()];
                                group_list.toArray(group_list_z);

                                ArrayAdapter<String> adapter_group = new ArrayAdapter<String>(AddActivity.this, R.layout.spanner_style_b, group_list_z);
                                //建立Adapter并且绑定数据源
                                //第一个参数表示在哪个Activity上显示，第二个参数是系统下拉框的样式，第三个参数是数组。
                                s_group.setAdapter(adapter_group);//绑定Adapter到控件
                                s_group.setSelection(Utils.findIndexInList(group_list, str));
                                if (dialoggg != null) {
                                    dialoggg.dismiss();
                                }


                            }
                        }
                    });

                    //清空背景黑框
                    dialoggg.getWindow().getDecorView().setBackground(null);
                    dialoggg.setCancelable(false);
                    dialoggg.show();

                    //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                    dialoggg.getWindow().setLayout((Utils.getScreenWidth(AddActivity.this) / 4 * 3), Utils.dp2px(AddActivity.this, 400));


                } else {
                    group = s_group.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button b_outDateBtn = root.findViewById(R.id.id_add_outdate_btn);
        Button b_scanBarcode = root.findViewById(R.id.id_add_barcode_scan);


        Button b_img_default = root.findViewById(R.id.id_add_img_btn_usedefault);
        Button b_img_clear = root.findViewById(R.id.id_add_img_btn_clear);
        Button b_img_crop = root.findViewById(R.id.id_add_img_btn_cropimg);
        EditText t_desp = root.findViewById(R.id.id_add_desp);
        EditText t_elabelBox = root.findViewById(R.id.id_add_elabel_box);
        EditText t_name = root.findViewById(R.id.id_add_name);
        EditText t_barcode = root.findViewById(R.id.id_add_barcode);
        EditText t_company = root.findViewById(R.id.id_add_company);
        EditText t_usage_1 = root.findViewById(R.id.id_add_usage_1);
        EditText t_usage_2 = root.findViewById(R.id.id_add_usage_2);
        EditText t_usage_3 = root.findViewById(R.id.id_add_usage_3);
        EditText t_yu = root.findViewById(R.id.id_add_yu);
        Button b_addElabel = root.findViewById(R.id.id_add_elabel_add);
        Button b_ok = root.findViewById(R.id.id_add_ok);
        Button b_elabel1 = root.findViewById(R.id.id_add_elabel1);
        Button b_elabel2 = root.findViewById(R.id.id_add_elabel2);
        Button b_elabel3 = root.findViewById(R.id.id_add_elabel3);
        Button b_elabel4 = root.findViewById(R.id.id_add_elabel4);
        Button b_elabel5 = root.findViewById(R.id.id_add_elabel5);
        Button[] btn_elabel = new Button[]{b_elabel1, b_elabel2, b_elabel3, b_elabel4, b_elabel5};
        RadioButton b_otcr = root.findViewById(R.id.idadd_otc_otcr);
        RadioButton b_otcg = root.findViewById(R.id.idadd_otc_otcg);
        RadioButton b_none = root.findViewById(R.id.idadd_otc_none);
        RadioButton b_rx = root.findViewById(R.id.idadd_otc_rx);

        FlowLayout elabel_layout = root.findViewById(R.id.id_add_elbaelLayout);
        RoundImageView b_chooseImg = root.findViewById(R.id.id_add_img_btn);
        ScrollView scrollView = root.findViewById(R.id.id_add_scrollView);
        for (Button btn : btn_elabel) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = (String) btn.getText();
                    AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
                            .setTitle("提示:")
                            .setMessage("确认删除" + str + "吗")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    elabel.remove(str);
                                    for (Button btn : btn_elabel) {
                                        btn.setVisibility(View.GONE);
                                        btn.setText("");
                                    }
                                    for (int i = 0; i < elabel.size(); i++) {
                                        btn_elabel[i].setText(elabel.get(i));
                                        btn_elabel[i].setVisibility(View.VISIBLE);
                                    }
                                    System.out.println(elabel.toString());
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    tips.show();
                }
            });
        }
        b_addElabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t_elabelBox.getText() != null && !t_elabelBox.getText().toString().trim().equals("")) {
                    String str = t_elabelBox.getText().toString().trim();
                    if (elabel.size() < 5) {
                        if (!elabel.contains(str)) {
                            t_elabelBox.setText("");
                            elabel.add(str);
                            elabel_layout.setVisibility(View.VISIBLE);
                            for (Button btn : btn_elabel) {
                                if (btn.getText().equals("")) {
                                    btn.setText(str);
                                    btn.setVisibility(View.VISIBLE);
                                    System.out.println(elabel.toString());
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(AddActivity.this, "此标签已存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddActivity.this, "数量已达上限(5)", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(AddActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
        TextView id_add_yu_title = binding.idAddYuTitle;
        t_usage_1_util.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usage_1_util++;
                switch (usage_1_util) {
                    case 0:
                        t_usage_1_util.setText("包");
                        id_add_yu_title.setText("药品余量(单位:包)");
                        break;
                    case 1:
                        t_usage_1_util.setText("克");
                        id_add_yu_title.setText("药品余量(单位:克)");
                        break;
                    case 2:
                        t_usage_1_util.setText("片");
                        id_add_yu_title.setText("药品余量(单位:片)");
                        usage_1_util = -1;
                        break;
                }
            }
        });
        t_usage_1_util.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogV = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                final AlertDialog dialogu1 = new AlertDialog.Builder(AddActivity.this).setView(dialogV).create();


                TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        5)});
                id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
                                .setTitle("提示:")
                                .setMessage("确认要关闭吗？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialoga, int which) {
                                        dialogu1.dismiss();
                                    }
                                })
                                .setNegativeButton("返回", null)
                                .create();
                        tips.show();

                    }
                });


                id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = id_dialog_medicine_au_text.getText().toString().trim();
                        if (str.length() == 0) {
                            Toast.makeText(AddActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            t_usage_1_util.setText(str);
                            id_add_yu_title.setText("药品余量(单位:" + str + ")");
                            dialogu1.dismiss();


                        }
                    }
                });

                //清空背景黑框
                dialogu1.getWindow().getDecorView().setBackground(null);
                dialogu1.setCancelable(false);
                dialogu1.show();

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialogu1.getWindow().setLayout((Utils.getScreenWidth(AddActivity.this) / 4 * 3), Utils.dp2px(AddActivity.this, 400));


                return false;
            }
        });
        t_usage_2_util.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogV = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                final AlertDialog dialogu2 = new AlertDialog.Builder(AddActivity.this).setView(dialogV).create();


                TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        5)});
                id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
                                .setTitle("提示:")
                                .setMessage("确认要关闭吗？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialoga, int which) {
                                        dialogu2.dismiss();
                                    }
                                })
                                .setNegativeButton("返回", null)
                                .create();
                        tips.show();

                    }
                });


                id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = id_dialog_medicine_au_text.getText().toString().trim();
                        if (str.length() == 0) {
                            Toast.makeText(AddActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            t_usage_2_util.setText(str);
                            dialogu2.dismiss();

                        }
                    }
                });

                //清空背景黑框
                dialogu2.getWindow().getDecorView().setBackground(null);
                dialogu2.setCancelable(false);
                dialogu2.show();

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialogu2.getWindow().setLayout((Utils.getScreenWidth(AddActivity.this) / 4 * 3), Utils.dp2px(AddActivity.this, 400));


                return false;
            }
        });


        t_usage_3_util.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usage_3_util++;
                switch (usage_3_util) {
                    case 0:
                        t_usage_3_util.setText("时");
                        break;
                    case 1:
                        t_usage_3_util.setText("天");
                        usage_3_util = -1;
                        break;
                }
            }
        });
        t_usage_3_util.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogV = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_medicine_addusage, null, false);
                final AlertDialog dialogu3 = new AlertDialog.Builder(AddActivity.this).setView(dialogV).create();


                TextView id_dialog_medicine_au_title = dialogV.findViewById(R.id.id_dialog_medicine_au_title);
                EditText id_dialog_medicine_au_text = dialogV.findViewById(R.id.id_dialog_medicine_au_text);
                Button id_dialog_medicine_au_ok = dialogV.findViewById(R.id.id_dialog_medicine_au_ok);
                Button id_dialog_medicine_au_back = dialogV.findViewById(R.id.id_dialog_medicine_au_back);


                id_dialog_medicine_au_text.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        id_dialog_medicine_au_title.setText("请填写您需要的单位名(" + s.length() + "/5)");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                id_dialog_medicine_au_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        5)});
                id_dialog_medicine_au_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
                                .setTitle("提示:")
                                .setMessage("确认要关闭吗？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialoga, int which) {
                                        dialogu3.dismiss();
                                    }
                                })
                                .setNegativeButton("返回", null)
                                .create();
                        tips.show();

                    }
                });


                id_dialog_medicine_au_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String str = id_dialog_medicine_au_text.getText().toString().trim();
                        if (str.length() == 0) {
                            Toast.makeText(AddActivity.this, "单位名不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            t_usage_3_util.setText(str);
                            dialogu3.dismiss();
                        }
                    }
                });

                //清空背景黑框
                dialogu3.getWindow().getDecorView().setBackground(null);
                dialogu3.setCancelable(false);
                dialogu3.show();

                //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的9/10 注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
                dialogu3.getWindow().setLayout((Utils.getScreenWidth(AddActivity.this) / 4 * 3), Utils.dp2px(AddActivity.this, 400));


                return false;
            }
        });


        b_otcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otc = "OTC-R";
            }
        });
        b_otcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otc = "OTC-G";
            }
        });
        b_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otc = "NONE";
            }
        });
        b_rx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otc = "RX";
            }
        });

        b_scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA},
                            1);
                }//这一块红色的是开启手机里的相机权限，安卓6.0以后的系统需要，否则会报错
                else {
                    Intent intent = new Intent(AddActivity.this, CaptureActivity.class);//黄色是第三方类库里面的类
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }

            }
        });

        b_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
            }
        });

        b_outDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date myDate = new Date();
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTime(myDate);
                DatePickerDialog dialogdate = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        outDate = Utils.getDateFromString(year + "-" + ((month + 1) > 10 ? (month + 1) : "0" + (month + 1)) + "-" + ((dayOfMonth > 10) ? dayOfMonth : "0" + dayOfMonth));
                        b_outDateBtn.setText("已选：" + year + "年" + ((month + 1) > 10 ? (month + 1) : "0" + (month + 1)) + "月" + dayOfMonth + "日");
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialogdate.show();
            }
        });
        b_img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.recycle();
                img = null;
                b_chooseImg.setImageResource(R.mipmap.add_imgadd);
                b_chooseImg.setScaleType(RoundImageView.ScaleType.FIT_XY);
                img_default = false;
                imgUri = null;
            }
        });
        b_img_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!img_default) {
                    b_chooseImg.setImageResource(R.mipmap.add_imgdefault);
                    b_chooseImg.setDrawingCacheEnabled(true);
                    b_chooseImg.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    b_chooseImg.layout(0, 0, b_chooseImg.getMeasuredWidth(), b_chooseImg.getMeasuredHeight());
                    b_chooseImg.buildDrawingCache();
                    b_chooseImg.setScaleType(RoundImageView.ScaleType.FIT_XY);
                    img = Bitmap.createBitmap(b_chooseImg.getDrawingCache());
                    img_default = true;
                    b_chooseImg.setDrawingCacheEnabled(false);
                }

            }
        });
        b_img_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!img_default) {
                    b_chooseImg.setScaleType(RoundImageView.ScaleType.FIT_XY);
                    startImageCrop(imgUri);
                } else {
                    Toast.makeText(AddActivity.this, "默认图片无法裁剪", Toast.LENGTH_SHORT).show();
                }

            }
        });
        b_ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (t_name.getText().length() == 0 || t_name.getText().toString().trim().equals("")) {
                    scrollView.fling(0);
                    Toast.makeText(AddActivity.this, "未填写药品名", Toast.LENGTH_SHORT).show();
                } else if (img == null) {
                    scrollView.fling(b_chooseImg.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写图片，可设置默认图片", Toast.LENGTH_SHORT).show();
                } else if (t_desp.getText().length() == 0 || t_desp.getText().toString().trim().equals("")) {
                    scrollView.fling(t_desp.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写药品描述", Toast.LENGTH_SHORT).show();
                } else if (outDate == 0L) {
                    scrollView.fling(t_desp.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未选择过期时效", Toast.LENGTH_SHORT).show();
                } else if (otc == null || otc.equals("")) {
                    scrollView.fling(b_otcr.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未选择药品标识", Toast.LENGTH_SHORT).show();
                } else if (t_barcode.getText().length() == 0 || t_barcode.getText().toString().trim().equals("")) {
                    scrollView.fling(t_barcode.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未选择药品条码", Toast.LENGTH_SHORT).show();
                } else if (!Utils.isNum(t_barcode.getText().toString()) || t_barcode.getText().length() != 13) {
                    scrollView.fling(t_barcode.getTop() - 100);
                    Toast.makeText(AddActivity.this, "条码不符合规范", Toast.LENGTH_SHORT).show();
                } else if ((t_usage_1.getText().length() == 0 || t_usage_1.getText().toString().trim().equals("")) ||
                        (t_usage_2.getText().length() == 0 || t_usage_2.getText().toString().trim().equals("")) ||
                        (t_usage_3.getText().length() == 0 || t_usage_3.getText().toString().trim().equals(""))) {
                    scrollView.fling(t_usage_1.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写药品用法用量", Toast.LENGTH_SHORT).show();
                } else if (!Utils.isNum(t_usage_1.getText().toString()) ||
                        !Utils.isNum(t_usage_2.getText().toString()) ||
                        !Utils.isNum(t_usage_3.getText().toString()) ||
                        t_usage_3.getText().length() > Constant.MAX_USE ||
                        t_usage_2.getText().length() > Constant.MAX_USE ||
                        t_usage_3.getText().length() > Constant.MAX_USE) {
                    scrollView.fling(t_usage_1.getTop() - 100);
                    Toast.makeText(AddActivity.this, "用法用量不符合规范", Toast.LENGTH_SHORT).show();
                } else if (t_company.getText().length() == 0 || t_company.getText().toString().trim().equals("")) {
                    scrollView.fling(t_company.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写公司名", Toast.LENGTH_SHORT).show();
                } else if (t_yu.getText().length() == 0 || t_yu.getText().toString().trim().equals("")) {
                    scrollView.fling(t_yu.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写余量", Toast.LENGTH_SHORT).show();
                } else if (!Utils.isNum(t_yu.getText().toString())) {
                    scrollView.fling(t_yu.getTop() - 100);
                    Toast.makeText(AddActivity.this, "余量不符合规范", Toast.LENGTH_SHORT).show();
                } else if (Long.parseLong(t_yu.getText().toString()) > Constant.MAX_YU) {
                    scrollView.fling(t_yu.getTop() - 100);
                    Toast.makeText(AddActivity.this, "余量不正确(>2亿)", Toast.LENGTH_SHORT).show();
                } else if (elabel.size() <= 0) {
                    scrollView.fling(b_addElabel.getTop() - 100);
                    Toast.makeText(AddActivity.this, "未填写标签", Toast.LENGTH_SHORT).show();
                } else {
                    String keyid = Utils.getRandomKeyId();
                    String name = t_name.getText().toString().trim();
                    while (isPresentkeyId(keyid)) {
                        keyid = Utils.getRandomKeyId();
                    }
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Constant.COLUMN_M_KEYID, keyid);
                    values.put(Constant.COLUMN_M_UID, Utils.getString(AddActivity.this, Constant.MG_LOGIN_LNAME, "0"));
                    values.put(Constant.COLUMN_M_NAME, name);
                    values.put(Constant.COLUMN_M_IMAGE, Utils.getBytesFromBitmap(Utils.getBitmapPress(img,4)));
                    values.put(Constant.COLUMN_M_DESCRIPTION, t_desp.getText().toString());
                    values.put(Constant.COLUMN_M_OUTDATE, String.valueOf(outDate));
                    values.put(Constant.COLUMN_M_OTC, otc);
                    values.put(Constant.COLUMN_M_BARCODE, t_barcode.getText().toString().trim());
                    values.put(Constant.COLUMN_M_YU, t_yu.getText().toString().trim());
                    values.put(Constant.COLUMN_M_ELABEL, Utils.getStringFromArrayList(elabel));
                    values.put(Constant.COLUMN_M_LOVE, "0");
                    values.put(Constant.COLUMN_M_SHARE, "无");
                    values.put(Constant.COLUMN_M_MUSE, t_usage_1.getText().toString() + "-" + t_usage_1_util.getText().toString() + "-"
                            + t_usage_2.getText().toString() + "-" + t_usage_2_util.getText().toString() + "-" +
                            t_usage_3.getText().toString() + "-" + t_usage_3_util.getText().toString());
                    values.put(Constant.COLUMN_M_COMPANY, t_company.getText().toString());
                    values.put(Constant.COLUMN_M_DELFLAG, 0);
                    values.put(Constant.COLUMN_M_SHOWFLAG, 0);
                    values.put(Constant.COLUMN_M_FROMWEB, 0);
                    values.put(Constant.COLUMN_M_GROUP, group);
//					values.put(Constant.COLUMN_M_MD5KEY, Utils.getMedicineMD5(keyid, name));
                    long status = db.insert(Constant.TABLE_NAME_MEDICINE, null, values);
                    if (status >= 0) {
                        Toast.makeText(AddActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
                        Log.d(Constant.TABLE_NAME_MEDICINE, "insert successful");
                    } else {
                        Toast.makeText(AddActivity.this, "插入失败", Toast.LENGTH_SHORT).show();
                        Log.d(Constant.TABLE_NAME_MEDICINE, "insert error");
                    }
                    db.close();
                    finish();
                }
            }
        });


        ImageButton add_import = root.findViewById(R.id.id_add_import);
        add_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AlertDialog tips = new AlertDialog.Builder(AddActivity.this)
//                        .setTitle("提示：")
//                        .setMessage("未完善，请等待最近公告")
//                        .setNeutralButton("好", null)
//                        .create();
//                tips.show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AddActivity.this.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddActivity.this, "需要申请存储权限，否则无法使用导入导出功能", Toast.LENGTH_SHORT).show();

                    AddActivity.this.requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);

                } else {
                    AlertDialog tips11 = new AlertDialog.Builder(AddActivity.this)
                            .setTitle("提示：")
                            .setMessage("1.请按照模板模块填入并导入内容。\n2.导入的药品需符合法律法规，不违反国家法律。")
                            .setNeutralButton("我承诺，开始导入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent a = new Intent(AddActivity.this, ImportActivity.class);
                                    startActivity(a);

                                }
                            })
                            .setNegativeButton("取消", null)
                            .create();
                    tips11.show();

                }

            }
        });
    }


    //剪切图片
    //originUri--原始图片的Uri；
    //mDestinationUri--目标裁剪的图片保存的Uri
    private void startImageCrop(Uri uri) {
        if (uri == null) {
            return;
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setMaxBitmapSize(80);
        options.setMaxScaleMultiplier(6);
        Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "MgCropImage.jpeg"));
        UCrop.of(uri, mDestinationUri)
                .withOptions(options)
                .useSourceImageAspectRatio()
                .withAspectRatio(16, 9)
                .start(this, REQUEST_CODE_CROP);
    }

    public boolean isPresentkeyId(String keyid) {
        //TODO:联网
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select `" + Constant.COLUMN_M_KEYID +
                "` from " + Constant.TABLE_NAME_MEDICINE +
                " where `" + Constant.COLUMN_M_KEYID + "` = ?", new String[]{keyid});
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMG) {//判断是不是我们选择图片按钮的回调
            if (resultCode == Activity.RESULT_OK && null != data) {
                try {
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
//                System.out.println(Utils.getBase64(cr,uri));
                    img = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    RoundImageView b_chooseImg = root.findViewById(R.id.id_add_img_btn);
                    b_chooseImg.setImageBitmap(img);
                    imgUri = uri;
                    img_default = false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AddActivity.this, "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String code = data.getStringExtra("codedContent");
                if (Utils.isNum(code)) {
                    EditText t_barcode = root.findViewById(R.id.id_add_barcode);
                    t_barcode.setText(code);
                } else {
                    Toast.makeText(AddActivity.this, "条码不正确，请重新扫码", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                System.out.println(resultUri);
                try {
                    img = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    RoundImageView b_chooseImg = root.findViewById(R.id.id_add_img_btn);
                    b_chooseImg.setImageBitmap(img);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } else if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> essFileList = data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                System.out.println(essFileList.toString());

//                StringBuilder builder = new StringBuilder();
//                for (String file : essFileList) {
//                    builder.append(file).append("\n");
//                }

//                System.out.println(builder.toString());
            }
        }


    }


}
