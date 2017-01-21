package com.yek.keyboard.ime;

import com.yek.keyboard.keyboards.views.OnKeyboardActionListener;

public interface InputViewActionsProvider {

    /**
     * Sets the listener of actions taken on this {@link InputViewActionsProvider}.
     */
    void setOnKeyboardActionListener(OnKeyboardActionListener keyboardActionListener);

}
