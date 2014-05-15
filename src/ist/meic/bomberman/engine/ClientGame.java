package ist.meic.bomberman.engine;

import ist.meic.bomberman.GameActivity;
import ist.meic.bomberman.MultiplayerClientGameActivity;
import ist.meic.bomberman.multiplayer.GameState;
import ist.meic.bomberman.wifi.ClientReceiveActionsThread;
import ist.meic.bomberman.wifi.PlayerAction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClientGame implements IGame {
	private ClientReceiveActionsThread receiveThread;
	private Socket sendSocket;
	private GameActivity activity;
	
	private List<Wall> walls;
	private Map<Integer, Obstacle> obstacles; 
	private List<Obstacle> obstaclesOld; // passar para mapa 
	private Map<Integer, Player> players;
	private Map<Integer, Player> playersAlive;
	private Map<Integer, Robot> robots;
	private Map<Integer, Bomb> bombs;
	private List<Bomb> bombsOld; // passar para mapa
	private Map<Integer, List<ExplosionPart>> explosionParts;
	private int width;
	private int height;
	private int playerId;
	
	private DataOutputStream out;
	private DataInputStream in;
	
	public ClientGame(MultiplayerClientGameActivity activity, GameMapView gameArea, String server, int port) throws UnknownHostException, IOException, ClassNotFoundException{
		this.activity = activity;
		sendSocket = new Socket(server,port);
		DataOutputStream outToServer = new DataOutputStream(sendSocket.getOutputStream());
		outToServer.writeBoolean(false);
		out = outToServer;
		in = new DataInputStream(sendSocket.getInputStream());

		ObjectInputStream fetchState = new ObjectInputStream(sendSocket.getInputStream());
		GameState state = (GameState) fetchState.readObject();
		walls = state.getWalls();
		obstaclesOld = state.getObstacles();
		players = state.getPlayers();
		playersAlive = state.getPlayersAlive();
		robots = state.getRobots();
		bombsOld = state.getBombs();
		explosionParts = state.getExplosionParts();
		
		width = state.getMapWidth();
		height = state.getMapHeight();
		
		activity.setPlayerId(state.getPlayerId());
		int stripeSize = walls.get(0).getImage().getWidth();
		gameArea.setGame(this, width*stripeSize, height*stripeSize);
		//TODO: read from the server all the state of the game (I need this before start the thread)
		
			
		Socket receiveSocket = new Socket(server,port);
		outToServer = new DataOutputStream(receiveSocket.getOutputStream());
		outToServer.writeBoolean(true);
		receiveThread = new ClientReceiveActionsThread(this, receiveSocket);
			
	}
	
	@Override
	public void start() {
		// it does not need to be a thread
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
		// TODO Auto-generated method stub

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

	public void changeObject(DrawableObject object) {
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
			case Game.OBSTACLE:
				Obstacle obstacle = (Obstacle) object;
				//players.put(obstacle.getId(), obstacle);
				break;
			case Game.EXPLOSION:
				break;
			default:
				// ????
				break;
		}
		// change the list
		boolean isEndOftheGame = false;
		activity.draw(isEndOftheGame);
	}

	public void removeObject(char type, int id) {
		switch (type) {
			case Game.PLAYER:
				players.remove(id);
				break;
			case Game.ROBOT:
				robots.remove(id);
				break;
			case Game.OBSTACLE:
				//players.remove(id);
				break;
			case Game.EXPLOSION:
				
				break;
			default:
				// ????
				break;
		}
		// change the list
		boolean isEndOftheGame = false;
		activity.draw(isEndOftheGame);
	}
	
	public void addExplosions(int explosionId, List<ExplosionPart> expParts){
		explosionParts.put(explosionId, expParts);
	}

	public void removeExplosions(int explosionId){
		explosionParts.remove(explosionId);
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
		objects.addAll(obstaclesOld);
		objects.addAll(playersAlive.values());
		objects.addAll(bombsOld);
		for (List<ExplosionPart> drawableObjects : explosionParts.values()) {
			objects.addAll(drawableObjects);
		}
		return objects;
	}
}
