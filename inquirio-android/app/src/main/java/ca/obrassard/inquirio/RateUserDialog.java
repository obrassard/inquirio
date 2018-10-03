package ca.obrassard.inquirio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import ca.obrassard.inquirio.transfer.NotificationSummary;

public class RateUserDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_rate_user, null);

        RatingBar rb = v.findViewById(R.id.user_rating);
        TextView title = v.findViewById(R.id.title);
        TextView message = v.findViewById(R.id.msg);

        long id = this.getArguments().getLong("notifId");
        // TODO -> Obtention des details selon l'id de la notification
        NotificationSummary ns = new NotificationSummary();
        ns.senderName = "Olivier"; ns.itemName="Caniche royal";

        title.setText(getString(R.string.rate, ns.senderName));
        message.setText(getString(R.string.rate_dialog_txt, ns.senderName, ns.itemName));


        builder.setView(v);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Enregister
                //rb.getRating();
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

}
