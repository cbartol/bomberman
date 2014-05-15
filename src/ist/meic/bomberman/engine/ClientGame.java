package ist.meic.bomberman.engine;

import ist.meic.bomberman.MultiplayerClientGameActivity;
import ist.meic.bomberman.multiplayer.GameState;
import ist.meic.bomberman.wifi.ClientReceiveActionsThread;
import ist.meic.bomberman.wifi.PlayerAction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClientGame extends Thread implements IGame {
	private ClientReceiveActionsThread receiveThread;
	private Socket sendSocket;
	
	private List<Wall> walls;
	private Map<Integer, Obstacle> obstacles; 
	private Map<Integer, Player> players;
	private Map<Integer, Player> playersAlive;
	private Map<Integer, Robot> robots;
	private Map<Integer, Bomb> bombs;
	private Map<Integer, List<ExplosionPart>> explosionParts;
	private int width;
	private int height;
	
	private DataOutputStream out;
	private DataInputStream in;
	
	private MultiplayerClientGameActivity activity;
	private GameMapView gameArea;
	private String server;
	private int port;
	
	public ClientGame(MultiplayerClientGameActivity activity, GameMapView gameArea, String server, int port){
		this.activity = activity;
		this.gameArea = gameArea;
		this.server = server;
		this. port = port;
	}
	
	@Override
	public void run() {
		try {
			
			sendSocket = new Socket(server,port);
			DataOutputStream outToServer = new DataOutputStream(sendSocket.getOutputStream());
			outToServer.writeBoolean(false);
			out = outToServer;
			in = new DataInputStream(sendSocket.getInputStream());
	
			ObjectInputStream fetchState = new ObjectInputStream(sendSocket.getInputStream());
			GameState state = (GameState) fetchState.readObject();
			walls = state.getWalls();
			obstacles = Collections.synchronizedMap(new TreeMap<Integer, Obstacle>());
			for (Obstacle obstacle : state.getObstacles()) {
				obstacles.put(obstacle.getId(), obstacle);
			}
			players = state.getPlayers();
			playersAlive = state.getPlayersAlive();
			robots = state.getRobots();
			
			bombs = Collections.synchronizedMap(new TreeMap<Integer, Bomb>());
			for (Bomb bomb : state.getBombs()) {
				bombs.put(bomb.getExplosionId(), bomb);
			}
			explosionParts = state.getExplosionParts();
			
			width = state.getMapWidth();
			height = state.getMapHeight();
			
			activity.setPlayerId(state.getPlayerId());
			int stripeSize = walls.get(0).getImage().getWidth();
			gameArea.setGame(this, width*stripeSize, height*stripeSize);
			activity.releaseTheUI();
			
				
			Socket receiveSocket = new Socket(server,port);
			outToServer = new DataOutputStream(receiveSocket.getOutputStream());
			outToServer.writeBoolean(true);
			receiveThread = new ClientReceiveActionsThread(this, receiveSocket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void movePlayer(int playerId, Direction direction) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.MOVE.ordinal()); // move instruction
			out.writeInt(direction.ordinal());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void dropBomb(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.DROP_BOMB.ordinal()); // bomb instruction
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void endGame() {
		receiveThread.interrupt();
	}

	@Override
	public void pausePlayer(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.PAUSE.ordinal()); // pause instruction
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void resumePlayer(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.RESUME.ordinal()); // resume instruction
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean isPlayerWinner(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.IS_WINNER.ordinal()); // is winner instruction
			return in.readBoolean();
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getPlayerScore(int playerId) {
		return playersAlive.get(playerId).getScore();
	}

	public void changeObject(DrawableObject object, boolean isEndOftheGame) {
		char type = object.getType();
		switch (type) {
			case Game.PLAYER:
				Player player = (Player) object;
				players.put(player.getId(), player);
				break;
			case Game.ROBOT:
				Robot robot = (Robot) object;
				robots.put(robot.getId(), robot);
				break;
			case Game.BOMB:
				Bomb bomb = (Bomb) object; 
				bombs.put(bomb.getExplosionId(), bomb);
				break;
			default:
				// ????
				break;
		}
		// change the list
		activity.draw(isEndOftheGame);
	}

	public void removeObject(char type, int id, boolean isEndOftheGame) {
		switch (type) {
			case Game.PLAYER:
				players.remove(id);
				break;
			case Game.ROBOT:
				robots.remove(id);
				break;
			case Game.OBSTACLE:
				obstacles.remove(id);
				break;
			case Game.BOMB:
				bombs.remove(id);
				break;
			default:
				// ????
				break;
		}
		activity.draw(isEndOftheGame);
	}
	
	public void addExplosions(int explosionId, List<ExplosionPart> expParts, boolean isEndOftheGame){
		explosionParts.put(explosionId, expParts);
		bombs.get(explosionId).explode();
		activity.draw(isEndOftheGame);
	}

	public void removeExplosions(int explosionId, boolean isEndOftheGame){
		explosionParts.remove(explosionId);
		bombs.remove(explosionId);
		activity.draw(isEndOftheGame);
	}
	
	@Override
	public List<DrawableObject> getFixedObjects() {
		List<DrawableObject> objects = new LinkedList<DrawableObject>();
		objects.addAll(walls);
		return objects;
	}

	@Override
	public List<DrawableObject> getObjectsToDraw() {
		List<DrawableObject> objects = new LinkedList<DrawableObject>();
		objects.addAll(robots.values());
		objects.addAll(obstacles.values());
		objects.addAll(playersAlive.values());
		objects.addAll(bombs.values());
		for (List<ExplosionPart> drawableObjects : explosionParts.values()) {
			objects.addAll(drawableObjects);
		}
		return objects;
	}
}
