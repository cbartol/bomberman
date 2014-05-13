package ist.meic.bomberman.wifi;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.id;
import ist.meic.bomberman.R.layout;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

	private List<WifiP2pDevice> items;
	private Activity activity;

	public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
		super(context, textViewResourceId, objects);
		items = objects;
		activity = (Activity) context;

	}

	@Override
	public int getCount() {
		return items.size();
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("WiFiPeerListAdapter", "getView");
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.server_element, null);
		}

		WifiP2pDevice device = items.get(position);
		if (device != null) {
			TextView top = (TextView) v.findViewById(R.id.device_name);
			TextView bottom = (TextView) v.findViewById(R.id.device_details);
			if (top != null) {
				Log.i("Device Name", device.deviceName);
				top.setText(device.deviceName);
			}
			if (bottom != null) {
				bottom.setText(getDeviceStatus(device.status));
			}
		}

		return v;
	}

	private String getDeviceStatus(int deviceStatus) {
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			return "Available";
		case WifiP2pDevice.INVITED:
			return "Invited";
		case WifiP2pDevice.CONNECTED:
			return "Connected";
		case WifiP2pDevice.FAILED:
			return "Failed";
		case WifiP2pDevice.UNAVAILABLE:
			return "Unavailable";
		default:
			return "Unknown";

		}
	}


}
