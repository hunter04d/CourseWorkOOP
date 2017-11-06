package com.hunter04d.android.booleanminimizer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hunter04d.android.booleanminimizer.databinding.FragmentBooleanMinimizerBinding;

public class BooleanMinimizerFragment extends Fragment
{
    private FragmentBooleanMinimizerBinding mBinding;
    private boolean mIsExprMode = true;
    private String mVector;
    public static BooleanMinimizerFragment newInstance()
    {
        BooleanMinimizerFragment fragment = new BooleanMinimizerFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_boolean_minimizer, container, false);
        mBinding.executePendingBindings();
        mBinding.inputFormula.setShowSoftInputOnFocus(false);
        mBinding.inputFormula.clearFocus();
        mBinding.button0.setOnClickListener(v -> addString("0"));
        mBinding.button1.setOnClickListener(v -> addString("1"));
        mBinding.buttonDash.setOnClickListener((view) -> addString("‒"));
        mBinding.buttonAnd.setOnClickListener((view) -> addString("∧"));
        mBinding.buttonOr.setOnClickListener((view) -> addString("∨"));
        mBinding.buttonXor.setOnClickListener((view) -> addString("⊕"));
        mBinding.buttonNot.setOnClickListener((view) -> addString("¬"));
        mBinding.buttonBraceLeft.setOnClickListener((view) -> addString("("));
        mBinding.buttonBraceRight.setOnClickListener((view) -> addString(")"));
        mBinding.buttonX1.setOnClickListener((view) -> addString("X1"));
        mBinding.buttonX2.setOnClickListener((view) -> addString("X2"));
        mBinding.buttonX3.setOnClickListener((view) -> addString("X3"));
        mBinding.buttonX4.setOnClickListener((view) -> addString("X4"));
        mBinding.buttonX5.setOnClickListener((view) -> addString("X5"));
        mBinding.buttonX6.setOnClickListener((view) -> addString("X6"));
        mBinding.buttonX7.setOnClickListener((view) -> addString("X7"));
        mBinding.imageButton.setOnClickListener((view) -> {
            int start = mBinding.inputFormula.getSelectionStart();
            int end = mBinding.inputFormula.getSelectionEnd();

            if (start == end)
            {
                if(start == 0) return;
                String str1 = mBinding.inputFormula.getText().toString().substring(0, start-1);
                String str2 = mBinding.inputFormula.getText().toString().substring(end);
                mBinding.inputFormula.setText(str1 + str2);
                mBinding.inputFormula.setSelection(str1.length());
            }
            else
            {
                String str1 = mBinding.inputFormula.getText().toString().substring(0, start);
                String str2 = mBinding.inputFormula.getText().toString().substring(end);
                mBinding.inputFormula.setText(str1 + str2);
                mBinding.inputFormula.setSelection(str1.length());
            }
        });
        mBinding.imageButton2.setOnClickListener((view) -> {mBinding.inputFormula.setText("");  mBinding.inputFormula.clearFocus();});
        mBinding.inputLayoutFormula.setErrorEnabled(true);
        mBinding.inputFormula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 32)
                {
                    mBinding.inputFormula.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
                else if (s.length() > 40)
                {
                    mBinding.inputFormula.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                }
                else
                {
                    mBinding.inputFormula.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                }
                if (s.length() == 0)
                {
                    mBinding.inputLayoutFormula.setHint(getString(R.string.enter_expression));
                    mBinding.inputLayoutFormula.setError(null);
                    return;
                }
                for(char x: s.toString().toCharArray())
                {
                    if (x == '0' || x == '1' || x == '‒')
                    {
                        continue;
                    }
                    else // TODO: Call native parser from here
                    {
                        mIsExprMode = true;
                        mBinding.inputLayoutFormula.setError(null);
                        mBinding.inputLayoutFormula.setHint(getString(R.string.enter_expression));
                        return;
                    }
                }
                mBinding.inputLayoutFormula.setHint("position: " + (mBinding.inputFormula.length() -1));
                mIsExprMode = false;
                if (s.length() != Math.pow(2, Math.ceil((double)(Math.log(s.length())/ Math.log(2)))))
                {
                    mBinding.inputLayoutFormula.setError("Boolean vector has to have power of 2 points");
                }
                else
                {
                    mBinding.inputLayoutFormula.setError(null);
                    //mVector = s.toString();

                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        return mBinding.getRoot();
    }

    private void addString(String string)
    {
        int start = mBinding.inputFormula.getSelectionStart();
        int end = mBinding.inputFormula.getSelectionEnd();
        String str1 = mBinding.inputFormula.getText().toString().substring(0, start);
        String str2 = mBinding.inputFormula.getText().toString().substring(end);
        mBinding.inputFormula.setText(str1 + string + str2);
        mBinding.inputFormula.setSelection(str1.length() + string.length());
    }
}
