package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ServerGame;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class MainThreadServer extends Thread {
	
	private ServerGame game;
	private ServerSocket mainSocket;
	private int port = 20000;
	private boolean gameRunnig = true; 
	private List<ServerReceiver> threads = new ArrayList<ServerReceiver>();

	public MainThreadServer(ServerGame game) {
		this.game = game;
	}

	@Override
	public void run() {
		try {
			mainSocket = new ServerSocket(port);
			while(gameRunnig) {
				Log.i("MainThreadServer", "Server is running and waiting requests");
				Socket clientSocket = mainSocket.accept();

				DataInputStream input = new DataInputStream(clientSocket.getInputStream());
				boolean isRead = input.readBoolean();

				// Wants to join
				if(!isRead) {
					game.addClient(clientSocket);					
				} else {
					ServerReceiver thread = new ServerReceiver(clientSocket, game);
					threads.add(thread);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				mainSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
