package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buzi.phonecleaner.R;

public class AdapterGridview extends BaseAdapter{
    private Context context; 
	
	public AdapterGridview(Context context){
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
		Holder holder = null;
		
		if (convertView == null) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        	holder = new Holder(); 
        	LayoutInflater inflate = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE ); 
        	convertView =  inflate.inflate(R.layout.item_gridview_main, null);
        	TextView texto = (TextView) convertView.findViewById(R.id.texto);
            texto.setTypeface(typeface);
            texto.setCompoundDrawablesWithIntrinsicBounds(0,icons[position],0,0);
        	holder.setTexto(texto);
        	convertView.setTag(holder); 
        } else {
        	holder = (Holder) convertView.getTag();
        }

	     holder.getTextView().setText(title[position]);
		
		return convertView;
	}
	
	
	public class Holder{

	      public TextView  texto;
	      
	      public void setTexto(TextView texto){
	    	    this.texto = texto;
	      }
	      
	      public TextView getTextView(){
	            return texto;
	      }
     }
	
	
	private Integer[] icons = {R.drawable.cache, R.drawable.call_msg, R.drawable.app, R.drawable.history, R.drawable.ram, R.drawable.batery};
	
	private String[]  title = {"Cache","Call/Msg", "App Manager", "History", "Optimizer Ram","Batery"};

}
