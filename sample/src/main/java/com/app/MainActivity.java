package com.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hirayclay.Hook;
import com.hirayclay.SpanTextView;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    SpanTextView spanTextView;
    @Bind(R.id.span_textview)
    SpanTextView spanTextview;
    @Bind(R.id.radio_name)
    AppCompatRadioButton radioName;
    @Bind(R.id.radio_age)
    AppCompatRadioButton radioAge;
    @Bind(R.id.radio_height)
    AppCompatRadioButton radioHeight;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.textsize)
    TextView textsize;
    @Bind(R.id.textsizeValue)
    TextView textsizeValue;
    @Bind(R.id.textsizeUnit)
    TextView textsizeUnit;
    @Bind(R.id.colorSelector)
    TextView colorSelector;
    @Bind(R.id.nameValue)
    AppCompatEditText nameValue;
    @Bind(R.id.ageValue)
    AppCompatEditText ageValue;
    @Bind(R.id.heightValue)
    AppCompatEditText heightValue;
    @Bind(R.id.seekBar)
    SeekBar seekBar;
    @Bind(R.id.highLightColorSelector)
    TextView highLightColorSelector;
    @Bind(R.id.cacheToggle)
    ToggleButton cacheToggle;

    int[] colors = {
            R.color.blue,
            R.color.gray,
            R.color.navy,
            R.color.orange,
            R.color.purple,
            R.color.green};
    @Bind(R.id.uniformTextSize)
    TextView uniformTextSize;
    @Bind(R.id.uniformSeekBar)
    SeekBar uniformSeekBar;
    @Bind(R.id.uniformtextsizeValue)
    TextView uniformtextsizeValue;
    @Bind(R.id.uniformtextsizeUnit)
    TextView uniformtextsizeUnit;


    private int textColor;
    private int highLightTextColor = Color.TRANSPARENT;

    Map<String, String> binding = new ArrayMap<>();

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            refresh();
        }
    };

    Hook.ClickSpanListener clickSpanListener = new Hook.ClickSpanListener() {
        @Override
        public void onSpanClick(int index, String template, String value) {
            Toast.makeText(MainActivity.this, "click  " + template + " index:" + index + " value:" + value, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        spanTextView = (SpanTextView) findViewById(R.id.span_textview);
        setUpListeners();
        initDisplay();
    }


    private void setUpListeners() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                refresh();
            }
        });

        colorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newInstance().show(getSupportFragmentManager(), new ColorPickerDialog.OnColorSelectListener() {
                    @Override
                    public void onColorSelect(int color, int index) {
                        textColor = getResources().getColor(color);
                        colorSelector.setBackground(getResources().getDrawable(ds[index]));
                        refresh();
                    }
                });
            }
        });

        highLightColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newInstance().show(getSupportFragmentManager(), new ColorPickerDialog.OnColorSelectListener() {
                    @Override
                    public void onColorSelect(int color, int index) {
                        highLightTextColor = getResources().getColor(color);
                        highLightColorSelector.setBackground(getResources().getDrawable(ds[index]));
                        refresh();
                    }
                });
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textsizeValue.setText(String.valueOf(seekBar.getProgress()));
                refresh();
            }
        });

        uniformSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uniformtextsizeValue.setText(String.valueOf(uniformSeekBar.getProgress()));
                refresh();
            }
        });

        nameValue.addTextChangedListener(watcher);
        ageValue.addTextChangedListener(watcher);
        heightValue.addTextChangedListener(watcher);
    }


    @OnClick(R.id.refreshAfterChangeCache)
    public void refreshAfterChangeCache() {
        spanTextView.hook().cache(cacheToggle.isChecked()).bind(binding).make();
    }

    private void refresh() {
        int textSize = seekBar.getProgress();
        String name = nameValue.getEditableText().toString();
        String age = ageValue.getEditableText().toString();
        String height = heightValue.getEditableText().toString();
        binding.put("name", name);
        binding.put("age", age);
        binding.put("height", height);
        AppCompatRadioButton button = (AppCompatRadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        String key = button.getText().toString();


        Hook hook = spanTextView.hook();

        hook.bind(binding)
                .cache(cacheToggle.isChecked())
                .uniformSpanTextSize(uniformSeekBar.getProgress())
                .highLightColor(highLightTextColor)
                .spanClickListener(clickSpanListener)
                .image("avatar",getResources().getDrawable(R.drawable.girl))
                .textSize(key, textSize);
        if (textColor != 0)
            hook.uniformColor(textColor);
        hook.make();

    }

    private void initDisplay() {
        Map<String, String> binding = new ArrayMap<>();
        binding.put("name", "Alice");
        binding.put("age", "18");
        binding.put("height", "180");

        spanTextView.hook()
                .uniformColor(Color.GREEN)
                .underLineSpans(true, false, true)
                .spanTextColor(Color.RED, Color.CYAN)//the third key "height" textcolor is omit ,will be set with uniformColor
                .highLightColor(Color.RED)
                .textSize(0, 25)
                .textSize("height", 25)
                .image("avatar",getResources().getDrawable(R.drawable.girl))
                .spanClickListener(new Hook.ClickSpanListener() {
                    @Override
                    public void onSpanClick(int index, String template, String value) {
                        Toast.makeText(MainActivity.this, "click  " + template + " index:" + index + " value:" + value, Toast.LENGTH_SHORT).show();
                    }
                })
                .make(binding);
    }

    int[] ds = {
            R.drawable.blue_circle,
            R.drawable.gray_circle,
            R.drawable.navy_circle,
            R.drawable.orange_circle,
            R.drawable.purple_circle,
            R.drawable.green_circle};


}
