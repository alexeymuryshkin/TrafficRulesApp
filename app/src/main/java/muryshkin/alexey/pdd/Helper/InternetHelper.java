package muryshkin.alexey.pdd.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import muryshkin.alexey.pdd.Data.Const;

/**
 * Created by 123 on 8/6/2016.
 */
public class InternetHelper {

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) Const.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
