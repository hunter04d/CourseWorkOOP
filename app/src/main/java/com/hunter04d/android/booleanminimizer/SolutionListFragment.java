package com.hunter04d.android.booleanminimizer;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    public static final String RESULT_EXPRESSION  = "expression";
    public static final String RESULT_VAR_NAMES  = "var_names";
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.history);
        mBinding.solutionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.solutionRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.custToolbar);
        updateUI();
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_solution_list,menu);
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
        else
        {
            mSolutionAdapter.setSolutions(solutions);
            mSolutionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case  R.id.action_delete_all:
            {
                SolutionsManager.get(getContext()).deleteAllSolutions();
                updateUI();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SolutionHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        Solution mSolution;
        TextView mExprEditText;
        TextView mSolutionEditText;

        public SolutionHolder(LayoutInflater inflater, ViewGroup parent, int id)
        {
            super(inflater.inflate(id, parent, false));
            itemView.setOnClickListener(this);
            mExprEditText = itemView.findViewById(R.id.solution_list_expression);
            mSolutionEditText = itemView.findViewById(R.id.solution_list_solution);
        }

         public void bind(Solution solution)
        {
            mSolution = solution;
            mExprEditText.setText(solution.getExpression());
            mSolutionEditText.setText("= " + solution.getLinearResult());

        }
        @Override
        public void onClick(View v)
        {
            Intent i = new Intent();
            i.putExtra(RESULT_EXPRESSION,mSolution.getExpression());
            i.putExtra(RESULT_VAR_NAMES, mSolution.getVarNames());
            getActivity().setResult(Activity.RESULT_OK, i);
            getActivity().onBackPressed();
        }
    }

    private class SolutionAdapter extends RecyclerView.Adapter<SolutionHolder>
    {
        private List<Solution> mSolutions;
        public SolutionAdapter(List<Solution> solutions)
        {
            mSolutions = solutions;
        }

        public void setSolutions(List<Solution> solutions)
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
            holder.bind(mSolutions.get(mSolutions.size() - position - 1));
        }

        @Override
        public int getItemCount()
        {
            return mSolutions.size();
        }
    }




}
