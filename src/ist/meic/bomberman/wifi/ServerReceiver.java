package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ServerGame;

import java.net.Socket;

public class ServerReceiver extends Thread {
	private Socket socket;
	private ServerGame game;
	
	public ServerReceiver (Socket socket, ServerGame server){
		this.socket = socket;
		this.game = server;
	}
	
	@Override
	public void run() {
		super.run();
		while(true) {
			//socket.getInputStream();
		}
		// TODO Auto-generated method stub
	}
}	
