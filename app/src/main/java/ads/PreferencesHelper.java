package ads;

import android.content.Context;
import android.content.SharedPreferences;
 
public class PreferencesHelper {
    private SharedPreferences settings;
    
    public PreferencesHelper(Context context){
    	settings = context.getSharedPreferences("ACleaner", Context.MODE_PRIVATE);
    }
    
    public PreferencesHelper(String Shared_pref_name, Context context) {
        settings = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
    }
 
    public boolean isExist(String val) {
       String value = settings.getString(val, null);
       return value != null;
    }
 
    public String getStringfrom_shprf(String val) {
        String valor = settings.getString(val, null);
        return valor;
    }
 
    public void writeString(String clave, String valor) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(clave, valor);
        editor.commit();
    }
 
    public void removeValue(String clave) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(clave);
        editor.commit();
    }
    
}