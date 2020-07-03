package com.omurgun.additionandwrite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.omurgun.additionandwrite.Introduction.PreferenceManager;
import com.omurgun.additionandwrite.Introduction.SlidePagerAdapter;
import com.omurgun.additionandwrite.Manager.HomeMenuActivity;
import com.omurgun.additionandwrite.RegisterAndLogin.RegisterMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private int[] layouts = {R.layout.first_page, R.layout.second_page, R.layout.third_page,R.layout.fourth_page};
    private SlidePagerAdapter slidePagerAdapter;
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    private Button btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        userControl();
        translucentStatusBarFlag();
        createDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);

                if (position == layouts.length - 1) {
                    btnNext.setText(R.string.start);
                    btnSkip.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setText(R.string.next);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        slidePagerAdapter = new SlidePagerAdapter(layouts, this);
        viewPager.setAdapter(slidePagerAdapter);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }
    private void createDots(int current_position) {
        if (dotsLayout != null) {
            dotsLayout.removeAllViews();
        }
        dots = new ImageView[layouts.length];

        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);

            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_dot));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], params);
        }


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnNext) {
            loadNextSlide();
        } else if (v.getId() == R.id.btnSkip) {
            goRegister();
            new PreferenceManager(this).writePreference();
        }
    }
    private void goRegister() {
        Intent intent = new Intent(MainActivity.this, RegisterMainActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadNextSlide() {
        int nextSlide = viewPager.getCurrentItem() + 1;

        if (nextSlide < layouts.length) {
            viewPager.setCurrentItem(nextSlide);
        } else {
            goRegister();
            new PreferenceManager(this).writePreference();
        }
    }
    private void translucentStatusBarFlag() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    private void userControl() {
        if (new PreferenceManager(this).checkPreference()) {
            goRegister();
        }
    }
}
