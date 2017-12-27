package com.hunter04d.android.booleanminimizer;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunter04d.android.booleanminimizer.database.Solution;
import com.hunter04d.android.booleanminimizer.database.SolutionsManager;
import com.hunter04d.android.booleanminimizer.databinding.FragmentWebViewBinding;

import j2html.TagCreator;

/**
 * Created by Den on 18.12.2017.
 */

public class WebViewFragment extends Fragment
{
    public static final String ARG_VECTOR = "vector";
    public static final String ARG_FUNCTION = "function";
    public static final String ARG_IS_EXPR_MODE = "expr_mode";

    private static final String mFileName = "detailedSolution.html";
    private FragmentWebViewBinding mBinding;
    private BuildSolutionTask mTask;
    private String mVector;
    private String mFunction;
    private boolean mIsExprMode;

    public static WebViewFragment newInstance(String vector, String function, boolean isExprMode)
    {

        Bundle args = new Bundle();
        args.putString(ARG_VECTOR, vector);
        args.putString(ARG_FUNCTION, function);
        args.putBoolean(ARG_IS_EXPR_MODE, isExprMode);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container,false);
        mBinding.executePendingBindings();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.detailed_solution);
        mBinding.solveWebView.getSettings().setJavaScriptEnabled(true);
        Bundle b = getArguments();
        mVector = b.getString(ARG_VECTOR);
        mFunction = b.getString(ARG_FUNCTION);
        mIsExprMode = b.getBoolean(ARG_IS_EXPR_MODE);
        mBinding.solveWebView.loadUrl("file:///android_asset/preloader_determinate.html");
        mTask = new BuildSolutionTask();
        mTask.execute();
        return mBinding.getRoot();
    }


    @Override
    public void onDestroyView()
    {
        if (mTask != null)
        {
            mTask.cancel(true);
        }
        super.onDestroyView();
    }

    private class BuildSolutionTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            OutputWriter outputWriter = new OutputWriter(getContext(), mFileName);
            if (mIsExprMode)
            {
                outputWriter.append(TagCreator.p(getActivity().getString(R.string.detailed_solution_0_1, mFunction)).render());
            }
            else
            {
                outputWriter.append(TagCreator.p(getActivity().getString(R.string.detailed_solution_0_2, mFunction)).render());
            }
            outputWriter.append(HtmlBuilder.truthTable(mVector));
            String varNamesString = SharedPreferenceManager.getVarNamesString(getContext());
            DetailedSolutionResult result = NativeLib.getDetailedResult(mVector, varNamesString);
            if (result.hasSucceeded())
            {
                String[] arr = result.getTables();
                outputWriter
                        .append(TagCreator.p(getActivity().getString(R.string.detailed_solution_1, (int) (Math.log(mVector.length()) / Math.log(2)))).render())
                        .append("<table class=\"centered bordered\">" + arr[0] + "</table><hr>")
                        .append(TagCreator.p(getActivity().getString(R.string.detailed_solution_2)).render())
                        .append("<table class=\"centered bordered\">" + arr[1] + "</table><hr>")
                        .append(TagCreator.p(getActivity().getString(R.string.detailed_solution_3)).render())
                        .append("<table class=\"centered bordered\">" + arr[2] + "</table><hr>")
                        .append(TagCreator.p(getActivity().getString(R.string.detailed_solution_4)).render())
                        .append("<table class=\"centered bordered\">" + arr[3] + "</table><hr>")
                        .append(TagCreator.p(getString(R.string.detailed_solution_5)).render())
                        .append("<p>" + TagCreator.b(result.getCore()) + "</p>");
                if (result.getRest().length != 0)
                {
                    if(!result.getRest()[0].equals(""))
                    {
                        outputWriter.append(TagCreator.p(getString(R.string.detailed_solution_6_1)).render());
                        for (String rest : result.getRest())
                        {
                            outputWriter.append(TagCreator.p(rest).render());
                        }
                    }
                }
                else
                {
                    outputWriter.append(TagCreator.p(getString(R.string.detailed_solution_6_2)).render());
                }
                if (result.getResults().length != 0)
                {
                    outputWriter.append(TagCreator.p(getString(R.string.detailed_solution_7)).render());
                    String[] varNames = SharedPreferenceManager.getVarNames(getContext());
                    for (String res : result.getResults())
                    {
                        outputWriter.append(HtmlBuilder.pRes(res, varNames) + "<hr>");
                    }
                }
                Solution s = new Solution(SolutionsManager.get(getContext()).getCount() + 1,
                        mFunction, HtmlBuilder.linearResult(result.getResults()[0], varNamesString.split(" ")), varNamesString);
                SolutionsManager.get(getContext()).insertSolution(s);
            }
            else
            {
                outputWriter.append(getString(R.string.detailed_solution_error));
            }
            return outputWriter.save();
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s)
        {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s)
        {
            mBinding.solveWebView.loadUrl(s);
        }
    }
}
