package com.hunter04d.android.booleanminimizer;


import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hunter04d.android.booleanminimizer.databinding.FragmentSettingsBinding;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingsFragment extends Fragment
{

    public static SettingsFragment newInstance()
    {
        SettingsFragment fragment = new SettingsFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    private String[] mVarNames;

    private FragmentSettingsBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        mBinding.executePendingBindings();
        mBinding.settingsX1Layout.clearFocus();
        InputFilter filter = (source, start, end, dest, dstart, dend) ->
        {
            if (source.toString().equals(""))
            {
                return null;
            }
            Pattern pattern = Pattern.compile("^[a-zA-z]\\d{0,2}$");
            Matcher matcher = pattern.matcher(dest.toString() + source.toString());
            if(!matcher.matches())
            {
                Toast.makeText(getContext(), "Variable must start with a letter and have 1 or 2 numbers after it", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        };
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.settingsToolbar);
        mBinding.settingsX1.setFilters(new InputFilter[]{filter});
        mBinding.settingsX2.setFilters(new InputFilter[]{filter});
        mBinding.settingsX3.setFilters(new InputFilter[]{filter});
        mBinding.settingsX4.setFilters(new InputFilter[]{filter});
        mBinding.settingsX5.setFilters(new InputFilter[]{filter});
        mBinding.settingsX6.setFilters(new InputFilter[]{filter});
        mBinding.settingsX7.setFilters(new InputFilter[]{filter});
        mBinding.settingsX8.setFilters(new InputFilter[]{filter});
        mVarNames = SharedPreferenceManager.getVarNames(getContext());
        //mVarNames = "X1 X2 X3 X4 X5 X6 X7 X8".split(" ");
        mBinding.settingsX1.setText(mVarNames[0]);
        mBinding.settingsX2.setText(mVarNames[1]);
        mBinding.settingsX3.setText(mVarNames[2]);
        mBinding.settingsX4.setText(mVarNames[3]);
        mBinding.settingsX5.setText(mVarNames[4]);
        mBinding.settingsX6.setText(mVarNames[5]);
        mBinding.settingsX7.setText(mVarNames[6]);
        mBinding.settingsX8.setText(mVarNames[7]);
        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView()
    {
        //TODO: return to the Main Fragment as String and aply changes there
        getActivity().setResult(Activity.RESULT_OK);
        mVarNames[0] = mBinding.settingsX1.getText().toString();
        mVarNames[1] = mBinding.settingsX2.getText().toString();
        mVarNames[2] = mBinding.settingsX3.getText().toString();
        mVarNames[3] = mBinding.settingsX4.getText().toString();
        mVarNames[4] = mBinding.settingsX5.getText().toString();
        mVarNames[5] = mBinding.settingsX6.getText().toString();
        mVarNames[6] = mBinding.settingsX7.getText().toString();
        mVarNames[7] = mBinding.settingsX8.getText().toString();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 8; ++i)
        {
            if (mVarNames[i].isEmpty())
            {
                s.append("X").append(i + 1).append(" ");
            }
            else
            {
                s.append(mVarNames[i].toUpperCase()).append(" ");
            }
        }
        super.onDestroyView();
        SharedPreferenceManager.setVarNames(getContext(), s.toString());

    }
}