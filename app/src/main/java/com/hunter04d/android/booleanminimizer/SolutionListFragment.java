package com.hunter04d.android.booleanminimizer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunter04d.android.booleanminimizer.database.Solution;
import com.hunter04d.android.booleanminimizer.database.SolutionsManager;
import com.hunter04d.android.booleanminimizer.databinding.FragmentSolutionListBinding;

import java.util.List;

/**
 * Created by hunter04d on 13.12.2017.
 */

public class SolutionListFragment extends Fragment
{

    private FragmentSolutionListBinding mBinding;
    public static SolutionListFragment newInstance()
    {
        //Bundle args = new Bundle();
        
        SolutionListFragment fragment = new SolutionListFragment();
       // fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_solution_list, container, false);
        mBinding.executePendingBindings();
        mBinding.solutionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mBinding.getRoot();
    }

    private void updateUI()
    {
        SolutionsManager solutionsManager = SolutionsManager.get(getActivity());
        List<Solution> solutions = solutionsManager.getAllSolutions();
        if (!solutions.isEmpty())
        {
            mBinding.noItemsRecycler.setVisibility(View.GONE);
        }
        else
        {
            mBinding.noItemsRecycler.setVisibility(View.VISIBLE);
        }
    }



    private class SolutionHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {

        public SolutionHolder(LayoutInflater inflater, ViewGroup parent, int id)
        {
            super(inflater.inflate(id, parent, false));
            //super(itemView);
        }
        @Override
        public void onClick(View v)
        {

        }
    }

    private class SolutionAdapter extends RecyclerView.Adapter<SolutionHolder>
    {
        private List<Solution> mSolutions;
        public SolutionAdapter(List<Solution> solutions)
        {
            mSolutions = solutions;
        }
        @Override
        public SolutionHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new SolutionHolder(layoutInflater, parent, R.layout.solution_list_item);
        }

        @Override
        public void onBindViewHolder(SolutionHolder holder, int position)
        {

        }

        @Override
        public int getItemCount()
        {
            return 0;
        }
    }




}
