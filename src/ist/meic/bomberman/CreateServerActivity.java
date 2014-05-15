package ist.meic.bomberman;

import ist.meic.bomberman.engine.MapProperties;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateServerActivity extends Activity {
	private int levels = 1;
	private MapProperties selectedMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_server);
		Resources res = getResources();
		levels = res.getInteger(R.integer.max_levels);
		addItemsOnLevelSpinner();
		TextView myIp = (TextView) findViewById(R.id.my_ip);
		final Inet4Address inet4Address = getWifiInetAddress(this, Inet4Address.class);
		myIp.setText(inet4Address.toString());
		//TODO: show some where the IP and the PORT of the server
	}


	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    CreateServerActivity.this.overridePendingTransition(0, 0);
	}
	
	public void newGame(View v) {
		Intent intent = new Intent(CreateServerActivity.this,
				MultiplayerServerGameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("level", selectedMap.getLevel());
		startActivity(intent);
	}
	
	public void addItemsOnLevelSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.levelSpinner);
		List<String> list = new ArrayList<String>();
		for(int i = 1 ; i <= levels ; i++){
			list.add("Level " + i);
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		final CreateServerActivity context = this;
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				int level = pos +1;
				context.setLevel(level);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	private void setLevel(int level){
		selectedMap = new MapProperties(this, level);
		TextView levelTitle = (TextView) findViewById(R.id.levelTitle);
		levelTitle.setText(selectedMap.getName());
		ImageView preview = (ImageView) findViewById(R.id.mapView);
		int mapId = getResources().getIdentifier("map" + selectedMap.getLevel() + "_preview", "drawable", getPackageName());
		preview.setImageDrawable(getResources().getDrawable(mapId));
	}

	/*protected String wifiIpAddress(Context context) {
	    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	    // Convert little-endian to big-endianif needed
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        ipAddress = Integer.reverseBytes(ipAddress);
	    }

	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	    String ipAddressString;
	    try {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) {
	        Log.e("WIFIIP", "Unable to get host address.");
	        ipAddressString = null;
	    }

	    return ipAddressString;
	}
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e("WiFi ip", ex.toString());
	    }
	    return null;
	}*/
	public static Enumeration<InetAddress> getWifiInetAddresses(final Context context) {
	    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    final String macAddress = wifiInfo.getMacAddress();
	    final String[] macParts = macAddress.split(":");
	    final byte[] macBytes = new byte[macParts.length];
	    for (int i = 0; i< macParts.length; i++) {
	        macBytes[i] = (byte)Integer.parseInt(macParts[i], 16);
	    }
	    try {
	        final Enumeration<NetworkInterface> e =  NetworkInterface.getNetworkInterfaces();
	        while (e.hasMoreElements()) {
	            final NetworkInterface networkInterface = e.nextElement();
	            if (Arrays.equals(networkInterface.getHardwareAddress(), macBytes)) {
	                return networkInterface.getInetAddresses();
	            }
	        }
	    } catch (SocketException e) {
	        Log.wtf("WIFIIP", "Unable to NetworkInterface.getNetworkInterfaces()");
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public static<T extends InetAddress> T getWifiInetAddress(final Context context, final Class<T> inetClass) {
	    final Enumeration<InetAddress> e = getWifiInetAddresses(context);
	    while (e.hasMoreElements()) {
	        final InetAddress inetAddress = e.nextElement();
	        if (inetAddress.getClass() == inetClass) {
	            return (T)inetAddress;
	        }
	    }
	    return null;
	}
}
