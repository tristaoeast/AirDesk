package pt.ulisboa.tecnico.cmov.airdesk;

import java.util.HashSet;
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
}
