package com.hunter04d.android.booleanminimizer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hunter04d.android.booleanminimizer.databinding.FragmentBooleanMinimizerBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

import j2html.TagCreator;

import static android.app.Activity.RESULT_OK;


public class BooleanMinimizerFragment extends Fragment
{
    private static int REQUEST_OPTIONS = 1;
    private FragmentBooleanMinimizerBinding mBinding;
    private boolean mIsExprMode = true;
    private String mVector;
    private String[] mVarNames;
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
        //setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mBinding.inputFormula.clearFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_boolean_minimizer, container, false);
        mBinding.executePendingBindings();
        mBinding.inputFormula.clearFocus();
        mBinding.inputFormula.setShowSoftInputOnFocus(false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        setHasOptionsMenu(true);
        //mBinding.inputFormula.clearFocus();
        mVarNames = SharedPreferenceManager.getVarNames(getContext());
        setVarNames();
        mBinding.button0.setOnClickListener(v -> addString("0"));
        mBinding.button1.setOnClickListener(v -> addString("1"));
        mBinding.buttonDash.setOnClickListener((view) -> addString("‒"));
        mBinding.buttonAnd.setOnClickListener((view) -> addString("∧"));
        mBinding.buttonOr.setOnClickListener((view) -> addString("∨"));
        mBinding.buttonXor.setOnClickListener((view) -> addString("⊕"));
        mBinding.buttonNot.setOnClickListener((view) -> addString("¬"));
        mBinding.buttonBraceLeft.setOnClickListener((view) -> addString("("));
        mBinding.buttonBraceRight.setOnClickListener((view) -> addString(")"));
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
                    if (x != '0' && x != '1' && x != '‒')
                    {
                        mIsExprMode = true;
                        mBinding.inputLayoutFormula.setError(null);
                        mBinding.inputLayoutFormula.setHint(getString(R.string.enter_expression));
                        String expr = s.toString();
                        String[] arr = Arrays.copyOf(mVarNames,8);
                        Arrays.sort(arr, (s1, s2) -> Integer.compare(s2.length(), s1.length()));
                        HashMap<String, String> namesMap = new HashMap<>(8);
                        for (int i = 0; i < 8; ++i)
                        {
                            if (!namesMap.containsKey(mVarNames[i]))
                            {
                                namesMap.put(mVarNames[i], "x" + (i + 1));
                            }
                        }
                        for (int i = 0; i < 8; ++i)
                        {
                            expr = expr.replace(arr[i], namesMap.get(arr[i]));
                        }
                        expr  = expr
                                .replace("¬", "-")
                                .replace("∧","*")
                                .replace("∨", "+")
                                .replace("⊕", "^");
                        int varCount = 0;
                        for (int i = 1; i <= 8;i++ )
                        {
                            if (expr.contains("x" + Integer.toString(i)))
                            {
                                varCount = i;
                            }
                        }
                        ParserResult result = NativeLib.parseExpresion(expr, varCount);
                        if (result.HasSucceded())
                        {
                            mVector = result.getResult();
                            //mBinding.inputLayoutFormula.setError(mVector);
                            mBinding.webView.loadUrl(OutputWriter.writeToBaseHTML(HtmlBuilder.vector(mVector),getActivity()));
                            mBinding.buttonCalc.setEnabled(true);
                        }
                        else // TODO: Output error here
                        {
                            mVector = result.getResult();
                            mBinding.inputLayoutFormula.setError(mVector);
                            mBinding.buttonCalc.setEnabled(false);
                        }
                        return;
                    }
                }
                mBinding.inputLayoutFormula.setHint("position: " + (mBinding.inputFormula.length() -1));
                mIsExprMode = false;
                if (s.length() == Math.pow(2, Math.ceil(Math.log(s.length())/ Math.log(2))))
                {
                    mBinding.inputLayoutFormula.setError(null);
                    mVector = s.toString().replace('‒', '-');
                    mBinding.buttonCalc.setEnabled(true);

                }
                else
                {
                    mBinding.inputLayoutFormula.setError("Boolean vector has to have power of 2 points");
                    mBinding.buttonCalc.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        mBinding.buttonCalc.setOnClickListener((View v) ->
        {
            mBinding.webView.loadUrl("file:///android_asset/preloader.html");
            new CalculateTask().execute(mVector);
        });
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        return mBinding.getRoot();
    }

    private void setVarNames()
    {
        mBinding.buttonX1.setText(mVarNames[0]);
        mBinding.buttonX2.setText(mVarNames[1]);
        mBinding.buttonX3.setText(mVarNames[2]);
        mBinding.buttonX4.setText(mVarNames[3]);
        mBinding.buttonX5.setText(mVarNames[4]);
        mBinding.buttonX6.setText(mVarNames[5]);
        mBinding.buttonX7.setText(mVarNames[6]);
        mBinding.buttonX8.setText(mVarNames[7]);

        mBinding.buttonX1.setOnClickListener((view) -> addString(mVarNames[0]));
        mBinding.buttonX2.setOnClickListener((view) -> addString(mVarNames[1]));
        mBinding.buttonX3.setOnClickListener((view) -> addString(mVarNames[2]));
        mBinding.buttonX4.setOnClickListener((view) -> addString(mVarNames[3]));
        mBinding.buttonX5.setOnClickListener((view) -> addString(mVarNames[4]));
        mBinding.buttonX6.setOnClickListener((view) -> addString(mVarNames[5]));
        mBinding.buttonX7.setOnClickListener((view) -> addString(mVarNames[6]));
        mBinding.buttonX8.setOnClickListener((view) -> addString(mVarNames[7]));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_boolean_minimizer, menu);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(i, REQUEST_OPTIONS);
                return true;
            case R.id.action_history:
                //TODO:
                return true;
            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_OPTIONS)
        {
            if (resultCode == RESULT_OK)
            {
                if (data != null)
                {
                    mVarNames = data.getStringExtra(SettingsFragment.RESULT_VAR_NAMES).split(" ");
                    setVarNames();
                }
            }

        }
    }

    private class CalculateTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            String nativeOut = NativeLib.stringFromJNI(strings[0]);
            if (nativeOut.equals("error"))
            {
                return OutputWriter.writeToBaseHTML("error", getActivity());
            }
            else return OutputWriter.writeToBaseHTML(HtmlBuilder.result(nativeOut, mVarNames), getActivity());
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s)
        {
            mBinding.webView.loadUrl(s);
        }
    }
}
