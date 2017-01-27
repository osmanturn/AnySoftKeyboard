package com.yek.keyboard.quicktextkeys.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.yek.keyboard.R;
import com.yek.keyboard.ime.InputViewActionsProvider;
import com.yek.keyboard.keyboards.views.OnKeyboardActionListener;
import com.yek.keyboard.quicktextkeys.HistoryQuickTextKey;
import com.yek.keyboard.quicktextkeys.QuickTextKey;
import com.yek.keyboard.quicktextkeys.QuickTextKeyFactory;
import com.yek.keyboard.ui.ViewPagerWithDisable;

import java.util.ArrayList;
import java.util.List;

public class QuickTextPagerView extends LinearLayout implements InputViewActionsProvider {

    private float mTabTitleTextSize;
    private ColorStateList mTabTitleTextColor;
    private Drawable mCloseKeyboardIcon;
    private Drawable mBackspaceIcon;
    private Drawable mSettingsIcon;

    public QuickTextPagerView(Context context) {
        super(context);
    }

    public QuickTextPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setThemeValues(float tabTextSize, ColorStateList tabTextColor, Drawable closeKeyboardIcon, Drawable backspaceIcon, Drawable settingsIcon) {
        mTabTitleTextSize = tabTextSize;
        mTabTitleTextColor = tabTextColor;
        mCloseKeyboardIcon = closeKeyboardIcon;
        mBackspaceIcon = backspaceIcon;
        mSettingsIcon = settingsIcon;
    }

    @Override
    public void setOnKeyboardActionListener(OnKeyboardActionListener keyboardActionListener) {
        FrameKeyboardViewClickListener frameKeyboardViewClickListener = new FrameKeyboardViewClickListener(keyboardActionListener);
        frameKeyboardViewClickListener.registerOnViews(this);

        final Context context = getContext();
        final List<QuickTextKey> list = new ArrayList<>();
        //always starting with Recent
        final HistoryQuickTextKey historyQuickTextKey = new HistoryQuickTextKey(context);
        list.add(historyQuickTextKey);
        //then all the rest
        list.addAll(QuickTextKeyFactory.getOrderedEnabledQuickKeys(context));

        final QuickTextUserPrefs quickTextUserPrefs = new QuickTextUserPrefs(context);

        ViewPagerWithDisable pager = (ViewPagerWithDisable) findViewById(R.id.quick_text_keyboards_pager);
        PagerAdapter adapter = new QuickKeysKeyboardPagerAdapter(context, pager, list, new RecordHistoryKeyboardActionListener(historyQuickTextKey, keyboardActionListener));

        ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                QuickTextKey selectedKey = list.get(position);
                quickTextUserPrefs.setLastSelectedAddOnId(selectedKey.getId());
            }
        };
        int startPageIndex = quickTextUserPrefs.getStartPageIndex(list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            setupSlidingTab(this, mTabTitleTextSize, mTabTitleTextColor, pager, adapter, onPageChangeListener, startPageIndex);
        } else {
            setupSupportTab(mTabTitleTextSize, mTabTitleTextColor, pager, adapter, onPageChangeListener, startPageIndex);
        }

        //setting up icons from theme
        ((ImageView)findViewById(R.id.quick_keys_popup_close)).setImageDrawable(mCloseKeyboardIcon);
        ((ImageView)findViewById(R.id.quick_keys_popup_backspace)).setImageDrawable(mBackspaceIcon);
        ((ImageView)findViewById(R.id.quick_keys_popup_quick_keys_settings)).setImageDrawable(mSettingsIcon);
    }

    private static void setupSupportTab(float tabTitleTextSize, ColorStateList tabTitleTextColor, ViewPager pager, PagerAdapter adapter, ViewPager.OnPageChangeListener onPageChangeListener, int startIndex) {
        PagerTabStrip pagerTabStrip = (PagerTabStrip) pager.findViewById(R.id.pager_tabs);
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTitleTextSize);
        pagerTabStrip.setTextColor(tabTitleTextColor.getDefaultColor());
        pagerTabStrip.setTabIndicatorColor(tabTitleTextColor.getDefaultColor());
        pager.setAdapter(adapter);
        pager.setCurrentItem(startIndex);
        pager.setOnPageChangeListener(onPageChangeListener);
    }

    private static void setupSlidingTab(View rootView, float tabTitleTextSize, ColorStateList tabTitleTextColor, ViewPager pager, PagerAdapter adapter, ViewPager.OnPageChangeListener onPageChangeListener, int startIndex) {
        PagerSlidingTabStrip pagerTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.pager_tabs);
        pagerTabStrip.setTextSize((int)tabTitleTextSize);
        pagerTabStrip.setTextColor(tabTitleTextColor.getDefaultColor());
        pagerTabStrip.setIndicatorColor(tabTitleTextColor.getDefaultColor());
        pager.setAdapter(adapter);
        pager.setCurrentItem(startIndex);
        pagerTabStrip.setViewPager(pager);
        pagerTabStrip.setOnPageChangeListener(onPageChangeListener);
    }
}
