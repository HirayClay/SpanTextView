package com.hirayclay;

import android.support.annotation.ColorInt;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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
    private int uniformColorSpan = -1;

    public Hook(SpanTextView target) {
        this.target = target;
    }

    //ForegroundSpan

    /**
     * style all the text in same color
     *
     * @param color
     * @return
     */
    public Hook color(@ColorInt int color) {
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


    public Hook colorSpan(int forgroundSpanColor) {
        this.uniformColorSpan = forgroundSpanColor;
        return this;
    }


    //execute according to the config
    public void make(Map<String, String> binding) {
        CharSequence template = target.getTemplateText();
        List<MarkInfo> markInfos = parseAndMark(new StringReader(template.toString()), binding);
        SpannableString spannableString = new SpannableString(template);
        if (uniformColorSpan >= 0 && uniformColorSpan <= 256) {

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

    //标记出包裹文字在解析过的字符串中的下标，方便后续施加乱七八糟的Span
    private List<MarkInfo> parseAndMark(Reader reader, Map<String, String> binding) {
        if (!reader.markSupported())
            reader = new BufferedReader(reader);
        List<MarkInfo> markInfos = new ArrayList<>();
        MarkInfo mark;
        StringWriter writer = new StringWriter(50);
        while (true) {
            int c = 0;
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
                                markInfos.add(new MarkInfo(start, end));
                                writer.write(value);
                            } else {
                                writer.write("${");
                                //key not found
                                writer.write(key);
                            }

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

    private static final class MarkInfo {
        public int start;
        public int end;

        public MarkInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
