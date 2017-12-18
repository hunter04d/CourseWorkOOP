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

/**
 * Created by Den on 18.12.2017.
 */

public class WebViewFragment extends Fragment
{

    private FragmentWebViewBinding mBinding;
    private BuildSolutionTask mTask;

    public static WebViewFragment newInstance()
    {

        Bundle args = new Bundle();

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false);
        mBinding.executePendingBindings();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.detailed_solution);
        mBinding.solveWebView.getSettings().setJavaScriptEnabled(true);
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
            return null;
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
            super.onPostExecute(s);
        }
    }
}
