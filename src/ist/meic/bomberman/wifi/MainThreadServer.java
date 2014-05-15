package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.Game;
import ist.meic.bomberman.engine.ServerGame;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainThreadServer extends Thread {
	
	private ServerGame game;
	private ServerSocket mainSocket;
	private int port = 20000;
	private boolean gameRunnig = true; 

	public MainThreadServer(ServerGame game) {
		this.game = game;
	}

	@Override
	public void run() {
		try {
			mainSocket = new ServerSocket(port);
			while(gameRunnig) {
				Socket clientSocket = mainSocket.accept();

				DataInputStream input = new DataInputStream(clientSocket.getInputStream());
				boolean isRead = input.readBoolean();

				// Wants to join
				if(!isRead) {
					game.addClient(clientSocket);					
				} else {

				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
