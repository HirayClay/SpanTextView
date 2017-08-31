package com.app;

import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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
        binding.put("height","180");

        spanTextView.hook()
                .uniformColor(Color.GREEN)
                .underLineSpans(true,false,true)
                .colorSpans(Color.RED, Color.CYAN)//the second template "height" textcolor is omit ,will be set with uniformColor
                .highLightColor(Color.RED)
                .spanClickListener(new Hook.ClickSpanListener() {
                    @Override
                    public void onSpanClick(int index, String template, String value) {
                        Toast.makeText(MainActivity.this,"click  "+template+" index:"+index+" value:"+value,Toast.LENGTH_SHORT).show();
                    }
                })
                .make(binding);
    }
}
