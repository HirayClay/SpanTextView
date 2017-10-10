# SpanTextView

days ago,i was learning groovy,when i meet "Template".i realize a old project may be brought to life again,
that is it! I use template in this project like groovy's template.

# Usage

```
 compile 'com.hiray.spantextview:lib:1.0.0'
```

```
        <com.hirayclay.SpanTextView
            android:id="@+id/span_textview"
            app:spanClick="spanClick"
            android:text="${name} now is ${age} years old!"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
```

control the style with java code:
```
        spanTextView = (SpanTextView) findViewById(R.id.span_textview);

        Map<String, String> binding = new ArrayMap<>();
        binding.put("name", "Alice");
        binding.put("age", "18");

        spanTextView.hook()
                .underLineSpans(true,false,true)
                .colorSpans(Color.RED, Color.CYAN)
                .make(binding);
```
and then the effect is like this below:
