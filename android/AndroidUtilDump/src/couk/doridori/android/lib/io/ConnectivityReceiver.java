package couk.doridori.android.lib.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A receiver for checking network status changes.
 *
 * Make sure to set <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> in your manifest
 *
 * This can be useful to reg in a base activity for your networked app if you are using in multiple places
 *
 * in onStart() - registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
 * in onStop() - unregisterReceiver(mConnectivityReceiver);
 *
 * User: doriancussen
 * Date: 31/10/2012
 */
public class ConnectivityReceiver extends BroadcastReceiver{

    private final ConnectivityListener mConnectivityListener;

    public ConnectivityReceiver(ConnectivityListener connectivityListener){
        mConnectivityListener = connectivityListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            mConnectivityListener.onConnectionAvailable();
        }else{
            mConnectivityListener.onConnectionUnavailable();
        }
    }

    public void register(Context context){
        context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

    public interface ConnectivityListener {

        /**
         * Called when a data connection has been established. Can use to
         * trigger any waiting behaviour
         */
        public void onConnectionAvailable();
        public void onConnectionUnavailable();
    }
}