package ca.obrassard.inquirio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ca.obrassard.inquirio.model.LostItem;
import ca.obrassard.inquirio.model.Notification;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(Context context) {
        super(context, R.layout.notification_listrow);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View vue = li.inflate(R.layout.notification_listrow, null);

        //Obtention des controles de l'adapter ici
        TextView title = vue.findViewById(R.id.notif_title);
        TextView itemDetail = vue.findViewById(R.id.item_detail);
        ImageView img = vue.findViewById(R.id.img_type);

        //Item selectionné
        final Notification item = getItem(position);

        switch (item.getType()) {
            case ItemMightBeFound:
                title.setText(getContext().getString(R.string.notif_user_may_have_found, item.getSender().getFullname()));
                itemDetail.setText(item.getItem().getTitle());
                img.setImageResource(R.drawable.speaker);
                break;
            case ConfirmItemIsFound:
                title.setText(getContext().getString(R.string.notif_confirm_item_is_found));
                itemDetail.setText(item.getItem().getTitle());
                img.setImageResource(R.drawable.handshake);
                break;
        }

        //OnClickListeners
        //TODO : Event du clic sur une des row

        return vue;
    }
}
