package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import Adapter.FragmentAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Button google;
    androidx.appcompat.widget.Toolbar toolbar;
    ViewPager veiwp;
    TabLayout tabl;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("My toolBar");

        tabl = findViewById(R.id.tablayout);
        veiwp = findViewById(R.id.viewPager);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        veiwp.setAdapter(adapter);
        tabl.setupWithViewPager(veiwp);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
            if(itemId == R.id.setting){
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);

            } else if (itemId == R.id.logout) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            } else if (itemId == R.id.group){
                Intent intent = new Intent(MainActivity.this, Groupchat.class);
                startActivity(intent);
            }

        return super.onOptionsItemSelected(item);
    }
}