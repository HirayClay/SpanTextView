package com.hirayclay;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.util.ArrayMap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageSwitcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by CJJ on 2017/8/30.
 *
 * @author CJJ
 */
public class Hook {
    private SpanTextView target;
    private int uniformColor;
    private int[] foregroundColors;
    private Map<String, String> binding;
    private boolean[] underLineSpans;
    private ClickSpanListener listener;
    private int highLightColor = Color.TRANSPARENT;
    private int textSize = -1;
    private Map<Integer, Integer> textSizeMap = new ArrayMap<>();
    private Map<String, Integer> unProcessedTextSize = new ArrayMap<>();
    private Map<String, Drawable> drawableMap = new ArrayMap<>();

    public Hook(SpanTextView target) {
        this.target = target;
        this.uniformColor = target.getCurrentTextColor();
    }

    /**
     * @param c if set true Hook will be single instance,when call {@link SpanTextView#hook()}next
     *          time ,the older Hook will be returned.If you want reset to default behavior(create new one every time)
     *          just call {@link SpanTextView#setCache(boolean)} and set false.
     *          will not created a new one except first call
     * @return {@link Hook}
     */

    public Hook cache(boolean c) {
        this.target.setCache(c);
        return this;
    }

    //createTemplate
    public Hook template(String template) {
        this.target.setTemplateText(template);
        return this;
    }

    //ForegroundSpan

    /**
     * style all the text in same color
     *
     * @param color
     * @return
     */
    public Hook uniformColor(@ColorInt int color) {
        this.uniformColor = color;
        return this;
    }

    public Hook spanTextColor(int... foregroundSpanColors) {
        this.foregroundColors = foregroundSpanColors;
        return this;
    }

    public Hook underLineSpans(boolean... underlineSpans) {
        this.underLineSpans = underlineSpans;
        return this;
    }

    public Hook highLightColor(@ColorInt int highLightColor) {
        this.highLightColor = highLightColor;
        return this;
    }

    public Hook spanClickListener(ClickSpanListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * set the  textSize of all spans
     *
     * @param textSize unit is "sp"
     * @return Hook
     */
    public Hook uniformSpanTextSize(int textSize) {
        this.textSize = sp(textSize);
        return this;
    }

    public Hook textSize(int index, int textSize) {
        textSizeMap.put(index, sp(textSize));
        return this;
    }

    public Hook textSize(String key, int textSize) {
        unProcessedTextSize.put(key, sp(textSize));
        return this;
    }

    public Hook bind(Map<String, String> binding) {
        this.binding = binding;
        return this;
    }

    public Hook image(String key, Drawable drawable) {
        drawableMap.put(key, drawable);
        return this;
    }

    /**
     * @param drawableMap the key and the corresponding drawable
     * @return
     */
    public Hook images(Map<String, Drawable> drawableMap) {
        this.drawableMap.putAll(drawableMap);
        return this;
    }


    /**
     * @param drawableRes the key and the drawable resource id
     * @return
     */
    public Hook imageRes(Map<String, Integer> drawableRes) {
        drawableMap.putAll(wrap(drawableRes));
        return this;
    }

    private Map<String, Drawable> wrap(Map<String, Integer> drawableRes) {
        Map<String, Drawable> drawables = new ArrayMap<>();
        Iterator<String> iterator = drawableRes.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            int id = drawableRes.get(key);
            if (Build.VERSION.SDK_INT >= 21)
                drawables.put(key, target.getResources().getDrawable(id, null));
            else drawables.put(key, target.getResources().getDrawable(id));

        }
        return drawables;
    }


    Map<String, List<Object>> appliedSpan = new ArrayMap<>();

    /**
     * @param key  the key in template string
     * @param span your Span instance you want to be applied on the key
     * @return Hook
     */
    public Hook apply(String key, Object span) {
        List<Object> list = appliedSpan.get(key);
        if (list != null)
            list.add(span);
        else {
            list = new ArrayList<>(2);
            list.add(span);
            appliedSpan.put(key, list);
        }
        return this;
    }

    public void make() {
//        if (this.binding != null)
        make(binding);
//        else throw new IllegalStateException("binding is not set before call make() method");
    }

