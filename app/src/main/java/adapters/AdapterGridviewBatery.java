package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buzi.phonecleaner.R;

public class AdapterGridviewBatery extends BaseAdapter{
    private Context context;

	public AdapterGridviewBatery(Context context){
		this.context = context;
	}
	
	@Override
	public int getCount() {	return title.length; }

	@Override
	public Object getItem(int position) { return null; }

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        if(arrayView[position] == null){
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
            LayoutInflater inflate = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView =  inflate.inflate(R.layout.item_gridview_main, null);
            TextView texto = (TextView) convertView.findViewById(R.id.texto);
            texto.setText(title[position]);
            texto.setTypeface(typeface);
            texto.setCompoundDrawablesWithIntrinsicBounds(0, icons[position], 0, 0);
            arrayView[position] = convertView;
        }

		return arrayView[position];
	}

    public void changedImage(int drawable, int position){
        TextView textViews = (TextView) arrayView[position].findViewById(R.id.texto);
        textViews.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0);
    }

    public View[] arrayView = {null,null, null, null, null, null};


    public Integer[] icons = {R.drawable.wifi_off, R.drawable.data_off, R.drawable.bri_all, R.drawable.vol_all, R.drawable.gps_off, R.drawable.time_out_10m};
	
	public String[]  title = {"Wifi","Data", "Brightness", "Volumen", "GPS", "Time out"};

}
