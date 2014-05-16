package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ClientGame;
import ist.meic.bomberman.engine.DrawableObject;
import ist.meic.bomberman.engine.ExplosionPart;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import android.util.Log;

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
				int action = in.readInt();
				switch (ServerUpdateType.values()[action]) {
				case MOVE:
					DrawableObject object = (DrawableObject) in.readObject();
					object.setContext(game.getContext());
					endOfTheGame = in.readBoolean();
					Log.i("Got a move", "check next log message... " + object.getType() + " is the end?" + endOfTheGame);
					game.changeObject(object, endOfTheGame);
					break;
				case REMOVE:
					char type = in.readChar();
					int id = in.readInt();
					endOfTheGame = in.readBoolean();
					Log.i("REMOVE", "type: " + type + " id:" + id  + " is the end?" + endOfTheGame);
					game.removeObject(type, id, endOfTheGame);
					break;
				case REMOVE_EXPLOSION:
					int explosionId = in.readInt();
					endOfTheGame = in.readBoolean();
					Log.i("REMOVE EXPLOSION", " id:" + explosionId  + " is the end?" + endOfTheGame);
					game.removeExplosions(explosionId, endOfTheGame);
					break;
				case PUT_EXPLOSION:
					explosionId = in.readInt();
					List<ExplosionPart> expParts = (List<ExplosionPart>) in.readObject();
					for (ExplosionPart explosionPart : expParts) {
						explosionPart.setContext(game.getContext());
					}
					endOfTheGame = in.readBoolean();
					Log.i("PUT EXPLOSION", " id:" + explosionId  + " size:"+ expParts.size() +" is the end?" + endOfTheGame);
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
