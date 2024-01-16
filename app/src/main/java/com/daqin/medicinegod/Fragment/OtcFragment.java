package com.daqin.medicinegod.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.daqin.medicinegod.Constant;
import com.daqin.medicinegod.R;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.data.DatabaseHelper;
import com.daqin.medicinegod.databinding.FragmentOtcBinding;
import com.daqin.medicinegod.databinding.FragmentOutBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class OtcFragment extends Fragment {

    private FragmentOtcBinding binding;
    View root;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private String lname="0";
    Map<String, Object> userInfo = new HashMap<>();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtcBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        drawer = binding.idOtcDrawerlayout;
        navigationView = binding.navViewOtc;
        ImageButton btn_more = binding.idOtcMore;
        btn_more.setOnClickListener(l -> {
            //打开滑动菜单  左侧出现
            drawer.openDrawer(GravityCompat.START);
        });
        Fragment fragment = requireActivity().getSupportFragmentManager().getFragments().get(0);
        navController = Navigation.findNavController(fragment.requireView());
        navigationView.setCheckedItem(R.id.navigation_otc_title);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                navController.navigate(item.getItemId());
//                navController.navigate(R.id.navigation_me_title);

                return false;
            }
        });


        TextView nav_header_version = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_version);

        //软件内置版本
        nav_header_version.setText("版本: V" + Constant.VERSION + Constant.VNAME);


        TextView nav_main_header_usercloud = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_usercloud);
        ImageView nav_header_img = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_imageView);
        TextView nav_main_header_username = navigationView.getHeaderView(0).findViewById(R.id.nav_main_header_username);
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

        //登录状态，已登录的话会有数据
        int isLogin = Utils.getInt(requireContext(), Constant.MG_LOGIN, 0);
        lname = Utils.getString(requireContext(), Constant.MG_LOGIN_LNAME, "0");

        if (isLogin == 1 && !lname.equals("0")) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            if (db != null) {
                Cursor cursor = db.rawQuery("select * from " + Constant.TABLE_NAME_USER + " where " + Constant.COLUMN_U_LNAME + " = ?", new String[]{lname});
                while (cursor.moveToNext()) {
                    userInfo.put(Constant.COLUMN_U_LNAME, lname);
                    userInfo.put(Constant.COLUMN_U_SNAME, cursor.getString(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_SNAME)));
                    userInfo.put(Constant.COLUMN_U_HEAD, cursor.getBlob(cursor.getColumnIndexOrThrow(Constant.COLUMN_U_HEAD)));
                }
                cursor.close();
                if (!userInfo.toString().equals("{}") && userInfo != null) {
                    nav_main_header_username.setText((String) userInfo.get(Constant.COLUMN_U_SNAME));
                    nav_header_img.setImageBitmap(Utils.getBitmapFromByte((byte[]) userInfo.get(Constant.COLUMN_U_HEAD)));
                }
                cursor.close();
                db.close();
            }

            if (db != null) {
                db.close();
            }
        } else {
            nav_main_header_username.setText("MedicineGod药神");
            nav_main_header_usercloud.setText("暂未开启多端共享( - )");
            nav_header_img.setImageResource(R.mipmap.icon);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}