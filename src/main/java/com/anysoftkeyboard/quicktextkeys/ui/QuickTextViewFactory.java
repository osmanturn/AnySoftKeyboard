package com.anysoftkeyboard.quicktextkeys.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.anysoftkeyboard.keyboards.views.MiniKeyboardActionListener;
import com.anysoftkeyboard.quicktextkeys.QuickTextKey;
import com.anysoftkeyboard.quicktextkeys.QuickTextKeyFactory;
import com.anysoftkeyboard.quicktextkeys.HistoryQuickTextKey;
import com.yek.keyboard.R;

import java.util.ArrayList;
import java.util.List;

public class QuickTextViewFactory {

    public static View createQuickTextView(final Context context, final MiniKeyboardActionListener keyboardActionListener, float tabTitleTextSize, ColorStateList tabTitleTextColor) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.quick_text_popup_root_view, null, false);
        FrameKeyboardViewClickListener frameKeyboardViewClickListener = new FrameKeyboardViewClickListener(keyboardActionListener);
        frameKeyboardViewClickListener.registerOnViews(rootView);
        final List<QuickTextKey> list = new ArrayList<>();
        //always starting with Recent
        final HistoryQuickTextKey historyQuickTextKey = new HistoryQuickTextKey(context);
        list.add(historyQuickTextKey);
        //then all the rest
        list.addAll(QuickTextKeyFactory.getOrderedEnabledQuickKeys(context));

        final QuickTextUserPrefs quickTextUserPrefs = new QuickTextUserPrefs(context);

        keyboardActionListener.setInOneShot(quickTextUserPrefs.isOneShotQuickTextPopup());

        ViewPager pager = (ViewPager) rootView.findViewById(R.id.quick_text_keyboards_pager);
        PagerTabStrip pagerTabStrip = (PagerTabStrip) pager.findViewById(R.id.pager_tabs);
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTitleTextSize);
        pagerTabStrip.setTextColor(tabTitleTextColor.getDefaultColor());
        pagerTabStrip.setTabIndicatorColor(tabTitleTextColor.getDefaultColor());
        final int decorationWidthSize = context.getResources().getDimensionPixelSize(R.dimen.quick_key_size);
        PagerAdapter adapter = new QuickKeysKeyboardPagerAdapter(
                context, list, new RecordHistoryKeyboardActionListener(historyQuickTextKey, keyboardActionListener), decorationWidthSize);
        pager.setAdapter(adapter);
        pager.setCurrentItem(quickTextUserPrefs.getStartPageIndex(list));
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                QuickTextKey selectedKey = list.get(position);
                quickTextUserPrefs.setLastSelectedAddOnId(selectedKey.getId());
            }
        });
        return rootView;
    }
}
