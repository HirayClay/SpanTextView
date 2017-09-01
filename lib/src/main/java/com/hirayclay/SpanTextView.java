package com.hirayclay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.util.AttributeSet;

import java.lang.reflect.Method;

/**
 * @author CJJ
 */

public class SpanTextView extends AppCompatTextView {
    Method spanClickMethod;
    private static final String TAG = "SpanTextView";
    private SpannableString spannableString;
    private String handlerName;
    private int color;
    private CharSequence templateText;
    private boolean cache;

    public SpanTextView(Context context) {
        super(context);
    }

    public SpanTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SpanTextView);
        color = array.getColor(R.styleable.SpanTextView_spanColor, Color.GRAY);
        handlerName = array.getString(R.styleable.SpanTextView_spanClick);
        array.recycle();
//        defaultHook();
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


    //默认xml布局中的样式只是改变${}包裹的文字的样式，不是替换绑定
    private void defaultHook() {

    }

    public void setCache(boolean cache) {
        this.cache = cache;
        if (cache)
        instance = beforeCacheInstance;
        else beforeCacheInstance = null;
//        beforeCacheInstance = null;
    }


    private static final class DefaultHook {

    }

    Hook instance;
    Hook beforeCacheInstance;

    public Hook hook() {
        if (cache) {
            if (instance != null) {
                return instance;
            }else if (beforeCacheInstance !=null)
                return instance = beforeCacheInstance;
            return instance = new Hook(this);
        }
        return beforeCacheInstance = new Hook(this);
    }
//    private void transform() {
//        String text = getText().toString();
//        int start = 0;
//        int end = 0;
//        int length = text.length();
//        start = text.indexOf("{");
//        if (start != -1)//没有转义字符
//            end = text.indexOf("}");
//        if (start >= 0 & end > 0) {
//            String str = text.substring(0, start)
//                    + text.substring(start + 1, end)
//                    + (end == length - 1 ? "" : text.substring(end + 1));
//            spannableString = new SpannableString(str);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
//            spannableString.setSpan(colorSpan, start, end - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            ClickableSpan clickableSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    if (spanClickMethod == null)
//                        spanClickMethod = resolveMethod(handlerName);
//                    if (spanClickMethod != null)
//                        try {
//                            spanClickMethod.invoke(getContext(), widget);
//                        } catch (IllegalAccessException | InvocationTargetException e) {
//                            e.printStackTrace();
//                        }
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                    ds.setColor(color);
//                }
//            };
//            setMovementMethod(LinkMovementMethod.getInstance());
//            setHighlightColor(Color.parseColor("#00000000"));
//            spannableString.setSpan(clickableSpan, start, end - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            setText(spannableString);
//        }
////    }
////
////    @Override
////    public void setText(CharSequence text, TextView.BufferType type) {
////        super.setText(text, type);
////        transform();
////    }
//
////    private Method resolveMethod(String handlerName) {
////        Context context = getContext();
////        while (context != null) {
////            try {
////                if (!context.isRestricted()) {
////                    return context.getClass().getMethod(handlerName, View.class);
////                }
////            } catch (NoSuchMethodException e) {
////                // Failed to find method, keep searching up the hierarchy.
////            }
////
////            if (context instanceof ContextWrapper) {
////                context = ((ContextWrapper) context).getBaseContext();
////            } else {
////                // Can't search up the hierarchy, null out and fail.
////                context = null;
////            }
////        }
////        return null;
////    }
//}
}