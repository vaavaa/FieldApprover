package com.asiawaters.fieldapprover.classes;


import android.content.Context;
import android.net.ConnectivityManager;

public class  Network_Helper {

    public static boolean isOnline(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static Boolean isConnectedbyPing(String host) {
        if (host.length() ==0) host="www.google.com";
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+host);
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
