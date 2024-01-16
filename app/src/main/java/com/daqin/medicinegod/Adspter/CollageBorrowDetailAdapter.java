package com.daqin.medicinegod.Adspter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CollageBorrowDetailAdapter extends ArrayAdapter {
	private final int resourceId;
	List<Map<String, Object>> ping = new ArrayList<>();
	Map<String, Object> detail = new HashMap<>();
	String lname;

	public CollageBorrowDetailAdapter(Context context, int textViewResourceId, List<Map<String, Object>> ping, Map<String, Object> detail, String lname) {
		super(context, textViewResourceId, ping);
		this.ping = ping;
		resourceId = textViewResourceId;
		this.lname = lname;
		this.detail = detail;
	}


	@SuppressLint({"SetTextI18n"})
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, Object> map = ping.get(position);
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
		} else {
			view = convertView;
		}
		if (map.get(Constant.COLUMN_C_B_PID) == null) {
			return view;
		}
		RoundImageView id_collage_b_ping_head = view.findViewById(R.id.id_collage_b_ping_head);
		TextView id_collage_b_ping_name = view.findViewById(R.id.id_collage_b_ping_name);
		TextView id_collage_b_ping_time = view.findViewById(R.id.id_collage_b_ping_time);
		TextView id_collage_b_ping_context = view.findViewById(R.id.id_collage_b_ping_context);
		TextView id_collage_b_ping_del = view.findViewById(R.id.id_collage_b_ping_del);
		String lname = (String) map.get(Constant.COLUMN_C_B_PING_LNAME);
		String pid = (String) map.get(Constant.COLUMN_C_B_PID);
		id_collage_b_ping_del.setVisibility(this.lname.equals(lname) ? View.VISIBLE : View.GONE);
		id_collage_b_ping_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnItemDelListener.onDelClick(position, pid);
			}
		});

		byte[] img = (byte[]) map.get(Constant.COLUMN_C_B_PING_HEAD);
		if (img != null) {
			id_collage_b_ping_head.setImageBitmap(Utils.getBitmapFromByte(img));
		}


		id_collage_b_ping_name.setText((String) map.get(Constant.COLUMN_C_B_PING_SNAME));
		id_collage_b_ping_time.setText(Utils.getStringFromTime(Long.parseLong(Objects.requireNonNull(map.get(Constant.COLUMN_C_B_PING_SHOWTIME)).toString())));
		id_collage_b_ping_context.setText((String) map.get(Constant.COLUMN_C_B_PING_CONTEXT));

		return view;
	}

	public interface onItemDelListener {
		void onDelClick(int i, String pid);
	}

	private onItemDelListener mOnItemDelListener;

	public void setonItemDelListener(onItemDelListener mOnItemDelListener) {
		this.mOnItemDelListener = mOnItemDelListener;
	}

	@Override
	public int getCount() {
		return ping == null ? 0 : ping.size();//一般返回数据源的长度
	}

	@Override
	public Object getItem(int position) {
		if (ping != null && position >= 0 && position < ping.size()) {
			return ping.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}