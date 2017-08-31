package com.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by CJJ on 2017/8/31.
 *
 * @author CJJ
 */

public class ColorPickerDialog extends DialogFragment {

    private OnColorSelectListener listener;

    public static ColorPickerDialog newInstance() {

        Bundle args = new Bundle();

        ColorPickerDialog fragment = new ColorPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_dialog, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    int[] colors = {
            R.color.blue,
            R.color.gray,
            R.color.navy,
            R.color.orange,
            R.color.purple,
            R.color.green};

    @OnClick({R.id.blue, R.id.gray, R.id.navy, R.id.orange, R.id.purple, R.id.green})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.blue:
                listener.onColorSelect(colors[0],0);
                break;
            case R.id.gray:
                listener.onColorSelect(colors[1],1);
                break;
            case R.id.navy:
                listener.onColorSelect(colors[2],2);
                break;
            case R.id.orange:
                listener.onColorSelect(colors[3],3);
                break;
            case R.id.purple:
                listener.onColorSelect(colors[4],4);
                break;
            case R.id.green:
                listener.onColorSelect(colors[5],5);
                break;


        }
        dismiss();
    }

    public interface OnColorSelectListener {
        void onColorSelect(int color,int index);
    }


    public void show(FragmentManager manager, OnColorSelectListener listener) {
        this.listener = listener;
        show(manager, "");
    }
}
