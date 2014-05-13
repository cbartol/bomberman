package ist.meic.bomberman;

import ist.meic.bomberman.wifi.WiFiPeerListAdapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServerListFragment extends ListFragment implements
PeerListListener {

	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
	View mContentView = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.server_element, peers));

	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.servers_list, null);
        return mContentView;
    }

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peersList) {
		Log.i("SERVERLISTFRAG", "On Peers Available");
		peers.clear();
		peers.addAll(peersList.getDeviceList());
		((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if (peers.size() == 0) {
			return;
		}

	}
	
	public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

}
