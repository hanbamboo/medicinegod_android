package com.daqin.medicinegod;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.daqin.medicinegod.Fragment.SearchResultLocalFragment;
import com.daqin.medicinegod.Fragment.SearchResultWebFragment;
import com.daqin.medicinegod.Utils.Utils;
import com.daqin.medicinegod.databinding.ActivitySearchResultBinding;
import com.google.android.material.tabs.TabLayout;

/**
 * 搜索分页
 */
public class SearchResultActivity extends AppCompatActivity {

    private ActivitySearchResultBinding binding;
    View root;


    
    private TabLayout tabLayout = null;

    private ViewPager viewPager;

    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];

    String searchKey = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        tabLayout = (TabLayout) findViewById(R.id.id_searchres_tablayout);
        viewPager = (ViewPager) findViewById(R.id.id_searchres_viewpager);
        initView();
        TextView sk_title = root.findViewById(R.id.id_searchres_title);
        ImageButton sk_btn_back = root.findViewById(R.id.id_searchres_back);
        sk_btn_back.setOnClickListener(l -> {
            finish();
        });
        searchKey = Utils.getString(getApplicationContext(), Constant.SEARCHKEY, "none");
        if (searchKey.equals("none")) {
            finish();
        } else {
            sk_title.setText("搜索" + searchKey + "的结果");
        }




    }
    private void initView() {
        mTabTitles[0] = "本地搜索";
        mTabTitles[1] = "网络搜索";
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        mFragmentArrays[0] = SearchResultLocalFragment.newInstance();
        mFragmentArrays[1] = SearchResultWebFragment.newInstance();
        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tabLayout.setupWithViewPager(viewPager);

    }
    final class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }


        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];

        }
    }


}

