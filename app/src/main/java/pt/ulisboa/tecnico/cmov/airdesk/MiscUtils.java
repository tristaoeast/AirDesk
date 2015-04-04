package pt.ulisboa.tecnico.cmov.airdesk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by TMC on 03/04/2015.
 */
public class MiscUtils {

    public HashSet<String> stringToSetTokenzier(String str, String token) {


            HashSet<String> hSet = new HashSet<String>();
            StringTokenizer st = new StringTokenizer(str, token);
            while (st.hasMoreTokens())
                hSet.add(st.nextToken());

        return hSet;
    }

    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
