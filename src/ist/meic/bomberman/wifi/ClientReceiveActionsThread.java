package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ClientGame;
import ist.meic.bomberman.engine.DrawableObject;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceiveActionsThread extends Thread {
	private ClientGame game;
	private Socket connectionSocket;
	
	public ClientReceiveActionsThread(ClientGame game, Socket socket){
		this.game = game;
		this.connectionSocket = socket;
	}
	
	@Override
	public void run() {
		try{
			while(true){
				ObjectInputStream outToServer = new ObjectInputStream(connectionSocket.getInputStream());
				DrawableObject object = (DrawableObject) outToServer.readObject();
				game.changeObject(object);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
