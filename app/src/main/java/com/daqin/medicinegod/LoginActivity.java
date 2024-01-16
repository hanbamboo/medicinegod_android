package com.daqin.medicinegod;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityLoginBinding;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

	private ActivityLoginBinding binding;
	View root;

	boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		root = binding.getRoot();
		EditText id_login_uname = binding.idLoginUname;
		EditText id_login_pwd = binding.idLoginPwd;
		id_login_pwd.setFilters(new InputFilter[]{new InputFilter() {
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
		id_login_uname.setFilters(new InputFilter[]{new InputFilter() {
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
		Button id_login_toregister = binding.idLoginToregister;
		id_login_toregister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(i);

			}
		});

		Button id_login_login = binding.idLoginLogin;
		id_login_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String uname = id_login_uname.getText().toString().trim();
				String upwd = id_login_pwd.getText().toString().trim();
				if (uname.length() <= 0) {
					AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
							.setTitle("错误:")
							.setMessage("请填写登录名或邮箱")
							.setPositiveButton("好", null)
							.create();
					dialog.show();
				} else if (upwd.length() <= 0) {
					AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
							.setTitle("错误:")
							.setMessage("请填写密码")
							.setPositiveButton("好", null)
							.create();
					dialog.show();
				} else {
					if (!flag) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								flag = true;
								Connection connection = null;
								PreparedStatement pps = null;
								ResultSet resultSet = null;
								final DatabaseHelper dbHelper = new DatabaseHelper(LoginActivity.this);
								if (uname.contains("@") && uname.contains("com")) {
									try {
										String lname = null;
										connection = JdbcUtil.getConnection();
										pps = connection.prepareStatement("SELECT LNAME,PWD FROM USERINFO WHERE MAIL =?");
										pps.setString(1, uname);
										resultSet = pps.executeQuery();
										int i = 0;
										while (resultSet.next()) {
											i++;
										}
										if (i == 0) {
											Looper.prepare();
											flag = false;
											AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
													.setTitle("错误:")
													.setMessage("邮箱不存在，登陆失败，请重试")
													.setPositiveButton("好", null)
													.create();
											if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
											{
												dialog.show();
											}
											Looper.loop();
										} else {
											connection.setAutoCommit(false);
											resultSet.first();
											lname = resultSet.getString(Constant.COLUMN_U_LNAME);
											String s = Utils.getSerect(lname, upwd);
											pps = connection.prepareStatement("SELECT * FROM USERINFO WHERE MAIL = ? AND PWD =?");
											pps.setString(1, uname);
											pps.setString(2, s);
											resultSet = pps.executeQuery();

											pps = connection.prepareStatement(Utils.getDatabaseSQL(lname));
											pps.executeUpdate();
											connection.commit();
											Map<String, Object> userInfo = new HashMap<>();
											while (resultSet.next()) {
												userInfo.put(Constant.COLUMN_U_LNAME, resultSet.getString(Constant.COLUMN_U_LNAME));
												userInfo.put(Constant.COLUMN_U_SNAME, resultSet.getString(Constant.COLUMN_U_SNAME));
												userInfo.put(Constant.COLUMN_U_PWD, resultSet.getString(Constant.COLUMN_U_PWD));
												userInfo.put(Constant.COLUMN_U_HEAD, resultSet.getBlob(Constant.COLUMN_U_HEAD));
												userInfo.put(Constant.COLUMN_U_FRIEND, resultSet.getString(Constant.COLUMN_U_FRIEND));
												userInfo.put(Constant.COLUMN_U_PHONE, resultSet.getString(Constant.COLUMN_U_PHONE));
												userInfo.put(Constant.COLUMN_U_MAIL, resultSet.getString(Constant.COLUMN_U_MAIL));
												userInfo.put(Constant.COLUMN_U_RGTIME, resultSet.getLong(Constant.COLUMN_U_RGTIME));
												userInfo.put(Constant.COLUMN_U_ONLINE, resultSet.getInt(Constant.COLUMN_U_ONLINE));
												userInfo.put(Constant.COLUMN_U_POINT, resultSet.getInt(Constant.COLUMN_U_POINT));
												userInfo.put(Constant.COLUMN_U_POINTHISTORY, resultSet.getString(Constant.COLUMN_U_POINTHISTORY));
												userInfo.put(Constant.COLUMN_U_VIPYU, resultSet.getLong(Constant.COLUMN_U_VIPYU));
												userInfo.put(Constant.COLUMN_U_VIP, resultSet.getInt(Constant.COLUMN_U_VIP));
												userInfo.put(Constant.COLUMN_U_CLOUDYU, resultSet.getInt(Constant.COLUMN_U_CLOUDYU));
												userInfo.put(Constant.COLUMN_U_SIGNIN, resultSet.getString(Constant.COLUMN_U_SIGNIN));
												userInfo.put(Constant.COLUMN_U_MYSTYLE, resultSet.getString(Constant.COLUMN_U_MYSTYLE));
											}
											if (userInfo.size() == 0) {
												Looper.prepare();
												flag = false;
												AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
														.setTitle("错误:")
														.setMessage("密码错误，请稍候重试")
														.setPositiveButton("好", null)
														.create();
												if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
												{
													dialog.show();
												}
												Looper.loop();
											} else {
												lname = (String) userInfo.get(Constant.COLUMN_U_LNAME);
												Utils.putInt(LoginActivity.this, Constant.MG_LOGIN, 1);
												Utils.putString(LoginActivity.this, Constant.MG_LOGIN_LNAME, lname);
												SQLiteDatabase db = dbHelper.getWritableDatabase();
												ContentValues values = new ContentValues();
												values.put(Constant.COLUMN_U_LNAME, lname);
												values.put(Constant.COLUMN_U_SNAME, (String) userInfo.get(Constant.COLUMN_U_SNAME));
												values.put(Constant.COLUMN_U_PWD, (String) userInfo.get(Constant.COLUMN_U_PWD));
												values.put(Constant.COLUMN_U_HEAD, Utils.blobToBytes((Blob) userInfo.get(Constant.COLUMN_U_HEAD)));
												values.put(Constant.COLUMN_U_FRIEND, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
												values.put(Constant.COLUMN_U_PHONE, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
												values.put(Constant.COLUMN_U_MAIL, (String) userInfo.get(Constant.COLUMN_U_MAIL));
												values.put(Constant.COLUMN_U_RGTIME, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_RGTIME)).toString()));
												values.put(Constant.COLUMN_U_ONLINE, 1);
												values.put(Constant.COLUMN_U_POINT, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_POINT)).toString()));
												values.put(Constant.COLUMN_U_POINTHISTORY, (String) userInfo.get(Constant.COLUMN_U_POINTHISTORY));
												values.put(Constant.COLUMN_U_VIP, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIP)).toString()));
												values.put(Constant.COLUMN_U_VIPYU, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIPYU)).toString()));
												values.put(Constant.COLUMN_U_CLOUDYU, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_CLOUDYU)).toString()));
												values.put(Constant.COLUMN_U_SIGNIN, (String) userInfo.get(Constant.COLUMN_U_SIGNIN));
												userInfo.put(Constant.COLUMN_U_MYSTYLE, (String) userInfo.get(Constant.COLUMN_U_MYSTYLE));

												long status_inset = db.replace(Constant.TABLE_NAME_USER, null, values);
												db.close();
												if (status_inset >= 1) {
													Looper.prepare();
													AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
															.setTitle("完成:")
															.setMessage("登录成功")
															.setPositiveButton("进入药神", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	finish();
																}
															})
															.create();
													dialog.setCancelable(false);
													flag = false;
													if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
													{
														dialog.show();
													}
													Looper.loop();
												} else {
													Looper.prepare();
													AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
															.setTitle("错误:")
															.setMessage("获取用户信息失败，请稍候重试")
															.setPositiveButton("好", null)
															.create();
													flag = false;

													if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
													{
														dialog.show();
													}
													Looper.loop();
												}
											}
										}
									} catch (Exception e) {
										try {
											if (connection != null) {
												//事务回滚，取消当前事务
												connection.rollback();
											}
										} catch (Exception sqlException) {
											flag = false;
											sqlException.printStackTrace();
										}
										Looper.prepare();
										flag = false;
										AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
												.setTitle("错误:")
												.setMessage("登陆失败，请稍候重试")
												.setPositiveButton("好", null)
												.create();
										if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
										{
											dialog.show();
										}
										Looper.loop();
										e.printStackTrace();
									} finally {
										flag = false;
										JdbcUtil.close(connection, null, pps, resultSet);
									}
								} else {
									try {
										String serct = Utils.getSerect(uname, upwd);
										connection = JdbcUtil.getConnection();
										connection.setAutoCommit(false);
										if (serct.length() == 0) {
											Looper.prepare();
											flag = false;
											AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
													.setTitle("错误:")
													.setMessage("登陆失败，请稍候重试")
													.setPositiveButton("好", null)
													.create();
											if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
											{
												dialog.show();
											}
											Looper.loop();
										} else {
											pps = connection.prepareStatement("SELECT * FROM USERINFO WHERE LNAME = ? AND PWD =?");
											pps.setString(1, uname);
											pps.setString(2, serct);
											resultSet = pps.executeQuery();
											pps = connection.prepareStatement(Utils.getDatabaseSQL(uname));
											pps.executeUpdate();
											connection.commit();
											Map<String, Object> userInfo = new HashMap<>();
											while (resultSet.next()) {
												userInfo.put(Constant.COLUMN_U_LNAME, resultSet.getString(Constant.COLUMN_U_LNAME));
												userInfo.put(Constant.COLUMN_U_SNAME, resultSet.getString(Constant.COLUMN_U_SNAME));
												userInfo.put(Constant.COLUMN_U_PWD, resultSet.getString(Constant.COLUMN_U_PWD));
												userInfo.put(Constant.COLUMN_U_HEAD, resultSet.getBlob(Constant.COLUMN_U_HEAD));
												userInfo.put(Constant.COLUMN_U_FRIEND, resultSet.getString(Constant.COLUMN_U_FRIEND));
												userInfo.put(Constant.COLUMN_U_PHONE, resultSet.getString(Constant.COLUMN_U_PHONE));
												userInfo.put(Constant.COLUMN_U_MAIL, resultSet.getString(Constant.COLUMN_U_MAIL));
												userInfo.put(Constant.COLUMN_U_RGTIME, resultSet.getLong(Constant.COLUMN_U_RGTIME));
												userInfo.put(Constant.COLUMN_U_ONLINE, resultSet.getInt(Constant.COLUMN_U_ONLINE));
												userInfo.put(Constant.COLUMN_U_POINT, resultSet.getInt(Constant.COLUMN_U_POINT));
												userInfo.put(Constant.COLUMN_U_POINTHISTORY, resultSet.getString(Constant.COLUMN_U_POINTHISTORY));
												userInfo.put(Constant.COLUMN_U_VIPYU, resultSet.getLong(Constant.COLUMN_U_VIPYU));
												userInfo.put(Constant.COLUMN_U_VIP, resultSet.getInt(Constant.COLUMN_U_VIP));
												userInfo.put(Constant.COLUMN_U_CLOUDYU, resultSet.getInt(Constant.COLUMN_U_CLOUDYU));
												userInfo.put(Constant.COLUMN_U_SIGNIN, resultSet.getString(Constant.COLUMN_U_SIGNIN));
												userInfo.put(Constant.COLUMN_U_MYSTYLE, resultSet.getString(Constant.COLUMN_U_MYSTYLE));
											}
											if (userInfo.size() == 0) {
												Looper.prepare();
												flag = false;
												AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
														.setTitle("错误:")
														.setMessage("密码错误，请稍候重试")
														.setPositiveButton("好", null)
														.create();
												if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
												{
													dialog.show();
												}
												Looper.loop();
											} else {
												Utils.putInt(LoginActivity.this, Constant.MG_LOGIN, 1);
												Utils.putString(LoginActivity.this, Constant.MG_LOGIN_LNAME, uname);
												SQLiteDatabase db = dbHelper.getWritableDatabase();
												ContentValues values = new ContentValues();
												values.put(Constant.COLUMN_U_LNAME, uname);
												values.put(Constant.COLUMN_U_SNAME, (String) userInfo.get(Constant.COLUMN_U_SNAME));
												values.put(Constant.COLUMN_U_PWD, (String) userInfo.get(Constant.COLUMN_U_PWD));
												values.put(Constant.COLUMN_U_HEAD, Utils.blobToBytes((Blob) userInfo.get(Constant.COLUMN_U_HEAD)));
												values.put(Constant.COLUMN_U_FRIEND, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
												values.put(Constant.COLUMN_U_PHONE, (String) userInfo.get(Constant.COLUMN_U_FRIEND));
												values.put(Constant.COLUMN_U_MAIL, (String) userInfo.get(Constant.COLUMN_U_MAIL));
												values.put(Constant.COLUMN_U_RGTIME, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_RGTIME)).toString()));
												values.put(Constant.COLUMN_U_ONLINE, 1);
												values.put(Constant.COLUMN_U_POINT, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_POINT)).toString()));
												values.put(Constant.COLUMN_U_POINTHISTORY, (String) userInfo.get(Constant.COLUMN_U_POINTHISTORY));
												values.put(Constant.COLUMN_U_VIP, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIP)).toString()));
												values.put(Constant.COLUMN_U_VIPYU, Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_VIPYU)).toString()));
												values.put(Constant.COLUMN_U_CLOUDYU, Integer.parseInt(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_CLOUDYU)).toString()));
												values.put(Constant.COLUMN_U_SIGNIN, String.valueOf(userInfo.get(Constant.COLUMN_U_SIGNIN)));
												values.put(Constant.COLUMN_U_MYSTYLE, String.valueOf(userInfo.get(Constant.COLUMN_U_MYSTYLE)));

												long status_inset = db.replace(Constant.TABLE_NAME_USER, null, values);
												db.close();
												System.out.println(status_inset);
												if (status_inset >= 1) {
													Looper.prepare();
													flag = false;
													AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
															.setTitle("完成:")
															.setMessage("登录成功")
															.setPositiveButton("进入药神", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	finish();
																}
															})
															.create();
													dialog.setCancelable(false);
													if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
													{
														dialog.show();
													}
													Looper.loop();
												} else {
													Looper.prepare();
													flag = false;
													AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
															.setTitle("错误:")
															.setMessage("获取用户信息失败，请稍候重试")
															.setPositiveButton("好", null)
															.create();
													if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
													{
														dialog.show();
													}
													Looper.loop();
												}
											}
										}

									} catch (Exception e) {
										try {
											if (connection != null) {
												//事务回滚，取消当前事务
												connection.rollback();
											}
										} catch (Exception sqlException) {
											flag = false;
											sqlException.printStackTrace();
										}
										e.printStackTrace();
										Looper.prepare();
										flag = false;
										AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
												.setTitle("错误:")
												.setMessage("登陆失败，请稍候重试")
												.setPositiveButton("好", null)
												.create();
										if (!LoginActivity.this.isFinishing())//xActivity即为本界面的Activity
										{
											dialog.show();
										}

										Looper.loop();
									} finally {
										flag = false;
										JdbcUtil.close(connection, null, pps, resultSet);
									}

								}

							}
						}).start();

					}
				}
			}
		});


	}


}

