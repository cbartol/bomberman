package ist.meic.bomberman.engine;

import ist.meic.bomberman.GameActivity;
import ist.meic.bomberman.wifi.MainThreadServer;
import ist.meic.bomberman.wifi.ServerUpdateType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class ServerGame extends Game {
	
	// client sockets to update the game state
	private HashMap<Integer, Socket> clientsSockets;
	private MainThreadServer mainThreadServer;
	private int idClient = 0;

	public ServerGame(GameActivity a, GameMapView gameArea,
			MapProperties mapProp, int maxPlayers) {
		super(a, gameArea, mapProp, maxPlayers);
		clientsSockets = new HashMap<Integer, Socket>();
		mainThreadServer = new MainThreadServer(this);
		mainThreadServer.start();
	}

	@Override
	public synchronized void movePlayer(int id, Direction direction) {
		super.movePlayer(id, direction);
		ObjectOutputStream os;
		/* Send message to players with the movement */
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.MOVE.ordinal());
				os.writeObject(super.getPlayer(id));
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void destroyObject(char type, int id){
		ObjectOutputStream os;
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.REMOVE.ordinal());
				os.writeChar(type);
				os.writeInt(id);
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void destroyExplosion(int explosionId){
		ObjectOutputStream os;
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.REMOVE_EXPLOSION.ordinal());
				os.writeInt(explosionId);
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void moveRobot(int id){
		super.moveRobot(id);
		ObjectOutputStream os;
		/* Send message to players with the movement */
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.MOVE.ordinal());
				os.writeObject(super.getRobots().get(id));
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void sendExplosion(List<ExplosionPart> parts) {
		ObjectOutputStream os;
		/* Send message to players with the movement */
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.PUT_EXPLOSION.ordinal());
				os.writeObject(parts);
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void sendBomb(Bomb bomb) {
		ObjectOutputStream os;
		/* Send message to players with the movement */
		for (Socket s : clientsSockets.values()){
			try {
				os = new ObjectOutputStream(s.getOutputStream());
				os.writeInt(ServerUpdateType.MOVE.ordinal());
				os.writeObject(bomb);
				os.writeBoolean(isTheEndOfTheGame());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addClient(Socket socketClient) {
			clientsSockets.put(++idClient, socketClient);
			// Send game state do socketClient
			updateGameState(socketClient);
	}
	
	private void updateGameState(Socket socketclient) {
		
	}
	@Override
	public void endGame() {
		super.endGame();
		mainThreadServer.interrupt();
	}

}
