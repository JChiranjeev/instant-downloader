package com.jainchiranjeev.instantdownloader;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Jain Chiranjeev on 22-May-18.
 * Visit http://jainchiranjeev.com
 */

public class CustomCalculations {
    private Context context;
    public CustomCalculations(Context context) {
        this.context = context;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
