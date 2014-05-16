package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ClientGame;
import ist.meic.bomberman.engine.DrawableObject;
import ist.meic.bomberman.engine.ExplosionPart;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

import android.util.Log;

public class ClientReceiveActionsThread extends Thread {
	private ClientGame game;
	private String server;
	private int port;
	
	public ClientReceiveActionsThread(ClientGame game, String server, int port){
		this.game = game;
		this.server = server;
		this.port = port;
	}
	
	@Override
	public void run() {
		Socket receiveSocket = null;
		try{
			receiveSocket = new Socket(server,port);
			DataOutputStream outToServer = new DataOutputStream(receiveSocket.getOutputStream());
			outToServer.writeBoolean(true);

			ObjectInputStream in = new ObjectInputStream(receiveSocket.getInputStream());
			while(true){
				boolean endOfTheGame;
				while(in.available() > 0){
					int action = in.readInt();
					Log.i("ACTION", "Got action " + ServerUpdateType.values()[action].name());
					switch (ServerUpdateType.values()[action]) {
					case MOVE:
						endOfTheGame = in.readBoolean();
						DrawableObject object = (DrawableObject) in.readObject();
						object.setContext(game.getContext());
						Log.i("Got a move", "check next log message... " + object.getType() + " is the end?" + endOfTheGame);
						game.changeObject(object, endOfTheGame);
						break;
					case REMOVE:
						endOfTheGame = in.readBoolean();
						char type = in.readChar();
						int id = in.readInt();
						Log.i("REMOVE", "type: " + type + " id:" + id  + " is the end?" + endOfTheGame);
						game.removeObject(type, id, endOfTheGame);
						break;
					case REMOVE_EXPLOSION:
						endOfTheGame = in.readBoolean();
						int explosionId = in.readInt();
						Log.i("REMOVE EXPLOSION", " id:" + explosionId  + " is the end?" + endOfTheGame);
						game.removeExplosions(explosionId, endOfTheGame);
						break;
					case PUT_EXPLOSION:
						endOfTheGame = in.readBoolean();
						explosionId = in.readInt();
						List<ExplosionPart> expParts = (List<ExplosionPart>) in.readObject();
						for (ExplosionPart explosionPart : expParts) {
							explosionPart.setContext(game.getContext());
						}
						Log.i("PUT EXPLOSION", " id:" + explosionId  + " size:"+ expParts.size() +" is the end?" + endOfTheGame);
						game.addExplosions(explosionId, expParts, endOfTheGame);
						break;
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			try {
				receiveSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
