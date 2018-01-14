package com.github2136.encrypt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by yb on 2018/1/14.
 */

public class ClipboardUtil {
    private ClipboardManager cm;

    public ClipboardUtil(Context context) {
        cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void copy(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        cm.setPrimaryClip(clip);
    }
}
