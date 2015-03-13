package guru.apps.llc.appetite.main;

/**
 * Object representing a list item in the navigation drawer.
 *
 * Created by jrpotter on 2/7/15.
 */
public class DrawerItem {

    public int iconId;
    public String name;

    public DrawerItem(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

}
