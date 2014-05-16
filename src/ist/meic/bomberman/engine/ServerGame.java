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
	private List<ObjectOutputStream> outStreams;
	private MainThreadServer mainThreadServer;

	public ServerGame(GameActivity a, GameMapView gameArea,
			MapProperties mapProp, int maxPlayers) {
		super(a, gameArea, mapProp, maxPlayers);
		clientsSockets = new LinkedList<Socket>();
		outStreams = new LinkedList<ObjectOutputStream>();
		mainThreadServer = new MainThreadServer(this);
		mainThreadServer.start();
	}

	@Override
	public synchronized void movePlayer(int id, Direction direction) {
		super.movePlayer(id, direction);
		Log.i("MOVING PLAYER", "moving player "+ id + " to " + clientsSockets.size() + " clients");
		/* Send message to players with the movement */
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.MOVE.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					Player p = super.getPlayer(id);
					Log.i("MOVE", "Sending player " + p.getId() + " X:" + p.getX() + " Y:" + p.getY());
					s.writeObject(p);
					s.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void destroyObject(char type, int id){
		Log.i("REMOVE OBJECT", "destroying object type:" + type + " id:" + id + " to " + clientsSockets.size() + " clients");
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.REMOVE.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					s.writeChar(type);
					s.writeInt(id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void destroyExplosion(int explosionId){
		Log.i("REMOVE EXPLOSION", "removing explosion:" + explosionId + " to " + clientsSockets.size() + " clients");
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.REMOVE_EXPLOSION.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					s.writeInt(explosionId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void moveRobot(Robot robot){
		Log.i("MOVING ROBOT", "moving robot "+ robot.getId() + " to " + clientsSockets.size() + " clients");
		/* Send message to players with the movement */
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.MOVE.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					Log.i("MOVE", "Sending robot " + robot.getId() + " X:" + robot.getX() + " Y:" + robot.getY());
					s.writeObject(robot);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void sendExplosion(List<ExplosionPart> parts) {
		/* Send message to players with the movement */
		Log.i("PUT EXPLOSION", "sending explosion: "+ parts.get(0).getExplosionId() + " size: " + parts.size() + " to " + clientsSockets.size() + " clients");
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.PUT_EXPLOSION.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					s.writeObject(parts);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void sendBomb(Bomb bomb) {
		/* Send message to players with the movement */
		synchronized (outStreams) {
			for (ObjectOutputStream s : outStreams){
				try {
					s.writeInt(ServerUpdateType.MOVE.ordinal());
					s.writeBoolean(isTheEndOfTheGame());
					s.writeObject(bomb);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void addClient(Socket socketClient) {
			clientsSockets.add(socketClient);
			try {
				ObjectOutputStream os = new ObjectOutputStream(socketClient.getOutputStream());
				os.flush();
				outStreams.add(os);
			} catch (Exception e) {
			}
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
