package com.hunter04d.android.booleanminimizer;

import android.content.Context;
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

import j2html.TagCreator;


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
        SharedPreferenceManager.getVarNames(getContext());
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
        mBinding.buttonX8.setOnClickListener((view) -> addString("X8"));
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
                        expr  = expr
                                .replace("X1", "x1")
                                .replace("X2", "x2")
                                .replace("X3","x3")
                                .replace("X4", "x4")
                                .replace("X5", "x5")
                                .replace("X6", "x6")
                                .replace("X7", "x7")
                                .replace("X8","x8")
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
                            mBinding.inputLayoutFormula.setError(mVector);
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
                //TODO:
                return true;
            case R.id.action_history:
                //TODO:
                return true;
            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
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
                return "error";
            }
            else return HtmlBuilder.result(nativeOut);
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s)
        {
            InputStream open = null;
            try
            {
                open =  getActivity().getAssets().open("base.html");
                File file = new File(getActivity().getFilesDir(), "res.html");
                if (file.exists())
                {
                    if (file.delete() == false)
                    {
                        throw new IOException();
                    }
                }
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while((read = open.read(buffer)) != -1)
                {
                    out.write(buffer, 0, read);
                }
                byte[] write = (s +"</body>" + "</html>").getBytes();
                out.write(write, 0, write.length);
                open.close();
                out.flush();
                out.close();
                mBinding.webView.loadUrl("file:///" + file.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
