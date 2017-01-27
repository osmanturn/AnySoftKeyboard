package com.yek.keyboard.keyboards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yek.keyboard.ime.InputViewActionsProvider;
import com.yek.keyboard.ime.InputViewBinder;

public class KeyboardViewContainerView extends FrameLayout {
    private InputViewBinder mStandardKeyboardView;
    private OnKeyboardActionListener mKeyboardActionListener;

    public KeyboardViewContainerView(Context context) {
        super(context);
    }

    public KeyboardViewContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardViewContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (mKeyboardActionListener != null) {
            ((InputViewActionsProvider) child).setOnKeyboardActionListener(mKeyboardActionListener);
        }
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener keyboardActionListener) {
        mKeyboardActionListener = keyboardActionListener;
        for (int childIndex=0; childIndex<getChildCount(); childIndex++)
            ((InputViewActionsProvider)getChildAt(childIndex)).setOnKeyboardActionListener(keyboardActionListener);
    }

    public InputViewBinder getStandardKeyboardView() {
        if (mStandardKeyboardView == null) {
            mStandardKeyboardView = (InputViewBinder) getChildAt(0);
        }
        return mStandardKeyboardView;
    }
}
