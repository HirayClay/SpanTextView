package com.hirayclay;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by CJJ on 2017/8/30.
 *
 * @author CJJ
 */
public class Hook {
    SpanTextView target;
    private int uniformColor;
    private boolean uniformUnderLine;
    private int[] foregroundColors;
    private Map<String, String> binding;
    private boolean[] underLineSpans;
    private ClickSpanListener listener;

    public Hook(SpanTextView target) {
        this.target = target;
    }


    //bindings
    public Hook bind(Map<String, String> binding) {
        this.binding = binding;
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

    /**
     * @param underline if true ,text is underlined
     * @return
     */
    public Hook underline(boolean underline) {
        this.uniformUnderLine = underline;
        return this;
    }

    public Hook colorSpans(int... foregroundSpanColors) {
        this.foregroundColors = foregroundSpanColors;
        return this;
    }

    public Hook underLineSpans(boolean... underlineSpans) {
        this.underLineSpans = underlineSpans;
        return this;
    }


    public Hook spanClickListener(ClickSpanListener listener) {
        this.listener = listener;
        return this;
    }


    //execute according to the config
    public void make(Map<String, String> binding) {
        CharSequence template = target.getTemplateText();
        List<MarkInfo> markInfos = parseAndMark(new StringReader(template.toString()), binding);
        SpannableString spannableString = new SpannableString(parseAndSubstitute(template.toString(), binding).toString());
        composeColorSpan(spannableString, markInfos);
        composeUnderLineSpan(spannableString, markInfos);
        composeClickableSpan(spannableString, markInfos);
        target.setText(spannableString);
    }

    private void composeClickableSpan(SpannableString spannableString, List<MarkInfo> markInfos) {
        if (listener != null) {
            for (int i = 0; i < markInfos.size(); i++) {
                MarkInfo markInfo = markInfos.get(i);
                InternalClickSpan ics = new InternalClickSpan(i, markInfo.template, markInfo.value, listener);
                spannableString.setSpan(ics, markInfo.start, markInfo.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            target.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void composeUnderLineSpan(SpannableString spannableString, List<MarkInfo> markInfos) {
        int n = -1;
        for (MarkInfo m :
                markInfos) {
            n++;
            if (n > underLineSpans.length - 1)
                n--;
            if (underLineSpans[n]) {
                UnderlineSpan uls = new UnderlineSpan();
                spannableString.setSpan(uls, m.start, m.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void composeColorSpan(SpannableString spannableString, List<MarkInfo> markInfos) {
        if (foregroundColors != null && foregroundColors.length > 0) {
            int i = 0;
            int color;
            for (MarkInfo m :
                    markInfos) {
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
        } else if (isValidColor(uniformColor)) {
            for (MarkInfo m :
                    markInfos) {
                ForegroundColorSpan fcs = new ForegroundColorSpan(uniformColor);
                spannableString.setSpan(fcs, m.start, m.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
        List<MarkInfo> markInfos = new ArrayList<>();
        MarkInfo mark;
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
                            if (!key.isEmpty() && binding.containsKey(key)) {
                                String value = binding.get(key);
                                int start = writer.getBuffer().length();
                                int end = start + value.length();
                                markInfos.add(new MarkInfo(key, value, start, end));
                                writer.write(value);
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

        return markInfos;
    }

    private Writer streamingParseAndSubstitute(Reader reader, Map<String, String> binding) {
        return parseInternal(reader, binding);
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
                            if (!key.isEmpty() && binding.containsKey(key))
                                writer.write(binding.get(key));
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
        public String template;
        public int start;
        public int end;

        public MarkInfo(String template, String value, int start, int end) {
            this.template = template;
            this.value = value;
            this.start = start;
            this.end = end;
        }
    }

    public interface ClickSpanListener {
        /**
         * @param index    the template index in all the templates
         * @param template the template text
         * @param value    the replaced template's value
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
