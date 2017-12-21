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
            outputWriter.append(HtmlBuilder.truthTable(mVector));

            String[] arr = NativeLib.getTablesHtml(mVector, SharedPreferenceManager.getVarNamesString(getContext()));
            outputWriter.append(TagCreator.p(getActivity().getString(R.string.delailed_solution_1, (int)(Math.log(mVector.length()) / Math.log(2)))).render());
            outputWriter.append("<table class=\"centered bordered\">" +  arr[0] + "</table>");
            outputWriter.append(TagCreator.p(getActivity().getString(R.string.delailed_solution_2)).render());
            outputWriter.append ("<table class=\"centered bordered\">" +  arr[1] + "</table>");
            outputWriter.append(TagCreator.p(getActivity().getString(R.string.delailed_solution_3)).render());
            outputWriter.append("<table class=\"centered bordered\">" +  arr[2] + "</table>");
            outputWriter.append(TagCreator.p(getActivity().getString(R.string.delailed_solution_4)).render());
            outputWriter.append("<table class=\"centered bordered\">" +  arr[3] + "</table>");
            outputWriter.append(TagCreator.p(getActivity().getString(R.string.delailed_solution_5)).render());
            outputWriter.append("<br>" + TagCreator.p(TagCreator.b(arr[4])).render());
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
