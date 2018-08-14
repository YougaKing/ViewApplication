package youga.lrcview;

import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/12/1 0001.
 */

public class TextMeasure {

    public static RectF measureText(@NonNull Paint paint, @NonNull String text, @IntRange(from = 0) int maxWidth) {
        int textWidth;
        if (measureTextWidth(paint, text) < maxWidth) {
            textWidth = (int) measureTextWidth(paint, text);
        } else {
            textWidth = maxWidth;
        }

        int textHeight = 0;
        if (text.contains("\n")) {
            int rows = textRows(text);
            String[] splits = text.split("\n");
            for (String split : splits) {
                textHeight += measureTextHeight(paint, split, textWidth);
            }
            textHeight += (int) ((rows - (splits.length - 1)) * measureRowHeight(paint));
        } else {
            textHeight = (int) measureTextHeight(paint, text, textWidth);
        }

        return new RectF(0, 0, textWidth, textHeight);
    }


    public static float measureTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public static int textRows(String text) {
        int count = 0;
        Pattern p = Pattern.compile("\n");
        Matcher m = p.matcher(text);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public static float measureTextHeight(Paint paint, String text, int textWidth) {
        if (measureTextWidth(paint, text) < textWidth) {
            return measureRowHeight(paint);
        } else if (measureTextWidth(paint, text) % textWidth == 0) {
            return (int) (measureTextWidth(paint, text) / textWidth) * measureRowHeight(paint);
        } else {
            return ((int) (measureTextWidth(paint, text) / textWidth) + 1) * measureRowHeight(paint);
        }
    }

    public static float measureRowHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }
}
