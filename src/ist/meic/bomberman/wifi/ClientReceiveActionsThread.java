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
				boolean endOfTheGame;
				switch (ServerUpdateType.values()[in.readInt()]) {
				case MOVE:
					DrawableObject object = (DrawableObject) in.readObject();
					object.setContext(game.getContext());
					object.reloadImage();
					endOfTheGame = in.readBoolean();
					game.changeObject(object, endOfTheGame);
					break;
				case REMOVE:
					char type = in.readChar();
					int id = in.readInt();
					endOfTheGame = in.readBoolean();
					game.removeObject(type, id, endOfTheGame);
					break;
				case REMOVE_EXPLOSION:
					int explosionId = in.readInt();
					endOfTheGame = in.readBoolean();
					game.removeExplosions(explosionId, endOfTheGame);
					break;
				case PUT_EXPLOSION:
					explosionId = in.readInt();
					List<ExplosionPart> expParts = (List<ExplosionPart>) in.readObject();
					for (ExplosionPart explosionPart : expParts) {
						explosionPart.setContext(game.getContext());
					}
					endOfTheGame = in.readBoolean();
					game.addExplosions(explosionId, expParts, endOfTheGame);
					break;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
