package com.example.jrpotter.appetite.login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.jrpotter.appetite.R;


/**
 * Represents the main portal for a user who has not yet created an account or logged in.
 * It will try and save the user's credentials so that the user does not have to repeatedly
 * attempt to login.
 */
public class LoginActivity extends ActionBarActivity implements
    ViewPager.OnPageChangeListener {

    // Hook Methods
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup tutorial slides
        ViewPager pager = (ViewPager) findViewById(R.id.activity_tutorial_viewpager);
        pager.setAdapter(new TutorialSlidePagerAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);
    }


    // Transition Methods
    // ==================================================

    public void signIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }


    // Tutorial Page Adapter
    // ==================================================

    private class TutorialSlidePagerAdapter extends FragmentStatePagerAdapter {

        public TutorialSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return TutorialSlideFragment.newInstance(position);
        }

        @Override
        public int getCount() {

            return TutorialSlideFragment.slideText.length;
        }
    }


    // Tutorial Page Listener
    // ==================================================

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LinearLayout indicators = (LinearLayout) findViewById(R.id.activity_login_tutorial_indicator);
        for(int i = 0; i < indicators.getChildCount(); i++) {
            View v = indicators.getChildAt(i);
            if(i == position) {
                v.setBackground(getResources().getDrawable(R.drawable.shape_selected_indicator));
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.shape_unselected_indicator));
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        // Intentionally Empty
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Intentionally Empty
    }

}
