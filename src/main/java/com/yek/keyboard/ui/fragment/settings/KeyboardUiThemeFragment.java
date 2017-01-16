package com.yek.keyboard.ui.fragment.settings;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anysoftkeyboard.theme.KeyboardTheme;
import com.anysoftkeyboard.theme.KeyboardThemeFactory;
import com.yek.keyboard.R;

import java.util.List;

/**
 * Created by banksy on 16.01.2017.
 */

public class KeyboardUiThemeFragment extends Fragment {

    public RecyclerView recylerview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recylerview = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recylerview.setHasFixedSize(true);
        recylerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recylerview.setAdapter(new ThemeAdapter(getAllAvailableAddOns(), getCurrentSelectedAddOn()));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.window_popup_corner_radius);
        recylerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }

    protected List<KeyboardTheme> getAllAvailableAddOns() {
        return KeyboardThemeFactory.getAllAvailableThemes(getActivity().getApplicationContext());
    }


    protected KeyboardTheme getCurrentSelectedAddOn() {
        return KeyboardThemeFactory.getCurrentKeyboardTheme(getActivity().getApplicationContext());
    }


    private class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<KeyboardTheme> list;
        private KeyboardTheme selectTheme;

        public ThemeAdapter(List<KeyboardTheme> list, KeyboardTheme selectTheme) {
            this.list = list;
            this.selectTheme = selectTheme;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ThemeHolder(getActivity().getLayoutInflater().inflate(R.layout.list_item_keyboard_theme, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ThemeHolder) holder).setData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ThemeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView cover;
            AppCompatRadioButton txtTitle;

            private KeyboardTheme theme;

            ThemeHolder(View itemView) {
                super(itemView);
                cover = (ImageView) itemView.findViewById(R.id.imgCover);
                txtTitle = (AppCompatRadioButton) itemView.findViewById(R.id.txtTitle);

                itemView.setOnClickListener(this);
            }

            public void setData(KeyboardTheme theme) {
                this.theme = theme;
                cover.setImageDrawable(theme.getScreenshot());
                txtTitle.setText(theme.getName());
                if (theme.getId().contains(selectTheme.getId()))
                    txtTitle.setChecked(true);
                else
                    txtTitle.setChecked(false);
                txtTitle.setTextIsSelectable(false);
                txtTitle.setClickable(false);
            }

            @Override
            public void onClick(View v) {
                selectTheme = theme;
                KeyboardThemeFactory.setCurrentKeyboardTheme(v.getContext().getApplicationContext(), theme.getId());
                notifyDataSetChanged();
            }
        }

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildLayoutPosition(view) % 2 == 1) {
                outRect.right = space;
                outRect.left = space / 2;
            } else {
                outRect.right = space / 2;
                outRect.left = space;
            }
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

}
