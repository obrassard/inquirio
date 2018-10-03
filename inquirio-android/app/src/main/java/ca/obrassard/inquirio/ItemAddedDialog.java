package ca.obrassard.inquirio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ItemAddedDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_itemcreated, null);
        ((TextView) v.findViewById(R.id.text)).setText(
                getString(R.string.itemaddeddesc, getArguments().getString("itemplace"))
        );
        builder.setView(v);
        builder.setPositiveButton(R.string.end, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().finish();
        Intent i = new Intent(getActivity(),ItemsDetailActivity.class);
        i.putExtra("item.id",getArguments().getLong("itemid"));
        startActivity(i);
    }
}
