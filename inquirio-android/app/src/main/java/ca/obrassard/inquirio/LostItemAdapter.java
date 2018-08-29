package ca.obrassard.inquirio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.obrassard.inquirio.model.LostItem;

public class LostItemAdapter extends ArrayAdapter<LostItem>{

    public LostItem getItem(long id){
        throw new UnsupportedOperationException();
    }

    public LostItemAdapter(Context context) {
        super(context, R.layout.lost_items_listrow);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View vue = li.inflate(R.layout.lost_items_listrow,null);

        //Obtention des controles de l'adapter ici
        TextView title = vue.findViewById(R.id.item_name);
        TextView emplacement = vue.findViewById(R.id.item_location);

        //Item selectionné
        final LostItem item = getItem(position);
        title.setText(item.getTitle());
        emplacement.setText(item.getLocation());

        //OnClickListeners
       //TODO : Event du clic sur une des row / bouton

        return vue;
    }
}