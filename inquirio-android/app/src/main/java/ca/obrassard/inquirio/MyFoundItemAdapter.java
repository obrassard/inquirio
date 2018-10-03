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
import ca.obrassard.inquirio.transfer.FoundItemSummary;
import ca.obrassard.inquirio.transfer.LostItemSummary;

public class MyFoundItemAdapter extends ArrayAdapter<FoundItemSummary>{

    public LostItemSummary getItem(long id){
        throw new UnsupportedOperationException();
    }

    public MyFoundItemAdapter(Context context) {
        super(context, R.layout.my_items_listrow);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View vue = li.inflate(R.layout.my_items_listrow,null);

        //Obtention des controles de l'adapter ici
        TextView title = vue.findViewById(R.id.item_name);
        TextView emplacement = vue.findViewById(R.id.item_location);

        ImageView i = vue.findViewById(R.id.img_category);
        i.setImageResource(R.drawable.handshake);

        //Item
        final FoundItemSummary item = getItem(position);
        title.setText(item.itemName);

        emplacement.setText(getContext().getString(R.string.foundby,item.finderName));

        return vue;
    }
}