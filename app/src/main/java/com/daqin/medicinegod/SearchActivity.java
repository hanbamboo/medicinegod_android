package com.daqin.medicinegod;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.daqin.medicinegod.Adspter.HotKeyAdapter;
import com.daqin.medicinegod.CustomWidget.FlowLayout;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivitySearchBinding;

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

/**
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    View root;
    final DatabaseHelper dbHelper = new DatabaseHelper(SearchActivity.this);
    private ArrayList<Map<String, Object>> history = new ArrayList<>();
    private ArrayList<Map<String, Object>> hotKey = new ArrayList<>();

    Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        history = queryHistory();
        new Thread(new TimerTask() {
            @Override
            public void run() {
                hotKey = queryHotKey();
                HotKeyAdapter adapter = new HotKeyAdapter(SearchActivity.this, R.layout.list_hotkey, hotKey);
                ListView listView = root.findViewById(R.id.id_search_hotkey);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();


        refreshHistory();

        ImageButton s_search_history_del = root.findViewById(R.id.id_search_history_del);
        s_search_history_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history.clear();
                DatabaseHelper dbHelper = new DatabaseHelper(SearchActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("Delete from " + Constant.TABLE_NAME_HISTORY + " where 1=1");
                db.close();
                FlowLayout s_history_layout = root.findViewById(R.id.id_search_history_layout);
                s_history_layout.removeAllViewsInLayout();
            }
        });
        ListView listView = root.findViewById(R.id.id_search_hotkey);

        EditText s_searchBox = (EditText) root.findViewById(R.id.id_search_box);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                s_searchBox.setText((String) hotKey.get(position).get(Constant.COLUMN_H_HOTKEY));
            }
        });
        s_searchBox.setFocusable(true);
        s_searchBox.setFocusableInTouchMode(true);
        s_searchBox.requestFocus();
        //进去整个view还没构建完毕，弹出软键盘是没有效果，加了个定时器，在进到页面后200毫秒后就会弹出软键盘。
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(s_searchBox, 0);
            }
        }, 200);
        ImageButton s_search_back = root.findViewById(R.id.id_search_back);
        s_search_back.setOnClickListener(l -> {
            finish();
        });
        Button s_search_btn = root.findViewById(R.id.id_search_btn);
        s_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = s_searchBox.getText().toString().trim();
                if (str.length() == 0) {
                    AlertDialog tips = new AlertDialog.Builder(SearchActivity.this)
                            .setTitle("一条信息:")
                            .setMessage("请填写搜索内容")
                            .setPositiveButton("好", null)
                            .create();
                    tips.show();
                } else {
                    addHistory(str);
                    Utils.putString(SearchActivity.this, Constant.SEARCHKEY, str);
                    Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
                    startActivity(i);
                }

            }
        });

    }

    /**
     * addHistory 添加搜索历史
     *
     * @param str 搜索内容
     */
    public void addHistory(String str) {
        new Thread(new TimerTask() {
            @Override
            public void run() {
                Connection connection = null;
                ResultSet resultSet = null;
                PreparedStatement pps = null;
                int times = -1;
                try {
                    connection = JdbcUtil.getConnection();
                    if (connection != null) {
                        pps = connection.prepareStatement("SELECT times FROM HOTKEY WHERE hotkey = ?;");
                        pps.setString(1, str);
                        resultSet = pps.executeQuery();
                        while (resultSet.next()) {
                            times = resultSet.getInt("times");
                        }
                        if (times == -1) {
                            times = 1;
                            try {
                                pps = connection.prepareStatement("INSERT INTO HOTKEY VALUES(?,?); ");
                                pps.setString(1, str);
                                pps.setInt(2, times);
                                pps.executeUpdate();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else {
                            times++;
                            try {
                                pps = connection.prepareStatement("UPDATE HOTKEY SET times=? where hotkey=?; ");
                                pps.setInt(1, times);
                                pps.setString(2, str);
                                pps.executeUpdate();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        }
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        if (pps != null) {
                            pps.close();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        if (connection != null) {
                            connection.close();
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }).start();
        if (history == null || history.size() == 0) {
            history = new ArrayList<>();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int viewId = random.nextInt(8999) * 1234;
        //添加到数据库中
        values.put(Constant.COLUMN_H_ID, viewId);
        values.put(Constant.COLUMN_H_STR, str);
        db.insert(Constant.TABLE_NAME_HISTORY, null, values);
        Map<String, Object> mapone = new HashMap<>();
        mapone.put(Constant.COLUMN_H_ID, viewId);
        mapone.put(Constant.COLUMN_H_STR, str);
        if (history == null) {
            history = new ArrayList<>();
        }
        if (history.size() >= 10) {
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>(history);
            arrayList.add(0, mapone);
            arrayList.remove(arrayList.size() - 1);
            db.execSQL("Delete from " + Constant.TABLE_NAME_HISTORY + " where 1=1");
            history.clear();
            history.addAll(arrayList);
            for (Map<String, Object> map : history) {
                ContentValues value = new ContentValues();
                value.put(Constant.COLUMN_H_ID, Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_H_ID)).toString()));
                value.put(Constant.COLUMN_H_STR, Objects.requireNonNull(map.get(Constant.COLUMN_H_STR)).toString());
                db.insert(Constant.TABLE_NAME_HISTORY, null, value);
            }
        } else {
            history.add(mapone);
        }
        db.close();
        refreshHistory();
    }

    public void refreshHistory() {
        DatabaseHelper dbHelper = new DatabaseHelper(SearchActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (history == null) {
            db.close();
            return;
        }
        FlowLayout s_history_layout = root.findViewById(R.id.id_search_history_layout);
        s_history_layout.removeAllViewsInLayout();
        EditText s_searchBox = (EditText) root.findViewById(R.id.id_search_box);
        for (Map<String, Object> map : history) {
            int viewId = Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_H_ID)).toString());
            String str = (String) map.get(Constant.COLUMN_H_STR);

            //设置一个标签
            TextView textView = new TextView(SearchActivity.this);
            textView.setId(viewId);
            textView.setText(str);
            textView.setTextSize(14);
            textView.setTextColor(Color.rgb(136, 136, 136));
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setPadding(20, 5, 20, 5);
            textView.setBackground(SearchActivity.this.getDrawable(R.drawable.bg_search_history_btn));
//            textView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    db.delete(Constant.TABLE_NAME_HISTORY, Constant.COLUMN_H_ID + "=?", new String[]{Objects.requireNonNull(map.get(Constant.COLUMN_H_ID)).toString()});
//                    db.close();
//                    history.remove(map);
//                    refreshHistory();
//                    return false;
//                }
//            });
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s_searchBox.setText(str);
                }
            });
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            //添加到布局中
            s_history_layout.addView(textView, layoutParams);
        }
    }

    public ArrayList<Map<String, Object>> queryHistory() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_HISTORY, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return null;
        }
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.COLUMN_H_ID, cursor.getString(0));
            map.put(Constant.COLUMN_H_STR, cursor.getString(1));
            res.add(0, map);
        }
        cursor.close();
        db.close();
        return res;
    }

    public ArrayList<Map<String, Object>> queryHotKey() {
        ArrayList<Map<String, Object>> key = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement pps = null;
        try {
            connection = JdbcUtil.getConnection();
            if (connection != null) {
                pps = connection.prepareStatement("SELECT * FROM HOTKEY ORDER BY times DESC LIMIT 10;");
                resultSet = pps.executeQuery();
                int count = 0;
                while (resultSet.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.COLUMN_H_HOTKEY, resultSet.getString(1));
                    map.put(Constant.COLUMN_H_TIMES, resultSet.getString(2));
                    key.add(map);
                    count++;
                }

                if (count == 0) {
                    return null;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (pps != null) {
                try {
                    pps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return key;
    }


}

