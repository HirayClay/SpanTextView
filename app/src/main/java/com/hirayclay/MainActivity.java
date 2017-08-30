package com.hirayclay;

import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Map;

public class MainActivity extends AppCompatActivity {


    SpanTextView spanTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spanTextView = (SpanTextView) findViewById(R.id.span_textview);


        Map<String, String> binding = new ArrayMap<>();
        binding.put("name", "Alice");
        binding.put("age", "18");

        spanTextView.hook()
                .colorSpans(new int[]{Color.YELLOW, Color.CYAN})
                .make(binding);
    }
}
