# SpanTextView

days ago,i was learning groovy,when i meet "Template".i realize a old project may be brought to life again,
that is it! After reading the template source code in groovy ,I use template in this project like groovy's template.
Comparing to  setting Android's various Span,set span with template is easier!

# Usage

```
        <com.hirayclay.SpanTextView
            android:id="@+id/span_textview"
            app:spanClick="spanClick"
            android:text="${name} now is ${age} years old!"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
```

in your code to control the style:
```
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
```
and then the effect is like this below:
