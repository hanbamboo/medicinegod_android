package com.daqin.medicinegod;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daqin.medicinegod.Adspter.HistoryExcelAdapter;
import com.daqin.medicinegod.Adspter.HotKeyAdapter;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityHistoryExcelBinding;
import com.daqin.medicinegod.databinding.ActivitySearchBinding;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HistoryExcelActivity extends AppCompatActivity {

    private ActivityHistoryExcelBinding binding;
    View root;
    final DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);
    private ArrayList<Map<String, Object>> history = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryExcelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        history = queryHistory();

        TextView id_his_excel_title = root.findViewById(R.id.id_his_excel_title);
        id_his_excel_title.setText("文件概览(" + history.size() + ")");
        HistoryExcelAdapter adapter = new HistoryExcelAdapter(
                HistoryExcelActivity.this,
                R.layout.list_history_excel,
                history);
        SwipeMenuListView listView = root.findViewById(R.id.id_his_excel_list);
        listView.setAdapter(adapter);

        ImageButton id_his_excel_back = root.findViewById(R.id.id_his_excel_back);
        id_his_excel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //侧滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "Edit" item
                SwipeMenuItem XianZhiItem = new SwipeMenuItem(HistoryExcelActivity.this);
                // set item background
                XianZhiItem.setBackground(new ColorDrawable(Color.rgb(104, 118, 237)));
                // set item width
                XianZhiItem.setWidth(200);
                // set item title
                XianZhiItem.setTitle("发送到");
                // set item title fontsize
                XianZhiItem.setTitleSize(16);
                // set item title font color
                XianZhiItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(XianZhiItem);

                // create "Edit" item
                SwipeMenuItem EditItem = new SwipeMenuItem(HistoryExcelActivity.this);
                // set item background
                EditItem.setBackground(new ColorDrawable(Color.rgb(159, 209,
                        161)));
                // set item width
                EditItem.setWidth(200);
                // set item title
                EditItem.setTitle("打开");
                // set item title fontsize
                EditItem.setTitleSize(16);
                // set item title font color
                EditItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(EditItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(HistoryExcelActivity.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(254,
                        159, 145)));
                // set item width
                deleteItem.setWidth(200);
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(16);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog tips11 = new AlertDialog.Builder(HistoryExcelActivity.this)
                        .setTitle("提示：")
                        .setMessage("是否要清除全部数据？（不会删除文件)")
                        .setNeutralButton("确认清除文件记录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete(Constant.TABLE_NAME_HISTORY_EXCEL, "1=1 ", null);
                                db.close();
                                history = new ArrayList<>();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(HistoryExcelActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                tips11.show();
                return false;
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                String filePath = (String) history.get(position).get(Constant.COLUMN_HE_FILEPATH);
                switch (index) {
                    case 0:
                        if (!Utils.isExists((String) history.get(position).get(Constant.COLUMN_HE_FILEPATH))) {
                            return false;
                        }
                        Utils.startShareFile(HistoryExcelActivity.this, filePath, Constant.FILE_TYPE_XLS, "将药神导入药品模板文件分享到...");
                        break;
                    case 1:

                        Utils.startOpenFile(HistoryExcelActivity.this, filePath, Constant.FILE_TYPE_XLS, "选择应用程序来打开药神导入药品模板文件");
                        break;
                    case 2:
                        if (!Utils.isExists(filePath)
                                || filePath == null
                                || (Objects.requireNonNull(history.get(position).get(Constant.COLUMN_HE_FLAG)).toString().contains(Constant.COLUMN_HE_FLAG_EXPIRE))) {
                            DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete(Constant.TABLE_NAME_HISTORY_EXCEL, Constant.COLUMN_HE_FILEPATH + " = ? and " + Constant.COLUMN_HE_NAME + " = ?",
                                    new String[]{filePath, (String) history.get(position).get(Constant.COLUMN_HE_NAME)});
                            db.close();
                            history.remove(position);
                            id_his_excel_title.setText("文件概览(" + history.size() + ")");
                            adapter.notifyDataSetChanged();
                            return false;
                        }
                        AlertDialog tips = new AlertDialog.Builder(HistoryExcelActivity.this)
                                .setTitle("提示：")
                                .setMessage("即将删除文件，是否确认？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boolean res = Utils.deleteFile(filePath);
                                        DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);

                                        if (res) {
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            db.delete(Constant.TABLE_NAME_HISTORY_EXCEL,
                                                    Constant.COLUMN_HE_FILEPATH + " = ? and " + Constant.COLUMN_HE_NAME + " = ?",
                                                    new String[]{filePath, (String) history.get(position).get(Constant.COLUMN_HE_NAME)});
                                            db.close();
                                            history.remove(position);
                                            id_his_excel_title.setText("文件概览(" + history.size() + ")");
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(HistoryExcelActivity.this, "文件删除成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            AlertDialog tips = new AlertDialog.Builder(HistoryExcelActivity.this)
                                                    .setTitle("提示：")
                                                    .setMessage("文件删除失败，文件不存在！是否直接从列表删除？")
                                                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //delete的返回值是该操作所影响的行数，所以当res = 1,就是删除了一条记录，0 表示删除失败
                                                            DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);

                                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                            db.delete(Constant.TABLE_NAME_HISTORY_EXCEL, Constant.COLUMN_HE_FILEPATH + " = ? and " + Constant.COLUMN_HE_NAME + " = ?",
                                                                    new String[]{filePath, (String) history.get(position).get(Constant.COLUMN_HE_NAME)});
                                                            db.close();
                                                            history.remove(position);
                                                            id_his_excel_title.setText("文件概览(" + history.size() + ")");
                                                            adapter.notifyDataSetChanged();
                                                            Toast.makeText(HistoryExcelActivity.this, "文件删除成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                                            ContentValues values = new ContentValues();
                                                            values.put(Constant.COLUMN_HE_FLAG, Constant.COLUMN_HE_FLAG_EXPIRE);
                                                            db.update(Constant.TABLE_NAME_HISTORY_EXCEL,
                                                                    values,
                                                                    Constant.COLUMN_HE_FILEPATH + " = ? and " + Constant.COLUMN_HE_NAME + " = ?",
                                                                    new String[]{filePath, (String) history.get(position).get(Constant.COLUMN_HE_NAME)});
                                                            db.close();
                                                            Map<String, Object> temp = history.get(position);
                                                            temp.put(Constant.COLUMN_HE_FLAG, Constant.COLUMN_HE_FLAG_EXPIRE);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .create();
                                            tips.show();

                                        }
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create();
                        tips.show();


                        break;
                }
                // false : close the menu; true : not close the menu

                return false;
            }
        });
        // Left

    }

    public ArrayList<Map<String, Object>> queryHistory() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(HistoryExcelActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_HISTORY_EXCEL, null);
//        Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_HISTORY_EXCEL + " order by ? DESC", new String[]{Constant.COLUMN_HE_TIME});
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return new ArrayList<>();
        }

        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.COLUMN_HE_FLAG, cursor.getString(0));
            map.put(Constant.COLUMN_HE_NAME, cursor.getString(1));
            map.put(Constant.COLUMN_HE_FILEPATH, cursor.getString(2));
            map.put(Constant.COLUMN_HE_TIME, cursor.getString(3));
            res.add(0, map);
        }
        cursor.close();
        db.close();
        return res;
    }
}



