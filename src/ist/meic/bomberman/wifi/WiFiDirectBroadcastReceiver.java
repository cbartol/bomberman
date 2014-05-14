package ist.meic.bomberman.wifi;

import ist.meic.bomberman.MultiplayerActivity;
import ist.meic.bomberman.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private MultiplayerActivity mActivity;

    public WiFiDirectBroadcastReceiver(MultiplayerActivity activity) {
        super();
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(mActivity, "WiFi Direct Enabled", Toast.LENGTH_SHORT).show();
            } else {
            	Toast.makeText(mActivity, "WiFi Direct Disabled", Toast.LENGTH_SHORT).show();
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	Log.i("WiFiDirectBroadCastReceiver", "WiFi P2P Peers Changed Action");
        	if (mActivity.getManager() != null) {
        		mActivity.getManager().requestPeers(mActivity.getChannel(), (PeerListListener) mActivity.getFragmentManager()
                        .findFragmentById(R.id.frag_servers_list));
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
