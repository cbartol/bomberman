package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ClientGame;
import ist.meic.bomberman.engine.DrawableObject;
import ist.meic.bomberman.engine.ExplosionPart;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

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
				ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
				
				switch (ServerUpdateType.values()[in.readInt()]) {
				case MOVE:
					DrawableObject object = (DrawableObject) in.readObject();
					game.changeObject(object);
					break;
				case REMOVE:
					char type = in.readChar();
					int id = in.readInt();
					game.removeObject(type, id);
					break;
				case REMOVE_EXPLOSION:
					int explosionId = in.readInt();
					game.removeExplosions(explosionId);;
					break;
				case PUT_EXPLOSION:
					explosionId = in.readInt();
					List<ExplosionPart> expParts = (List<ExplosionPart>) in.readObject();
					game.addExplosions(explosionId, expParts);
					break;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
