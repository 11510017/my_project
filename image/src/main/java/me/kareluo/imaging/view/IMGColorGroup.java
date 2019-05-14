package me.kareluo.imaging.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by felix on 2017/12/1 下午3:07.
 */

public class IMGColorGroup extends RadioGroup {

    /**
     * Instantiates a new Img color group.
     *
     * @param context the context
     */
    public IMGColorGroup(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Img color group.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public IMGColorGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Gets check color.
     *
     * @return the check color
     */
    public int getCheckColor() {
        int checkedId = getCheckedRadioButtonId();
        IMGColorRadio radio = findViewById(checkedId);
        if (radio != null) {
            return radio.getColor();
        }
        return Color.WHITE;
    }

    /**
     * Sets check color.
     *
     * @param color the color
     */
    public void setCheckColor(int color) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            IMGColorRadio radio = (IMGColorRadio) getChildAt(i);
            if (radio.getColor() == color) {
                radio.setChecked(true);
                break;
            }
        }
    }
}
