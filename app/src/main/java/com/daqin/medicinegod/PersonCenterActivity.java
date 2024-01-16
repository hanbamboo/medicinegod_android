package com.daqin.medicinegod;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daqin.medicinegod.CustomWidget.RoundImageView;
import com.daqin.medicinegod.Utils.JdbcUtil;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.ActivityPersonBinding;
import com.mysql.jdbc.Util;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class PersonCenterActivity extends AppCompatActivity {


	private ActivityPersonBinding binding;
	View root;
	Uri uripath = null;
	static String mailCode = "xxxxxx";
	final int REQUEST_CODE_CHOOSE_IMG = 100;
	final int REQUEST_CODE_CROP = 102;
	Map<String, Object> userInfo = new HashMap<>();
	String lname;
	int status_head = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityPersonBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		root = binding.getRoot();
		lname = Utils.getString(this, Constant.MG_LOGIN_LNAME, "0");
		if (lname.equals("0")) {
			finish();
		}
		RoundImageView id_person_head = binding.idPersonHead;
		ImageButton id_person_default = binding.idPersonDefault;
		ImageButton id_person_crop = binding.idPersonCrop;
		EditText id_person_pwd = binding.idPersonPwd;
		EditText id_person_sname = binding.idPersonSname;
		EditText id_person_mystyle = binding.idPersonMystyle;
		EditText id_person_mail = binding.idPersonMail;
		EditText id_person_rgtime = binding.idPersonRgtime;

		Button id_person_save_head = binding.idPersonSaveHead;
		Button id_person_save_pwd = binding.idPersonSavePwd;
		Button id_person_save_sname = binding.idPersonSaveSname;
		Button id_person_save_mystyle = binding.idPersonSaveMystyle;
		id_person_save_head.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Connection c = null;
						PreparedStatement p = null;
						try {
							byte[] head = Utils.getBytesFromBitmap(Utils.getBitmapFromView(id_person_head));
							c = JdbcUtil.getConnection();
							p = c.prepareStatement("update USERINFO set HEAD = ? where LNAME = ?");
							p.setBytes(1, head);
							p.setString(2, lname);
							int s = p.executeUpdate();
							if (s >= 1) {
								Looper.prepare();
								Toast.makeText(PersonCenterActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
								Utils.putString(PersonCenterActivity.this, "EDITPERSON", "ok");

								Looper.loop();
							}
						} catch (Exception sqlException) {
							sqlException.printStackTrace();
						} finally {
							JdbcUtil.close(c, null, p, null);
						}
					}
				}).start();
			}
		});
		id_person_save_pwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (id_person_pwd.getText().length() != 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Connection c = null;
							PreparedStatement p = null;
							try {
								String pwd = Utils.getSerect(lname, id_person_pwd.getText().toString().trim());
								c = JdbcUtil.getConnection();
								p = c.prepareStatement("update USERINFO set PWD = ? where LNAME = ?");
								p.setString(1, pwd);
								p.setString(2, lname);
								int s = p.executeUpdate();
								if (s >= 1) {
									Looper.prepare();
									Toast.makeText(PersonCenterActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
									Utils.putString(PersonCenterActivity.this, "EDITPERSON", "ok");

									Looper.loop();
								}
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
		id_person_save_mystyle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (id_person_mystyle.getText().length() != 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Connection c = null;
							PreparedStatement p = null;
							try {
								String mystyle = id_person_mystyle.getText().toString().trim();
								c = JdbcUtil.getConnection();
								p = c.prepareStatement("update USERINFO set MYSTYLE = ? where LNAME = ?");
								p.setString(1, mystyle);
								p.setString(2, lname);
								int s = p.executeUpdate();
								if (s >= 1) {
									Looper.prepare();
									Toast.makeText(PersonCenterActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
									Utils.putString(PersonCenterActivity.this, "EDITPERSON", "ok");

									Looper.loop();
								}
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
		id_person_save_sname.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (id_person_sname.getText().length() != 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Connection c = null;
							PreparedStatement p = null;
							try {
								String sname = id_person_sname.getText().toString().trim();
								c = JdbcUtil.getConnection();
								p = c.prepareStatement("update USERINFO set SNAME = ? where LNAME = ?");
								p.setString(1, sname);
								p.setString(2, lname);
								int s = p.executeUpdate();
								if (s >= 1) {
									Looper.prepare();
									Toast.makeText(PersonCenterActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
									Utils.putString(PersonCenterActivity.this, "EDITPERSON", "ok");

									Looper.loop();
								}
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
		final DatabaseHelper dbHelper = new DatabaseHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db != null) {
			Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
			while (cursor.moveToNext()) {
				userInfo.put(Constant.COLUMN_U_LNAME, lname);
				userInfo.put(Constant.COLUMN_U_SNAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SNAME)));
				userInfo.put(Constant.COLUMN_U_PWD, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_PWD)));
				userInfo.put(Constant.COLUMN_U_HEAD, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_HEAD)));
				userInfo.put(Constant.COLUMN_U_FRIEND, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_FRIEND)));
				userInfo.put(Constant.COLUMN_U_PHONE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_PHONE)));
				userInfo.put(Constant.COLUMN_U_MAIL, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_MAIL)));
				userInfo.put(Constant.COLUMN_U_RGTIME, Long.parseLong(Objects.requireNonNull(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_RGTIME)))));
				userInfo.put(Constant.COLUMN_U_ONLINE, 1);
				userInfo.put(Constant.COLUMN_U_POINT, Integer.parseInt(Objects.requireNonNull(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_POINT)))));
				userInfo.put(Constant.COLUMN_U_POINTHISTORY, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_POINTHISTORY)));
				userInfo.put(Constant.COLUMN_U_VIP, Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_VIP))));
				userInfo.put(Constant.COLUMN_U_VIPYU, Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_VIPYU))));
				userInfo.put(Constant.COLUMN_U_CLOUDYU, Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_CLOUDYU))));
				userInfo.put(Constant.COLUMN_U_SIGNIN, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SIGNIN)));
				userInfo.put(Constant.COLUMN_U_MYSTYLE, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_MYSTYLE)));

			}
			cursor.close();
		}
		id_person_pwd.setHint("只允许数字、字母、_ @ . 三种符号");
		id_person_pwd.setFilters(new InputFilter[]{new InputFilter() {
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
		id_person_sname.setHint((String) userInfo.get(Constant.COLUMN_U_SNAME));
		id_person_sname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		id_person_mystyle.setHint((String) userInfo.get(Constant.COLUMN_U_MYSTYLE));
		id_person_mystyle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
		String mail = (String) userInfo.get(Constant.COLUMN_U_MAIL);
		id_person_rgtime.setHint(Utils.getStringFromDate(Long.parseLong(Objects.requireNonNull(userInfo.get(Constant.COLUMN_U_RGTIME)).toString())));
		id_person_mail.setHint(mail.substring(0, 3) + "****@**.com");
		id_person_head.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
		id_person_head.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMG);//打开相册
			}
		});
		id_person_default.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (status_head) {
					case 0:
						id_person_head.setImageResource(R.mipmap.me_woman_default);
						id_person_default.setImageResource(R.mipmap.me_man_default);
						status_head = 1;
						break;
					case 1:
						id_person_head.setImageResource(R.mipmap.me_man_default);
						id_person_default.setImageResource(R.mipmap.me_woman_default);
						status_head = 0;
						break;
					case 2:
						id_person_head.setImageResource(R.mipmap.me_man_default);
						id_person_default.setImageResource(R.mipmap.me_woman_default);
						status_head = 0;
						uripath = null;
						break;
				}
			}
		});
		id_person_crop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startImageCrop(uripath);
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		RoundImageView b_chooseImg = binding.idPersonHead;
		ImageButton id_register_head_default = binding.idPersonDefault;
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
					Toast.makeText(PersonCenterActivity.this, "解析图片失败，请重试", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(PersonCenterActivity.this, "裁剪图片失败，请重试", Toast.LENGTH_SHORT).show();
				}

			}

		}
	}

	//剪切图片
	//originUri--原始图片的Uri；
	//mDestinationUri--目标裁剪的图片保存的Uri
	private void startImageCrop(Uri uri) {
		if (uri == null) {
			AlertDialog dialog = new AlertDialog.Builder(PersonCenterActivity.this)
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

