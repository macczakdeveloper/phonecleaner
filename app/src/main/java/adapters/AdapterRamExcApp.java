package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import helpers.InfoAppOnRam;

import java.util.List;

public class AdapterRamExcApp extends ArrayAdapter<InfoAppOnRam> {

    private List<InfoAppOnRam> appsList = null;
    private Context context;

    public AdapterRamExcApp(Context context, int textViewResourceId, List<InfoAppOnRam> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public InfoAppOnRam getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final InfoAppOnRam item = appsList.get(position);

        if(item != null){

            if(item.view == null){
                item.view = convertView;
            }

        }

        return item.view;
    }
}
