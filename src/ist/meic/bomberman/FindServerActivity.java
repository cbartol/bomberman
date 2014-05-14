package ist.meic.bomberman;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FindServerActivity extends MultiplayerActivity implements DeviceActionListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_server);
		
		final ServerListFragment fragment = (ServerListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_servers_list);
		mManager.discoverPeers(mChannel, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.i("FindServerActivity", "DiscoverPeers.onSuccess");

			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public void searchserver(View view) {
		mManager.discoverPeers(mChannel, new ActionListener() {

			@Override
			public void onSuccess() {
				Log.i("FindServerActivity", "DiscoverPeers.onSuccess");

			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FindServerActivity.this.overridePendingTransition(0, 0);
	}

	@Override
	public void cancelDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(WifiP2pConfig config) {
		mManager.connect(mChannel, config, new ActionListener() {

            @Override
            public void onSuccess() {
            	Log.i("FindServerActivity", "Connected");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(FindServerActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tryConnect(WifiP2pDevice device) {
		Log.i("FindServerActivity", "Try connect to " + device.deviceName);
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		connect(config);
	}	
}