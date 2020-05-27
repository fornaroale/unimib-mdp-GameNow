package it.unimib.disco.gruppoade.gamenow.fragments.profile;

import java.util.Comparator;

public class TagComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (o1 != null && o2 != null) {
            o1 = o1.toLowerCase();
            o2 = o2.toLowerCase();
            return o1.compareTo(o2);
        }


        return 0;

    }
}
