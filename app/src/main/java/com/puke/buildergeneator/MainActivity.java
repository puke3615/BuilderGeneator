package com.puke.buildergeneator;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dog dog = new DogBuilder()
                .configureAge(1)
                .configureSex("男")
                .configureCustomName("名字")
                .build();
    }
}
