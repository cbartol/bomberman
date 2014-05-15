package ist.meic.bomberman.wifi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public class WiFiConnectionInfoListener implements ConnectionInfoListener {
	
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		
		// InetAddress from WifiP2pInfo struct.
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
        	ServerAsyncTask server = new ServerAsyncTask();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
        	Socket clientSocket = new Socket();
        	try {
    			clientSocket.bind(null);
    		    clientSocket.connect((new InetSocketAddress("10.0.2.2", 4444)), 500);
    		    PrintWriter printwriter = new PrintWriter(clientSocket.getOutputStream(),true);
                printwriter.write("Enviei esta mensagem");
                printwriter.flush();
                printwriter.close();
                clientSocket.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
		
	}	

}
