package com.eventsearch.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TabLayout tabLayout2;
    private ViewPager2 viewPager2;

    public static HashMap<String, Event> favoriteEvents = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadFavoriteEvents(this);
        // Enable the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        tabLayout2 = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Return the fragment for the corresponding tab.
                switch (position) {
                    case 0:
                        return new SearchFragment();
                    case 1:
                        return new FavoritesFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout2, viewPager2, (tab, position) -> {
            // Set the text for the tabs.
            switch (position) {
                case 0:
                    tab.setText("Search");
                    break;
                case 1:
                    tab.setText("Favorites");
                    break;
            }
        }).attach();

        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("HomeActivity", "onTabSelected: " + tab.getPosition());
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static void saveFavoriteEvents(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(HomeActivity.favoriteEvents);
        editor.putString("favoriteEvents", json);
        editor.apply();
    }

    public static void loadFavoriteEvents(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favoriteEvents", "");
        Type type = new TypeToken<HashMap<String, Event>>() {}.getType();
        HomeActivity.favoriteEvents = gson.fromJson(json, type);
        if (HomeActivity.favoriteEvents == null) {
            HomeActivity.favoriteEvents = new HashMap<>();
        }
    }




}
