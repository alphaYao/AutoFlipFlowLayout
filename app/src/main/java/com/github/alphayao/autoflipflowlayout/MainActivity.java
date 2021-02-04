package com.github.alphayao.autoflipflowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.alphayao.autoflipflowlayoutlib.HotAskLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HotAskLayout hotAsk;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hotAsk = findViewById(R.id.hotAsk);

        for (int i = 0; i < 5; i++) {
            list.add("Where can i find bathroom");
            list.add("Weather");
            list.add("What is your name");
            list.add("What time does the show start");
            list.add("What is your name");
        }

        hotAsk.postDelayed(new Runnable() {
            @Override
            public void run() {
                hotAsk.initData(list);
            }
        }, 200);

    }
}