package ist.meic.bomberman;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;

public interface DeviceActionListener {
	void tryConnect(WifiP2pDevice device);

	void cancelDisconnect();

	void connect(WifiP2pConfig config);

	void disconnect();
}
