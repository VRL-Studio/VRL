package eu.mihosoft.vrl.v3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author nplekhanov
 */
public class Utils {

    public static long doubleNegate(long a) {
        if (a == 0) {
            return 0;
        }
        return 1;
    }

    public static <T> List<T> concat(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<>(a);
        list.addAll(b);
        return list;
    }

}
