package com.hirayclay;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.util.AttributeSet;

/**
 * @author CJJ
 */

public class SpanTextView extends AppCompatTextView {
    private static final String TAG = "SpanTextView";
    private CharSequence templateText;
    private boolean cache;

    public SpanTextView(Context context) {
        super(context);
    }

    public SpanTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SpanTextView);
        array.recycle();
    }

    public CharSequence getTemplateText() {
        return templateText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (!(text instanceof SpannableString))
            templateText = text;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
        if (cache)
        instance = beforeCacheInstance;
        else beforeCacheInstance = null;
    }


    SpanHelper instance;
    SpanHelper beforeCacheInstance;

    public SpanHelper hook() {
        if (cache) {
            if (instance != null) {
                return instance;
            }else if (beforeCacheInstance !=null)
                return instance = beforeCacheInstance;
            return instance = new SpanHelper(this);
        }
        return beforeCacheInstance = new SpanHelper(this);
    }

    public void setTemplateText(String templateText) {
        this.templateText = templateText;
    }
}