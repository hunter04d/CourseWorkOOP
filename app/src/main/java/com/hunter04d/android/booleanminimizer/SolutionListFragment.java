package com.hunter04d.android.booleanminimizer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hunter04d.android.booleanminimizer.database.Solution;
import com.hunter04d.android.booleanminimizer.database.SolutionsManager;
import com.hunter04d.android.booleanminimizer.databinding.FragmentSolutionListBinding;
import com.hunter04d.android.booleanminimizer.databinding.ListItemSolutionBinding;

import java.util.List;

/**
 * Created by hunter04d on 13.12.2017.
 */

public class SolutionListFragment extends Fragment
{

    private FragmentSolutionListBinding mBinding;
    private SolutionAdapter mSolutionAdapter;
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        updateUI();
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
        if (mSolutionAdapter == null)
        {
            mSolutionAdapter = new SolutionAdapter(solutions);
            mBinding.solutionRecyclerView.setAdapter(mSolutionAdapter);
        }
    }



    private class SolutionHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {
        TextView mExprEditText;
        TextView mSolutionEditText;

        public SolutionHolder(LayoutInflater inflater, ViewGroup parent, int id)
        {
            super(inflater.inflate(id, parent, false));
            itemView.setOnClickListener(this);
            mExprEditText = (EditText) itemView.findViewById(R.id.solution_list_expression);
            mSolutionEditText = (EditText) itemView.findViewById(R.id.solution_list_solution);

        }

         public void bind(Solution solution)
        {
            mExprEditText.setText(solution.getExpression());
            mSolutionEditText.setText(solution.getLinearResult());

        }
        @Override
        public void onClick(View v)
        {
            //getActivity().setResult();
            getActivity().finish();
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
            return new SolutionHolder(layoutInflater, parent, R.layout.list_item_solution);
        }

        @Override
        public void onBindViewHolder(SolutionHolder holder, int position)
        {
            holder.bind(mSolutions.get(mSolutions.size() - position));
        }

        @Override
        public int getItemCount()
        {
            return mSolutions.size();
        }
    }




}
