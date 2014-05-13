package ist.meic.bomberman;

import ist.meic.bomberman.wifi.WiFiDirectBroadcastReceiver;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;

public class MultiplayerActivity extends Activity {
	
	WiFiDirectBroadcastReceiver mReceiver;
	WifiP2pManager mManager;
	IntentFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer);
		
		filter = new IntentFilter();
		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		mReceiver = new WiFiDirectBroadcastReceiver(this);
		registerReceiver(mReceiver, filter);
	}
	
	public void createserver(View v) {
		Intent intent = new Intent(MultiplayerActivity.this,CreateServerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}
	
	public void findserver(View v) {
		Intent intent = new Intent(MultiplayerActivity.this,FindServerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    MultiplayerActivity.this.overridePendingTransition(0, 0);
	}
	
	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
	    super.onResume();
	    registerReceiver(mReceiver, filter);
	}
	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
	    super.onPause();
	    unregisterReceiver(mReceiver);
	}
}
