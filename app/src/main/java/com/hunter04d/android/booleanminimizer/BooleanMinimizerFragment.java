package com.hunter04d.android.booleanminimizer;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hunter04d.android.booleanminimizer.database.Solution;
import com.hunter04d.android.booleanminimizer.database.SolutionsManager;
import com.hunter04d.android.booleanminimizer.databinding.FragmentBooleanMinimizerBinding;

import java.util.Arrays;
import java.util.HashMap;

import j2html.TagCreator;

import static android.app.Activity.RESULT_OK;


public class BooleanMinimizerFragment extends Fragment
{
    private static final int REQUEST_SOLUTION = 2;
    private static final int REQUEST_OPTIONS = 1;
    private static final int REQUEST_SOVLE = 3;
    public static final String DIALOG_SOVLE = "DialogSolve";
    private FragmentBooleanMinimizerBinding mBinding;
    private boolean mIsExprMode = true;
    private String mVector;
    private boolean mIsAllCases = false;
    private CalculateTask mCalculateTask = null;
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
        mVarNames = SharedPreferenceManager.getVarNames(getContext());
        setVarNames();
        mBinding.button0.setOnClickListener(v -> addString("0",v));
        mBinding.button1.setOnClickListener(v -> addString("1",v));
        mBinding.buttonDash.setOnClickListener((view) -> addString("‒",view));
        mBinding.buttonAnd.setOnClickListener((view) -> addString("∧",view));
        mBinding.buttonOr.setOnClickListener((view) -> addString("∨",view));
        mBinding.buttonXor.setOnClickListener((view) -> addString("⊕",view));
        mBinding.buttonNot.setOnClickListener((view) -> addString("¬",view));
        mBinding.buttonBraceLeft.setOnClickListener((view) -> addString("(",view));
        mBinding.buttonBraceRight.setOnClickListener((view) -> addString(")",view));

