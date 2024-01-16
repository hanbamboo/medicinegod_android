package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CollageBorrowAdapter extends ArrayAdapter {
    List<Map<String, Object>> msg = new ArrayList<>();
    Context context;
    String lname;
    int zan;
    String ping;


    public CollageBorrowAdapter(Context context, int textViewResourceId, List<Map<String, Object>> msg, String lname) {
        super(context, textViewResourceId, msg);
        this.msg = msg;
        this.context = context;
        this.lname = lname;
    }


    @SuppressLint({"SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_collage_borrow, null);//实例化一个对象
        } else {
            view = convertView;
        }
        Map<String, Object> map = null;
        if (msg.size() != 0) {
            map = msg.get(position);
        } else {
            return view;
        }


        RoundImageView cb_head = view.findViewById(R.id.id_collage_b_list_head);
        TextView cb_name = view.findViewById(R.id.id_collage_b_list_name);
        TextView cb_mystyle = view.findViewById(R.id.id_collage_b_list_mystyle);
        TextView cb_method = view.findViewById(R.id.id_collage_b_list_method);
        TextView cb_title = view.findViewById(R.id.id_collage_b_list_title);
        TextView cb_context = view.findViewById(R.id.id_collage_b_list_context);
        RoundImageView cb_img = view.findViewById(R.id.id_collage_b_list_img);
        ImageButton cb_dellist = view.findViewById(R.id.id_collage_b_list_dellist);
        ImageButton cb_share = view.findViewById(R.id.id_collage_b_list_share);
        ImageButton cb_ping = view.findViewById(R.id.id_collage_b_list_ping);
        ImageButton cb_zan = view.findViewById(R.id.id_collage_b_list_zan);
        TextView cb_time = view.findViewById(R.id.id_collage_b_list_time);
//		TextView cb_ping_num = view.findViewById(R.id.id_collage_b_list_ping_num);
        TextView cb_zan_num = view.findViewById(R.id.id_collage_b_list_zan_num);

        cb_head.setImageBitmap(Utils.getBitmapFromByte((byte[]) map.get(Constant.COLUMN_U_HEAD)));
        cb_name.setText((String) map.get(Constant.COLUMN_U_SNAME));
        cb_mystyle.setText((String) map.get(Constant.COLUMN_U_MYSTYLE));
        String method = (String) map.get(Constant.COLUMN_C_B_METHOD);
        switch (Objects.requireNonNull(method)) {
            case Constant.COLUMN_C_B_METHOD_XIANZHI:
                cb_method.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_green, null));
                cb_method.setTextColor(Color.rgb(76, 175, 80));
                cb_method.setText("闲置出借");
                break;
            case Constant.COLUMN_C_B_METHOD_XUQIU:
                cb_method.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_red, null));
                cb_method.setTextColor(Color.rgb(255, 67, 54));
                cb_method.setText("药品需求");
                break;
            case Constant.COLUMN_C_B_METHOD_JIAOHUAN:
                cb_method.setBackground(ResourcesCompat.getDrawable(getContext().getResources(),
                        R.drawable.bg_collage_status_blue, null));
                cb_method.setTextColor(Color.rgb(69, 93, 238));
                cb_method.setText("药品交换");
                break;
        }
        cb_title.setText((String) map.get(Constant.COLUMN_C_B_TITLE));
        cb_context.setText("   " + (String) map.get(Constant.COLUMN_C_B_CONTEXT));
        byte[] img = (byte[]) map.get(Constant.COLUMN_C_B_IMAGE);
        if (img == null) {
            cb_img.setVisibility(View.GONE);
        } else {
            cb_img.setVisibility(View.VISIBLE);
            cb_img.setImageBitmap(Utils.getBitmapFromByte(img));
        }
        if (lname.equals((String) map.get(Constant.COLUMN_C_B_LNAME))) {
            cb_dellist.setVisibility(View.VISIBLE);
        } else {
            cb_dellist.setVisibility(View.GONE);
        }
        String bid = String.valueOf(Objects.requireNonNull(map.get(Constant.COLUMN_C_B_BID)).toString());

        cb_dellist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog tips = new AlertDialog.Builder(getContext())
                        .setTitle("提示:")
                        .setMessage("确定要删除吗")
                        .setNegativeButton("返回", null)
                        .setNeutralButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mOnItemDeleteListener.onDeleteClick(bid, which);
                            }
                        })
                        .create();
                tips.show();
            }
        });


//		zan = Integer.parseInt(Objects.requireNonNull(map.get(Constant.COLUMN_C_B_ZAN)).toString());
//		if (zan == 0) {
//			cb_zan.post(new Runnable() {
//				@Override
//				public void run() {
//					cb_zan.setImageResource(R.drawable.ic_zan_default);
//				}
//			});
//
//		} else {
//			cb_zan.post(new Runnable() {
//				@Override
//				public void run() {
//					cb_zan.setImageResource(R.drawable.ic_zan_red);
//				}
//			});
//		}
//		synchronized (this) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					Connection con = null;
//					PreparedStatement pps = null;
//					ResultSet resultSet = null;
//					try {
//						con = JdbcUtil.getConnection();
//						pps = con.prepareStatement("select COUNT(*) FROM " + Constant.TABLE_COLLAGE_BORROW_ZAN
//								+ "  where " + Constant.COLUMN_C_B_BID + "= ?");
//						pps.setString(1, bid);
//						resultSet = pps.executeQuery();
//						while (resultSet.next()) {
//							int count = resultSet.getInt(1);
//							cb_zan_num.post(new Runnable() {
//								@Override
//								public void run() {
//									cb_zan_num.setText(String.valueOf(count));
//								}
//							});
//						}
//					} catch (
//							SQLException sqlException) {
//						sqlException.printStackTrace();
//					} finally {
//						JdbcUtil.close(con, null, pps, resultSet);
//					}
//				}
//			}).start();
//		}
        long getTime = Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_C_B_TIMESHOW)).toString());
        cb_time.setText(Utils.getStringFromTime(getTime));


        cb_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				mOnItemZanListener.onZanClick(position, bid, cb_zan, cb_zan_num);


            }
        });


        return view;
    }


    /**
     * 删除按钮的监听接口
     */
    public interface onItemDeleteListener {
        void onDeleteClick(String bid, int i);
    }

    public interface onItemZanListener {
        void onZanClick(int i, String bid, View view, View countView);
    }


    private onItemDeleteListener mOnItemDeleteListener;
    private onItemZanListener mOnItemZanListener;

    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }

    public void setOnItemZanClickListener(onItemZanListener mOnItemZanListener) {
        this.mOnItemZanListener = mOnItemZanListener;
    }


    @Override
    public int getCount() {
        return msg == null ? 0 : msg.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (msg != null && position >= 0 && position < msg.size()) {
            return msg.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}