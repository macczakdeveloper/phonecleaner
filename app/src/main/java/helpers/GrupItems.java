package helpers;

import java.util.ArrayList;
import java.util.List;

public class GrupItems {

    public String title = null;
    public int icon = 0;

    public final List<String> list = new ArrayList<String>();

    public GrupItems(String t, int i) {
        title = t;
        icon = i;
    }
}