    //execute according to the binding
    public void make(Map<String, String> binding) {
        this.binding = binding;
        CharSequence template = target.getTemplateText();
        List<MarkInfo> markers = parseAndMark(new StringReader(template.toString()), binding);
        processTextAfterMark(markers);
        SpannableString spannableString = new SpannableString(parseAndSubstitute(template.toString(), binding).toString());
        composeColorSpan(spannableString, markers);
        composeUnderLineSpan(spannableString, markers);
        composeClickableSpan(spannableString, markers);
        composeTextSize(spannableString, markers);
        composeImageSpan(spannableString, markers);
        applySpan(spannableString,markers);
        target.setText(spannableString);
    }

    private void applySpan(SpannableString spannableString, List<MarkInfo> markers) {

    }

    private void processTextAfterMark(List<MarkInfo> markers) {
        if (!unProcessedTextSize.isEmpty()) {
            for (int i = 0; i < markers.size(); i++) {
                MarkInfo m = markers.get(i);
                String key = m.key;
                Integer size = unProcessedTextSize.get(key);
                if (size != null)
                    textSizeMap.put(i, size);

            }
            unProcessedTextSize.clear();
        }
    }

    private void composeImageSpan(SpannableString spannableString, List<MarkInfo> markers) {
        for (int i = 0; i < markers.size(); i++) {
            MarkInfo markInfo = markers.get(i);

            Drawable drawable = drawableMap.get(markInfo.key);
            if (drawable != null) {
                if (drawable.getBounds().width() == 0 || drawable.getBounds().height() == 0) {
                    drawable.setBounds(0, 0, (int) target.getTextSize(), (int) target.getTextSize());
                }
                ImageSpan is = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                spannableString.setSpan(is, markInfo.start, markInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }

        }
    }

    private void composeTextSize(SpannableString spannableString, List<MarkInfo> markers) {
        for (int i = 0; i < markers.size(); i++) {
            MarkInfo markInfo = markers.get(i);
            //check ImageSpan
            if (markInfo.value != null) {
                Integer textSize = textSizeMap.get(i);
                if (textSize != null) {
                    AbsoluteSizeSpan ass = new AbsoluteSizeSpan(textSize);
                    spannableString.setSpan(ass, markInfo.start, markInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    if (this.textSize > 0) {
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(this.textSize);
                        spannableString.setSpan(ass, markInfo.start, markInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }

            }
        }
    }

    private void composeClickableSpan(SpannableString spannableString, List<MarkInfo> markers) {
        if (listener != null) {
            for (int i = 0; i < markers.size(); i++) {
                MarkInfo markInfo = markers.get(i);

                //check ImageSpan
                if (markInfo.value != null) {

                    InternalClickSpan ics = new InternalClickSpan(i, markInfo.key, markInfo.value, listener);
                    spannableString.setSpan(ics, markInfo.start, markInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            target.setMovementMethod(LinkMovementMethod.getInstance());
            target.setHighlightColor(highLightColor);
        }
    }

    private void composeUnderLineSpan(SpannableString spannableString, List<MarkInfo> markers) {
        if (underLineSpans == null)
            return;
        int n = -1;
        for (MarkInfo m :
                markers) {
            n++;
            //check ImageSpan
            if (m.value != null) {

                if (n > underLineSpans.length - 1)
                    n--;
                if (underLineSpans[n]) {
                    UnderlineSpan uls = new UnderlineSpan();
                    spannableString.setSpan(uls, m.start, m.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private void composeColorSpan(SpannableString spannableString, List<MarkInfo> markers) {
        if (foregroundColors != null && foregroundColors.length > 0) {
            int i = 0;
            int color;
            for (MarkInfo m :
                    markers) {
                //check ImageSpan
                if (m.value != null) {

                    if (i > foregroundColors.length - 1) {
                        if (isValidColor(uniformColor))
                            color = uniformColor;
                        else color = target.getCurrentTextColor();

                    } else
                        color = foregroundColors[i];
                    ForegroundColorSpan fcs = new ForegroundColorSpan(color);
                    spannableString.setSpan(fcs, m.start, m.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    i++;
                }
            }
        } else if (isValidColor(uniformColor)) {
            for (MarkInfo m :
                    markers) {
                //check ImageSpan
                if (m.value != null) {

                    ForegroundColorSpan fcs = new ForegroundColorSpan(uniformColor);
                    spannableString.setSpan(fcs, m.start, m.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        } else {
            //ignored
        }
    }

    private String findKey(Reader reader) {
        StringWriter stringBuilder = new StringWriter(10);
        int c;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '}')
                    break;
                else stringBuilder.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //标记出模版在解析过的字符串中的下标，方便后续施加乱七八糟的Span
    private List<MarkInfo> parseAndMark(Reader reader, Map<String, String> binding) {
        if (!reader.markSupported())
            reader = new BufferedReader(reader);
        List<MarkInfo> markers = new ArrayList<>();
        StringWriter writer = new StringWriter(50);
        while (true) {
            int c;
            try {
                if ((c = reader.read()) != -1) {
                    if (c == '$') {
                        reader.mark(1);
                        c = reader.read();
                        if (c == '{') {
                            String key = findKey(reader);
                            //only true for text
                            if (!key.isEmpty()) {
                                String value = binding.get(key);
                                //for text
                                if (value != null) {
                                    int start = writer.getBuffer().length();
                                    int end = start + value.length();
                                    markers.add(new MarkInfo(key, value, start, end));
                                    writer.write(value);
                                } else {//for image

                                    int start = writer.getBuffer().length();
                                    int end = start + key.length();
                                    markers.add(new MarkInfo(key, null, start, end));
                                    writer.write(key);
                                }
                            } else {
                                writer.write("${");
                                //key not found
                                writer.write(key);
                            }

                        } else {
                            writer.write('$');
                            reader.reset();
                        }
                    } else {
                        writer.write(c);
                    }
                } else break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return markers;
    }

    private Writer parseAndSubstitute(String template, Map<String, String> binding) {
        Reader reader = new StringReader(template);
        return parseInternal(reader, binding);

    }

    private Writer parseInternal(Reader reader, Map<String, String> binding) {
        StringWriter writer = new StringWriter(50);
        if (!reader.markSupported())
            reader = new BufferedReader(reader);
        while (true) {
            int c;
            try {
                if ((c = reader.read()) != -1) {
                    if (c == '$') {
                        reader.mark(1);
                        c = reader.read();
                        if (c == '{') {
                            String key = findKey(reader);
                            if (!key.isEmpty() && binding != null /*&& binding.containsKey(key)*/)
                                if (binding.get(key) != null)
                                    writer.write(binding.get(key));
                                else writer.write(key);
                            else {
                                writer.write("${");
                                //key not found
                                writer.write(key);
                            }
                        } else {
                            writer.write('$');
                            reader.reset();
                        }
                    } else {
                        writer.write(c);
                    }

                } else break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return writer;

    }

    private int sp(int spTextSize) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spTextSize, target.getResources().getDisplayMetrics());
    }

    private boolean isValidColor(int color) {
        return componentValid(Color.red(color)) &&
                componentValid(Color.green(color)) &&
                componentValid(Color.blue(color));
    }

    private boolean componentValid(int comp) {
        return comp >= 0 && comp < 256;
    }

    private static final class MarkInfo {
        public String value;
        public String key;
        public int start;
        public int end;

        public MarkInfo(String key, String value, int start, int end) {
            this.key = key;
            this.value = value;
            this.start = start;
            this.end = end;
        }
    }

    public interface ClickSpanListener {
        /**
         * @param index    the key position in all the keys
         * @param template the key name
         * @param value    the replaced key's value
         */
        void onSpanClick(int index, String template, String value);
    }

    private final class InternalClickSpan extends ClickableSpan {
        int index;
        public String value;
        public String template;
        ClickSpanListener listener;

        public InternalClickSpan(int index, String template, String value, ClickSpanListener listener) {
            this.index = index;
            this.template = template;
            this.value = value;
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            listener.onSpanClick(index, template, value);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            super.updateDrawState(ds);
        }
    }
}
