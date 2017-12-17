package com.hunter04d.android.booleanminimizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.hunter04d.android.booleanminimizer.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Den on 17.12.2017.
 */

public class SolveOptionDialogFragment extends DialogFragment
{
    public static SolveOptionDialogFragment newInstance()
    {
        SolveOptionDialogFragment fragment = new SolveOptionDialogFragment();
        return fragment;
    }

    public static final String RESULT_WHICH = "which";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.solve_option_title).setItems(R.array.sovle_options, (dialog, which) ->
        {
            sendResult(RESULT_OK, which);

        });
        return builder.create();
    }

    private void sendResult(int resultCode, int which)
    {
        if (getTargetFragment() == null)
        {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(RESULT_WHICH, which);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
