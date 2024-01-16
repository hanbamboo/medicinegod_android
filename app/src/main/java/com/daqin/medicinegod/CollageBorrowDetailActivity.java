package com.daqin.medicinegod;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.daqin.medicinegod.Adspter.CollageBorrowDetailAdapter;
import com.daqin.medicinegod.CustomWidget.AutoSwipeRefreshLayout;
import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.databinding.ActivityCollageBorrowDetailBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CollageBorrowDetailActivity extends AppCompatActivity {
	private ActivityCollageBorrowDetailBinding binding;
	static Map<String, Object> collageBorrow = new HashMap<>();
	static List<Map<String, Object>> collageBorrowPing = new ArrayList<>();

	View root;

	private String bid;
	private String lname;
	ListView listView;
	View headview;
	AutoSwipeRefreshLayout autoSwipeRefreshLayout;
	CollageBorrowDetailAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityCollageBorrowDetailBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		root = binding.getRoot();
		bid = Utils.getString(this, Constant.CBBIDKEY, "0");
		lname = Utils.getString(this, Constant.MG_LOGIN_LNAME, "0");
		if (bid.equals("0") || lname.equals("0")) {
			finish();
		}
		listView = binding.idCollageBDetailList;
		autoSwipeRefreshLayout = binding.idCollageBDetailFreshlayout;
		//只含有topnew图片的viewpager
		headview = View.inflate(getApplicationContext(), R.layout.activity_collage_borrow_detail_header, null);
		//将viewpager添加到list中作为头
		listView.addHeaderView(headview);



		autoSwipeRefreshLayout.setColorSchemeResources(R.color.m_normal, R.color.m_blue_80, R.color.m_near, R.color.m_out);
		autoSwipeRefreshLayout.setOnRefreshListener(new AutoSwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				fresh();
				Log.i(Constant.PREFERENCES_NAME, "刷新详情页");
			}
		});
		autoSwipeRefreshLayout.setRefreshing(true);
		fresh();
	}

	public void fresh() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Connection con = null;
				PreparedStatement pps = null;
				ResultSet resultSet = null;
				try {
					collageBorrow.clear();
					collageBorrowPing.clear();
					con = JdbcUtil.getConnection();
					pps = con.prepareStatement("SELECT a.*,b.HEAD,b.SNAME,b.MYSTYLE," +
							"c.LNAME as PLNAME,c.CONTEXT as PCONTEXT,c.TIME_SHOW as PTIME_SHOW,c.PID," +
							"d.HEAD as PHEAD,d.SNAME as PSNAME " +
							"FROM " + Constant.TABLE_COLLAGE_BORROW + " as a " +
							"INNER JOIN USERINFO as b ON a.LNAME = b.LNAME " +
							"LEFT JOIN " + Constant.TABLE_COLLAGE_BORROW_PING + " as c ON a.BID = c.BID " +
							"LEFT JOIN USERINFO as d ON d.LNAME = c.LNAME WHERE a.BID = ? ORDER BY c.TIME_SHOW DESC ");
					pps.setString(1, bid);
					resultSet = pps.executeQuery();
					int count = 0;
					while (resultSet.next()) {
//						if (count == 0) {
						collageBorrow.put(Constant.COLUMN_C_B_BID, bid);
						collageBorrow.put(Constant.COLUMN_C_B_LNAME, resultSet.getString(Constant.COLUMN_C_B_LNAME));
						collageBorrow.put(Constant.COLUMN_C_B_TITLE, resultSet.getString(Constant.COLUMN_C_B_TITLE));
						collageBorrow.put(Constant.COLUMN_C_B_CONTEXT, resultSet.getString(Constant.COLUMN_C_B_CONTEXT));
						collageBorrow.put(Constant.COLUMN_C_B_TORNAME, resultSet.getString(Constant.COLUMN_C_B_TORNAME));
						collageBorrow.put(Constant.COLUMN_C_B_TOPHONE, resultSet.getString(Constant.COLUMN_C_B_TOPHONE));
						collageBorrow.put(Constant.COLUMN_C_B_TOADDRESS, resultSet.getString(Constant.COLUMN_C_B_TOADDRESS));
						collageBorrow.put(Constant.COLUMN_C_B_IMAGE, resultSet.getBytes(Constant.COLUMN_C_B_IMAGE));
						collageBorrow.put(Constant.COLUMN_C_B_TIMESHOW, resultSet.getString(Constant.COLUMN_C_B_TIMESHOW));
						collageBorrow.put(Constant.COLUMN_C_B_METHOD, resultSet.getString(Constant.COLUMN_C_B_METHOD));
						collageBorrow.put(Constant.COLUMN_U_SNAME, resultSet.getString(Constant.COLUMN_U_SNAME));
						collageBorrow.put(Constant.COLUMN_U_HEAD, resultSet.getBytes(Constant.COLUMN_U_HEAD));
						collageBorrow.put(Constant.COLUMN_U_MYSTYLE, resultSet.getString(Constant.COLUMN_U_MYSTYLE));
//							count++;
//						}
						Map<String, Object> map = new HashMap<>();
						map.put(Constant.COLUMN_C_B_BID, resultSet.getString(Constant.COLUMN_C_B_BID));
						map.put(Constant.COLUMN_C_B_PING_LNAME, resultSet.getString(Constant.COLUMN_C_B_PING_LNAME));
						map.put(Constant.COLUMN_C_B_PING_SNAME, resultSet.getString(Constant.COLUMN_C_B_PING_SNAME));
						map.put(Constant.COLUMN_C_B_PING_CONTEXT, resultSet.getString(Constant.COLUMN_C_B_PING_CONTEXT));
						map.put(Constant.COLUMN_C_B_PING_SHOWTIME, resultSet.getString(Constant.COLUMN_C_B_PING_SHOWTIME));
						map.put(Constant.COLUMN_C_B_PING_HEAD, resultSet.getBytes(Constant.COLUMN_C_B_PING_HEAD));
						map.put(Constant.COLUMN_C_B_PID, resultSet.getString(Constant.COLUMN_C_B_PID));
						collageBorrowPing.add(map);
					}
					System.out.println(collageBorrow);
					System.out.println(collageBorrowPing);
					synchronized (this) {
						headview.post(new Runnable() {
							@SuppressLint("SetTextI18n")
							@Override
							public void run() {
								TextView id_collage_b_detail_header_method = headview.findViewById(R.id.id_collage_b_detail_header_method);
								TextView id_collage_b_detail_header_title = headview.findViewById(R.id.id_collage_b_detail_header_title);
								RoundImageView id_collage_b_detail_header_head = headview.findViewById(R.id.id_collage_b_detail_header_head);
								TextView id_collage_b_detail_header_sname = headview.findViewById(R.id.id_collage_b_detail_header_sname);
								TextView id_collage_b_detail_header_mystyle = headview.findViewById(R.id.id_collage_b_detail_header_mystyle);
								TextView id_collage_b_detail_header_context = headview.findViewById(R.id.id_collage_b_detail_header_context);
								RoundImageView id_collage_b_detail_header_img = headview.findViewById(R.id.id_collage_b_detail_header_img);
								TextView id_collage_b_detail_header_toname = headview.findViewById(R.id.id_collage_b_detail_header_toname);
								TextView id_collage_b_detail_header_tophone = headview.findViewById(R.id.id_collage_b_detail_header_tophone);
								TextView id_collage_b_detail_header_toaddr = headview.findViewById(R.id.id_collage_b_detail_header_toaddr);
								TextView id_collage_b_detail_header_time = headview.findViewById(R.id.id_collage_b_detail_header_time);

								EditText id_collage_b_detail_header_text = headview.findViewById(R.id.id_collage_b_detail_header_text);
								Button id_collage_b_detail_header_tijiao = headview.findViewById(R.id.id_collage_b_detail_header_tijiao);

								String method = (String) collageBorrow.get(Constant.COLUMN_C_B_METHOD);
								if (method == null) {
									method = Constant.COLUMN_C_B_METHOD_XIANZHI;
								}
								switch (Objects.requireNonNull(method)) {
									case Constant.COLUMN_C_B_METHOD_XIANZHI:
										id_collage_b_detail_header_method.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(),
												R.drawable.bg_collage_status_green, null));
										id_collage_b_detail_header_method.setTextColor(Color.rgb(76, 175, 80));
										id_collage_b_detail_header_method.setText("闲置出借");
										break;
									case Constant.COLUMN_C_B_METHOD_XUQIU:
										id_collage_b_detail_header_method.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(),
												R.drawable.bg_collage_status_red, null));
										id_collage_b_detail_header_method.setTextColor(Color.rgb(255, 67, 54));
										id_collage_b_detail_header_method.setText("药品需求");
										break;
									case Constant.COLUMN_C_B_METHOD_JIAOHUAN:
										id_collage_b_detail_header_method.setBackground(ResourcesCompat.getDrawable(getApplicationContext().getResources(),
												R.drawable.bg_collage_status_blue, null));
										id_collage_b_detail_header_method.setTextColor(Color.rgb(69, 93, 238));
										id_collage_b_detail_header_method.setText("药品交换");
										break;
								}
								id_collage_b_detail_header_title.setText((String) collageBorrow.get(Constant.COLUMN_C_B_TITLE));
								byte[] imgs = (byte[]) collageBorrow.get(Constant.COLUMN_U_HEAD);
								if (imgs == null) {
									id_collage_b_detail_header_head.setImageResource(R.mipmap.me_man_default);
								} else {
									id_collage_b_detail_header_head.setImageBitmap(Utils.getBitmapFromByte(imgs));
								}

								id_collage_b_detail_header_sname.setText((String) collageBorrow.get(Constant.COLUMN_U_SNAME));
								id_collage_b_detail_header_mystyle.setText((String) collageBorrow.get(Constant.COLUMN_U_MYSTYLE));
								id_collage_b_detail_header_context.setText((String) collageBorrow.get(Constant.COLUMN_C_B_CONTEXT));
								byte[] img = (byte[]) collageBorrow.get(Constant.COLUMN_C_B_IMAGE);
								if (img == null) {
									id_collage_b_detail_header_img.setVisibility(View.GONE);
								} else {
									id_collage_b_detail_header_img.setVisibility(View.VISIBLE);
									id_collage_b_detail_header_img.setImageBitmap(Utils.getBitmapFromByte(img));
								}
								try {
									id_collage_b_detail_header_time.setText(Utils.getStringFromTime(Long.parseLong(Objects.requireNonNull(collageBorrow.get(Constant.COLUMN_C_B_TIMESHOW)).toString())));
								} catch (Exception e) {
									e.printStackTrace();
								}
								id_collage_b_detail_header_toname.setText("联系人：" + (String) collageBorrow.get(Constant.COLUMN_C_B_TORNAME));
								String phone = (String) collageBorrow.get(Constant.COLUMN_C_B_TOPHONE);
								id_collage_b_detail_header_tophone.setText("联系方式：" + phone);
								id_collage_b_detail_header_tophone.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										if (checkPer()) {
											//如果需要手动拨号将Intent.ACTION_CALL改为Intent.ACTION_DIAL（跳转到拨号界面，用户手动点击拨打）
											Intent intent = new Intent(Intent.ACTION_DIAL);
											Uri data = Uri.parse("tel:" + phone);
											intent.setData(data);
											startActivity(intent);
										}

									}
								});
								String address = (String) collageBorrow.get(Constant.COLUMN_C_B_TOADDRESS);
								id_collage_b_detail_header_toaddr.setText("联系地址：" + address);
								id_collage_b_detail_header_toaddr.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ClipboardManager clipboard = (ClipboardManager)
												getSystemService(Context.CLIPBOARD_SERVICE);
										ClipData clip = ClipData.newPlainText("toAddress", address);
										clipboard.setPrimaryClip(clip);
									}
								});


								id_collage_b_detail_header_tijiao.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										if (id_collage_b_detail_header_text.getText().toString().trim().length() == 0) {
											Looper.prepare();
											AlertDialog tips = new AlertDialog.Builder(CollageBorrowDetailActivity.this)
													.setTitle("提示:")
													.setMessage("请填写评论")
													.setPositiveButton("确认", null)
													.create();
											tips.show();
											Looper.loop();
										} else {
											new Thread(new Runnable() {
												@Override
												public void run() {
													Connection c = null;
													PreparedStatement p = null;
													ResultSet resultSet1 = null;
													try {
														String time = String.valueOf(Utils.getTimeFromString(Utils.getTime()));
														String text = id_collage_b_detail_header_text.getText().toString().trim();
														c = JdbcUtil.getConnection();
														p = c.prepareStatement("INSERT INTO " + Constant.TABLE_COLLAGE_BORROW_PING
																+ " (BID,LNAME,CONTEXT,TIME_SHOW) VALUES(?,?,?,?)");
														p.setString(1, bid);
														p.setString(2, lname);
														p.setString(3, text);
														p.setString(4, time);
														int status = p.executeUpdate();
														if (status >= 1) {
															p = c.prepareStatement("SELECT a.*,b.HEAD as PHEAD,b.SNAME AS PSNAME FROM "
																	+ Constant.TABLE_COLLAGE_BORROW_PING + " AS a INNER JOIN USERINFO AS b ON a.LNAME = b.LNAME " +
																	" WHERE (a.BID =? AND a.LNAME=? AND a.CONTEXT =? AND a.TIME_SHOW =?)");
															p.setString(1, bid);
															p.setString(2, lname);
															p.setString(3, text);
															p.setString(4, time);
															resultSet1 = p.executeQuery();
															while (resultSet1.next()) {
																Map<String, Object> mapres = new HashMap<>();
																mapres.put(Constant.COLUMN_C_B_PID, resultSet1.getString(Constant.COLUMN_C_B_PID));
																mapres.put(Constant.COLUMN_C_B_BID, bid);
																mapres.put(Constant.COLUMN_C_B_PING_LNAME, lname);
																mapres.put(Constant.COLUMN_C_B_PING_HEAD, resultSet1.getBytes(Constant.COLUMN_C_B_PING_HEAD));
																mapres.put(Constant.COLUMN_C_B_PING_SNAME, resultSet1.getString(Constant.COLUMN_C_B_PING_SNAME));
																mapres.put(Constant.COLUMN_C_B_PING_CONTEXT, text);
																mapres.put(Constant.COLUMN_C_B_PING_SHOWTIME, time);
																collageBorrowPing.add(mapres);
															}
															Looper.prepare();
															listView.post(new Runnable() {
																@Override
																public void run() {
																	adapter.notifyDataSetChanged();
																}
															});


															Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
															id_collage_b_detail_header_text.post(new Runnable() {
																@Override
																public void run() {
																	id_collage_b_detail_header_text.setText("");
																}
															});

															Looper.loop();


														}
														Log.i(Constant.PREFERENCES_NAME, "发表评论" + status);
													} catch (Exception sqlException) {
														sqlException.printStackTrace();
													} finally {
														JdbcUtil.close(c, null, p, null);
													}
												}
											}).start();

										}
									}
								});
							}

						});

					adapter =
							new CollageBorrowDetailAdapter(getApplicationContext(),
									R.layout.list_ping,
									collageBorrowPing,
									collageBorrow,
									lname);
					listView.post(new Runnable() {
						@Override
						public void run() {
							listView.setAdapter(adapter);

						}
					});
					}
					adapter.setonItemDelListener(new CollageBorrowDetailAdapter.onItemDelListener() {
						@Override
						public void onDelClick(int index, String pid) {
							AlertDialog tips = new AlertDialog.Builder(CollageBorrowDetailActivity.this)
									.setTitle("提示:")
									.setMessage("确认删除？")
									.setPositiveButton("确认", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											new Thread(new Runnable() {
												@Override
												public void run() {
													Connection c = null;
													PreparedStatement p = null;
													try {
														c = JdbcUtil.getConnection();
														p = c.prepareStatement("delete from " + Constant.TABLE_COLLAGE_BORROW_PING + " where PID =?");
														p.setString(1, pid);
														int status = p.executeUpdate();
														if (status >= 1) {
															Looper.prepare();
															Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
															collageBorrowPing.remove(index);
															listView.post(new Runnable() {
																@Override
																public void run() {
																	adapter.notifyDataSetChanged();
																}
															});
															Looper.loop();
														}
														Log.i(Constant.PREFERENCES_NAME, "删除成功" + status);
													} catch (SQLException sqlException) {
														sqlException.printStackTrace();
													} finally {
														JdbcUtil.close(c, null, p, null);
													}
												}
											}).start();
										}
									})
									.setNegativeButton("取消", null)
									.create();
							tips.show();


						}
					});


				} catch (
						SQLException sqlException) {
					sqlException.printStackTrace();
				} finally {
					JdbcUtil.close(con, null, pps, resultSet);
					autoSwipeRefreshLayout.setRefreshing(false);

				}
			}
		}).start();

	}

	public boolean checkPer() {
		//android6版本获取动态权限
		int REQUEST_CODE_CONTACT = 101;
		String[] permissions = {Manifest.permission.CALL_PHONE};
		//验证是否许可权限
		for (String str : permissions) {
			if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
				//申请权限
				this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
				return true;
			}
		}
		return false;
	}

}