        mBinding.imageButton.setOnClickListener((view) -> {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
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
        mBinding.imageButton2.setOnClickListener((view) ->
        {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            mBinding.inputFormula.setText("");
            mBinding.inputFormula.clearFocus();
            mBinding.webView.loadData("<html><body></body></html>", "text/html", "utf-8");
            if (mCalculateTask != null)
            {
                mCalculateTask.cancel(true);
            }
        });
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
                        ParserResult result = NativeLib.parseExpression(expr, varCount);
                        if (result.hasSucceeded())
                        {
                            mVector = result.getResult();
                            //mBinding.inputLayoutFormula.setError(mVector);
                            mBinding.webView.loadUrl(OutputWriter.writeToBaseHTML(HtmlBuilder.vector(mVector),getActivity()));
                            mBinding.buttonCalc.setEnabled(true);
                        }
                        else // TODO: Output error here
                        {
                            mVector = result.getResult();
                            mBinding.inputLayoutFormula.setError("Error: expression invalid");
                            mBinding.webView.loadData("<html><body></body></html>", "text/html", "utf-8");
                            mBinding.buttonCalc.setEnabled(false);
                        }
                        return;
                    }
                }
                mBinding.inputLayoutFormula.setHint("position: " + (mBinding.inputFormula.length() -1));
                mBinding.webView.loadData("<html><body></body></html>", "text/html", "utf-8");
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
            if (mVector != null)
            {
                mBinding.webView.loadUrl("file:///android_asset/preloader.html");
                mCalculateTask = new CalculateTask();
                mCalculateTask.execute(mVector);
            }
        });
        mBinding.buttonCalc.setOnLongClickListener(v ->
        {
            SolveOptionDialogFragment solveOptionDialogFragment = SolveOptionDialogFragment.newInstance();
            solveOptionDialogFragment.setTargetFragment(BooleanMinimizerFragment.this, REQUEST_SOVLE);
            solveOptionDialogFragment.show(getFragmentManager(), DIALOG_SOVLE);
            return true;
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

        mBinding.buttonX1.setOnClickListener((view) -> addString(mVarNames[0],view));
        mBinding.buttonX2.setOnClickListener((view) -> addString(mVarNames[1],view));
        mBinding.buttonX3.setOnClickListener((view) -> addString(mVarNames[2],view));
        mBinding.buttonX4.setOnClickListener((view) -> addString(mVarNames[3],view));
        mBinding.buttonX5.setOnClickListener((view) -> addString(mVarNames[4],view));
        mBinding.buttonX6.setOnClickListener((view) -> addString(mVarNames[5],view));
        mBinding.buttonX7.setOnClickListener((view) -> addString(mVarNames[6],view));
        mBinding.buttonX8.setOnClickListener((view) -> addString(mVarNames[7],view));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_boolean_minimizer, menu);
    }

    private void addString(String string, View v)
    {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
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
            case R.id.action_all_cases:
                item.setChecked(!item.isChecked());
                mIsAllCases = item.isChecked();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(settingsIntent, REQUEST_OPTIONS);
                return true;
            case R.id.action_history:
                Intent solutionIntent = new Intent(getActivity(), SolutionListActivity.class);
                startActivityForResult(solutionIntent, REQUEST_SOLUTION);
                return true;
            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_OPTIONS:
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        mVarNames = data.getStringExtra(SettingsFragment.RESULT_VAR_NAMES).split(" ");
                        setVarNames();
                        mBinding.inputFormula.clearFocus();
                    }
                }
                break;
            case REQUEST_SOLUTION:
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        mVarNames = data.getStringExtra(SolutionListFragment.RESULT_VAR_NAMES).split(" ");
                        setVarNames();
                        mBinding.inputFormula.setText(data.getStringExtra(SolutionListFragment.RESULT_EXPRESSION));
                    }
                }
                break;
            case REQUEST_SOVLE:
                if (resultCode == RESULT_OK)
                {
                    if (data != null)
                    {
                        int which = data.getIntExtra(SolveOptionDialogFragment.RESULT_WHICH, 0);
                        if (which == 0)
                        {
                            mBinding.buttonCalc.callOnClick();
                        }
                        else
                        {
                            if (mVector != null)
                            {
                                Intent i = new Intent(getActivity(), WebViewActivity.class);
                                i.putExtra(WebViewActivity.EXTRA_FUNCTION, mBinding.inputFormula.getText().toString());
                                i.putExtra(WebViewActivity.EXTRA_VECTOR, mVector);
                                i.putExtra(WebViewActivity.EXTRA_IS_EXPR_MODE, mIsExprMode);
                                startActivity(i);
                                //TODO:
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private class CalculateTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            if (strings[0].matches("^1+$"))
            {
                return OutputWriter.writeToBaseHTML(TagCreator.p("1").render(), getActivity());
            }
            if (strings[0].matches("^0+$"))
            {
                return OutputWriter.writeToBaseHTML(TagCreator.p("0").render(), getActivity());
            }
            String[] nativeOut = NativeLib.calculateMinimisation(strings[0], mIsAllCases);
            if (isCancelled())
            {
                return "";
            }
            if (nativeOut[0].equals("error"))
            {
                return OutputWriter.writeToBaseHTML("error", getActivity());
            }
            else
            {
                StringBuilder builder = new StringBuilder();
                for(String varName : mVarNames)
                {
                    builder.append(varName).append(' ');
                }
                builder.deleteCharAt(builder.length()-1);
                Solution s = new Solution(SolutionsManager.get(getContext()).getCount() + 1, mBinding.inputFormula.getText().toString(),HtmlBuilder.linearResult(nativeOut[0], mVarNames), builder.toString());
                SolutionsManager.get(getContext()).insertSolution(s);
                return OutputWriter.writeToBaseHTML(HtmlBuilder.result(nativeOut, mVarNames), getActivity());
            }
        }

        @Override
        protected void onCancelled(String s)
        {
            super.onCancelled(s);
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
