package ist.meic.bomberman.engine;

import ist.meic.bomberman.GameActivity;
import ist.meic.bomberman.wifi.MainThreadServer;
import ist.meic.bomberman.wifi.ServerUpdateType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class ServerGame extends Game {
	
	// client sockets to update the game state
	private List<Socket> clientsSockets;
	private MainThreadServer mainThreadServer;

	public ServerGame(GameActivity a, GameMapView gameArea,
			MapProperties mapProp, int maxPlayers) {
		super(a, gameArea, mapProp, maxPlayers);
		clientsSockets = new LinkedList<Socket>();
		mainThreadServer = new MainThreadServer(this);
		mainThreadServer.start();
	}

	@Override
	public synchronized void movePlayer(int id, Direction direction) {
		super.movePlayer(id, direction);
		ObjectOutputStream os;
		Log.i("MOVING PLAYER", "moving player "+ id + " to " + clientsSockets.size() + " clients");
		/* Send message to players with the movement */
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
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
	}

	@Override
	protected void destroyObject(char type, int id){
		ObjectOutputStream os;
		Log.i("REMOVE OBJECT", "destroying object type:" + type + " id:" + id + " to " + clientsSockets.size() + " clients");
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
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
	}

	@Override
	protected void destroyExplosion(int explosionId){
		ObjectOutputStream os;
		Log.i("REMOVE EXPLOSION", "removing explosion:" + explosionId + " to " + clientsSockets.size() + " clients");
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
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
	}
	
	@Override
	protected void moveRobot(Robot robot){
		ObjectOutputStream os;
		Log.i("MOVING ROBOT", "moving robot "+ robot.getId() + " to " + clientsSockets.size() + " clients");
		/* Send message to players with the movement */
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
				try {
					os = new ObjectOutputStream(s.getOutputStream());
					os.writeInt(ServerUpdateType.MOVE.ordinal());
					os.writeObject(robot);
					os.writeBoolean(isTheEndOfTheGame());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void sendExplosion(List<ExplosionPart> parts) {
		ObjectOutputStream os;
		/* Send message to players with the movement */
		Log.i("PUT EXPLOSION", "sending explosion: "+ parts.get(0).getExplosionId() + " size: " + parts.size() + " to " + clientsSockets.size() + " clients");
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
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
	}
	
	@Override
	protected void sendBomb(Bomb bomb) {
		ObjectOutputStream os;
		/* Send message to players with the movement */
		synchronized (clientsSockets) {
			for (Socket s : clientsSockets){
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
	}
	
	public void addClient(Socket socketClient) {
			clientsSockets.add(socketClient);
	}
	
	@Override
	public void endGame() {
		super.endGame();
		mainThreadServer.interrupt();
		for (Socket socket : clientsSockets) {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

}
