package com.yek.keyboard.keyboards.views.preview;

import android.graphics.Point;
import android.view.View;

import com.yek.keyboard.keyboards.Keyboard;

public interface PositionCalculator {
	Point calculatePositionForPreview(Keyboard.Key key, View keyboardView, PreviewPopupTheme theme, int[] windowOffset);
}
