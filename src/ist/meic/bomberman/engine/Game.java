package ist.meic.bomberman.engine;

import ist.meic.bomberman.GameActivity;
import ist.meic.bomberman.R;
import ist.meic.bomberman.R.raw;
import ist.meic.bomberman.multiplayer.GameState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
/*
 * This class will be used for multiplayer on the server side.
 * For clients it's necessary to create a 'GameProxy' class that communicates with the server
 */
@SuppressLint("UseSparseArrays") public class Game extends Thread implements IGame{
	
	// '0' + playerId --> player char on map
	public static final char PLAYER = '0';
	public static final char ROBOT = 'R';
	public static final char EXPLOSION = '#';
	public static final char WALL = 'W';
	public static final char OBSTACLE = 'O';
	public static final char EMPTY = '-';
	public static final char BOMB = 'B';
	
	private int maxPlayers = 1;
	
	private MapProperties mapProperties;
	private char[][] map;
	private int width; // number of cells of map's width
	private int height; // number of cells of map's height
	private int stripeSize = 0;
	private GameMapView gameMap;
	private GameActivity activity;
	private int timeLeft;
	private boolean isRunning = false;
	
	private List<Wall> walls = Collections.synchronizedList(new LinkedList<Wall>());
	private List<Bomb> bombs =  Collections.synchronizedList(new LinkedList<Bomb>());
	private Map<Integer,Robot> robots =  Collections.synchronizedMap(new HashMap<Integer,Robot>());
	private List<Obstacle> obstacles =  Collections.synchronizedList(new LinkedList<Obstacle>());
	private Map<Integer, List<ExplosionPart>> explosionParts =  Collections.synchronizedMap(new HashMap<Integer, List<ExplosionPart>>());
	private Map<Integer,Player> playersAlive =  Collections.synchronizedMap(new HashMap<Integer,Player>());
	private Map<Integer,Player> players =  Collections.synchronizedMap(new HashMap<Integer,Player>());
	private Map<Integer,Player> pausedPlayers = Collections.synchronizedMap(new HashMap<Integer,Player>());
	private SparseArray<Player> playersToEnterInGame = new SparseArray<Player>(); 
	private int explosionIdGenerator = 0;
	private int robotIdGenerator = 0;
	private int obstacleIdGenerator = 0;
	
	private Handler mHandler;
	private Runnable moveRobots;
	private Runnable timePassing;
	
	/******************************************************
	 ******************** Init section *********************
	 ******************************************************/
	public Game(final GameActivity a, GameMapView gameArea, MapProperties mapProp, int maxPlayers){
		this.activity = a;
		this.mapProperties = mapProp;
		this.maxPlayers = maxPlayers;
		this.timeLeft = mapProperties.getGameDuration();
		gameMap = gameArea;
		readMapFile(a, mapProperties.getLevel());
		setMapBackgroud(a);
		mHandler = new Handler();
		final double robotSpeed = mapProperties.getRobotSpeed();
		moveRobots = new Runnable() {
			@Override
			public synchronized void run() {
				if(timeLeft <= 0 || !isRunning){
					return;
				}
				mHandler.postDelayed(moveRobots,(long) (1000/robotSpeed));
				synchronized (robots) {
					moveRobots();
				}
				a.draw(isTheEndOfTheGame());
			}
		};
		timePassing = new Runnable() {
			@Override
			public void run() {
				if(timeLeft > 0 && isRunning){
					activity.changeTime(--timeLeft);
					mHandler.postDelayed(timePassing, 1000);
				} else {
					//end game
					a.draw(isTheEndOfTheGame());
				}
			}
		};
	}
	
	@Override
	public void run() {
		mHandler.postDelayed(timePassing, 1000);
		mHandler.postDelayed(moveRobots,  (long) (1000/mapProperties.getRobotSpeed()));
		isRunning = true;
		synchronized (this) {
			while(isRunning){
				Log.i("Game Thread", "weeeeeeeeeeeeeeee");
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		Log.i("Game Thread", "finito!!!!");
	}
	
	private void readMapFile(Activity activity, int level) {
		String levelName = "level" + level + "_map";
		
		Class<raw> c = R.raw.class;
		Field f = null;
		try {
			f = c.getField(levelName);
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		}
		int levelConfig = 0;
		try {
			levelConfig = f.getInt(f);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(levelConfig)));
		ArrayList<String> rawMap = new ArrayList<String>();
		
		String line;
		try {
			while((line = in.readLine()) != null){
				rawMap.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.width = rawMap.get(0).length();
		this.height = rawMap.size();
		this.map = new char[height][width];
		
		for(int i = 0 ; i < height ; i++){
			for(int j = 0 ; j < width ; j++){
				char object = rawMap.get(i).charAt(j);
				this.map[i][j] = object;
				if(object == WALL){
					walls.add(new Wall(activity, j, i, Game.WALL));
				} else if(object == ROBOT){
					final int robotId = robotIdGenerator++;
					robots.put(robotId, new Robot(activity, robotId, mapProperties.getRobotKilledPoints(), j, i, Game.ROBOT));
				} else if(object == OBSTACLE){
					obstacles.add(new Obstacle(activity, j, i, Game.OBSTACLE, obstacleIdGenerator++));
				} else if(object > PLAYER && object <= PLAYER + maxPlayers){
					final int playerId = object - PLAYER;
					final Player p = new Player(playerId, activity, mapProperties.getPlayerKilledPoints(), j, i, Game.PLAYER);
					players.put(playerId, p);
					if(object > PLAYER +1){ // only for player2, 3, etc...
						this.map[i][j] = EMPTY; // the player is not ready to join the game
						playersToEnterInGame.put(playerId, p);
					} else {
						playersAlive.put(playerId, p);
					}
				} else if(object > PLAYER + maxPlayers && object <= PLAYER + 9) {
					this.map[i][j] = EMPTY; // we have to delete players on map that are not playing.
				}
			}
		}
		stripeSize = walls.get(0).getImage().getWidth();
	}
	
	private void setMapBackgroud(Activity a){
		this.gameMap.setGame(this, width*stripeSize, height*stripeSize);
	}
	
	
	/******************************************************
	 ****************** Movement section ******************
	 ******************************************************/
	public synchronized void movePlayer(int id, Direction direction){
		if(getPlayer(id).isDead() || timeLeft <= 0) return;
		move(getPlayer(id), direction, (char) (PLAYER + id));
		
		// the player must die if he gets near a robot
		if(robotNear(getPlayer(id).getX(), getPlayer(id).getY())){
			killPlayer(id);
		}
		activity.draw(isTheEndOfTheGame());
	}
	
	public synchronized void moveRobots(){
		List<Robot> robotsCopy = new LinkedList<Robot>();
		synchronized(robots){
			robotsCopy.addAll(robots.values());
		}
		for (Robot robot : robotsCopy) {
			move(robot, Direction.random(whereToMove(robot.getX(),robot.getY())), ROBOT);
			List<Integer> playerIds = playerNear(robot.getX(), robot.getY());
			for (Integer playerId : playerIds) {
				killPlayer(playerId);
			}
			moveRobot(robot.getId());
		}
	}
	
	private List<Direction> whereToMove(int x, int y){
		ArrayList<Direction> directions = new ArrayList<Direction>();
		if(canMove(x,y-1))	directions.add(Direction.UP);
		if(canMove(x,y+1))	directions.add(Direction.DOWN);
		if(canMove(x-1,y))	directions.add(Direction.LEFT);
		if(canMove(x+1,y))	directions.add(Direction.RIGHT);
		return directions;
	}
	
	private boolean canMove(int x, int y){
		return map[y][x] == EMPTY || map[y][x] == EXPLOSION;
	}

	private synchronized void move(Entity entity, Direction direction, char entityChar){
		int x = entity.getX();
		int y = entity.getY();
		int xDest = entity.getX();
		int yDest = entity.getY();
		switch (direction) {
		case UP:
			if(!canMove(xDest,--yDest))	return;
			break;
		case DOWN:
			if(!canMove(xDest,++yDest))	return;
			break;
		case LEFT:
			if(!canMove(--xDest,yDest))	return;
			break;
		case RIGHT:
			if(!canMove(++xDest,yDest))	return;
			break;
		}
		if(map[yDest][xDest] == EXPLOSION){
			Player p = getPlayerOwnerOfTheExplosion(xDest, yDest);
			if(p != null){
				p.addScore(destroy(x, y));
				activity.changeScore(p);
			}
		} else {
			map[yDest][xDest] = entityChar;
		}
		map[y][x] = EMPTY;
		entity.move(direction);
	}
	
	private Player getPlayerOwnerOfTheExplosion(int xDest, int yDest) {
		synchronized (explosionParts) {
			for (List<ExplosionPart> explosion : explosionParts.values()) {
				for (ExplosionPart explosionPart : explosion) {
					if(explosionPart.getX() == xDest && explosionPart.getY() == yDest){
						return players.get(explosionPart.getPlayerOwner());
					}
				}
			}
		}
		return null;
	}

	private synchronized boolean robotNear(int xDest, int yDest) {
		return map[yDest-1][xDest] == ROBOT || map[yDest+1][xDest] == ROBOT || map[yDest][xDest-1] == ROBOT || map[yDest][xDest+1] == ROBOT;
	}
	
	private synchronized List<Integer> playerNear(int x, int y) {
		LinkedList<Integer> playersFound = new LinkedList<Integer>();
		final char maxPlayer = (char) (PLAYER + maxPlayers);
		if(map[y-1][x] > PLAYER && map[y-1][x] <= maxPlayer){
			playersFound.add(map[y-1][x] - PLAYER);
		} else if(map[y+1][x] > PLAYER && map[y+1][x] <= maxPlayer){
			playersFound.add(map[y+1][x] - PLAYER);
		} else if(map[y][x-1] > PLAYER && map[y][x-1] <= maxPlayer){
			playersFound.add(map[y][x-1] - PLAYER);
		} else if(map[y][x+1] > PLAYER && map[y][x+1] <= maxPlayer){
			playersFound.add(map[y][x+1] - PLAYER);
		}
		return playersFound;
	}
	
	protected synchronized void killPlayer(int id){
		Player player = getPlayer(id);
		map[player.getY()][player.getX()] = EMPTY;
		player.destroy();
		destroyObject(PLAYER, id);
		playersAlive.remove(id);
	}
	
	/******************************************************
	 ******************** Bombs section *******************
	 ******************************************************/
	
	public synchronized void dropBomb(int playerId){
		final Player player = players.get(playerId);
		if(player.isDead() || timeLeft <= 0 || !player.canDropBomb()){
			return;
		}
		player.canDropBomb(false);
		final int x = player.getX();
		final int y = player.getY();
		final int explosionId = ++explosionIdGenerator;
		final Bomb bomb = new Bomb(activity, playerId, explosionId, x, y, Game.BOMB);
		bombs.add(bomb);
		final Runnable explosionTimer = new Runnable() {
			@Override
			public void run() {
				if(timeLeft <= 0  || !isRunning){
					return;
				}
				map[bomb.getY()][bomb.getX()] = EMPTY;
				for (ExplosionPart explosionPart : explosionParts.get(bomb.getExplosionId())) {
					map[explosionPart.getY()][explosionPart.getX()] = EMPTY;
				}
				synchronized (explosionParts) {
					explosionParts.get(bomb.getExplosionId()).clear();
					explosionParts.remove(bomb.getExplosionId());
				}
				destroyObject(BOMB, bomb.getExplosionId());
				destroyExplosion(bomb.getExplosionId());
				bombs.remove(bomb);
				players.get(bomb.getPlayerId()).canDropBomb(true);
				activity.draw(isTheEndOfTheGame());				
			}
		};
		final Runnable bombTimer = new Runnable() {
			@Override
			public void run() {
				if(timeLeft <= 0  || !isRunning){
					return;
				}
				bomb.explode();
				createExplosion(bomb, player);
				mHandler.postDelayed(explosionTimer, (long) (1000*mapProperties.getExplosionDuration()));
				activity.draw(isTheEndOfTheGame());
			}
		};
		mHandler.postDelayed(bombTimer, (long) (1000*mapProperties.getExplosionTimeout()));
		activity.draw(isTheEndOfTheGame());
	}
	
	private synchronized void createExplosion(final Bomb bomb, Player explosionOwner){
		List<ExplosionPart> parts = new LinkedList<ExplosionPart>();
		
		parts.addAll(createExplosionAux(bomb, 1, 0, Direction.RIGHT, explosionOwner));
		parts.addAll(createExplosionAux(bomb, 0, 1, Direction.DOWN, explosionOwner));
		parts.addAll(createExplosionAux(bomb, -1, 0, Direction.LEFT, explosionOwner));
		parts.addAll(createExplosionAux(bomb, 0, -1, Direction.UP, explosionOwner));
		explosionParts.put(bomb.getExplosionId(), parts);
		sendExplosion(parts);
		activity.changeScore(explosionOwner);
	}
	
	// this function is not bullet proof. It was made to avoid repeating 4 times the same cycle.
	// values for incrX and incrY are 0, 1 or -1. when one of the variables is equal to 1 or -1 the other variable must be 0 and vice-versa. 
	private List<ExplosionPart> createExplosionAux(final Bomb bomb, int incrX, int incrY, Direction direction, Player explosionOwner){
		int startX = bomb.getX();
		int startY = bomb.getY();
		List<ExplosionPart> parts = new LinkedList<ExplosionPart>();
		int x, y;
		
		//note that one of the variables is set to 0 and the other is 1 or -1
		final int range = mapProperties.getExplosionRange() * incrX + mapProperties.getExplosionRange() * incrY; 
		explosionOwner.addScore(destroy(startX, startY));
		map[startY][startX] = EXPLOSION;
		for(x = incrX, y = incrY; x != range && y != range ; x+= incrX, y+=incrY){
			final char pos = map[startY+y][startX+x];
			if(pos == OBSTACLE || (pos != WALL && map[startY+y+incrY][startX+x+incrX] == WALL)){
				parts.add(new ExplosionPart(gameMap.getContext(), bomb.getPlayerId(), direction, true, startX+x, startY+y, Game.EXPLOSION, bomb.getExplosionId()));
				explosionOwner.addScore(destroy(startX+x, startY+y));
				map[startY+y][startX+x] = EXPLOSION;
				break;
			} else if(pos != WALL){
				parts.add(new ExplosionPart(gameMap.getContext(), bomb.getPlayerId(), direction, false, startX+x, startY+y, Game.EXPLOSION, bomb.getExplosionId()));
				explosionOwner.addScore(destroy(startX+x, startY+y));
				map[startY+y][startX+x] = EXPLOSION;
			} else {
				break;
			}
		}
		if(x == range || y == range){
			if(map[startY+y][startX+x] != WALL){
				parts.add(new ExplosionPart(gameMap.getContext(), bomb.getPlayerId(), direction, true, startX+x, startY+y, Game.EXPLOSION, bomb.getExplosionId()));
				explosionOwner.addScore(destroy(startX+x, startY+y));
				map[startY+y][startX+x] = EXPLOSION;
			}
		}
		return parts;
	}
	
	private synchronized Entity destroy(int posX, int posY){
		Entity entityDestroyed = null;
		final char object = map[posY][posX];
		if(object == ROBOT){
			synchronized(robots){
				Iterator<Robot> it = robots.values().iterator();
				while(it.hasNext()){
					Robot robot = it.next();
					if(robot.getX() == posX && robot.getY() == posY){
						robot.destroy(); //increment player score
						destroyObject(ROBOT, robot.getId());
						entityDestroyed = robot;
						it.remove();
						break;
					}
				}
			}
		} else if(object > PLAYER && object <= PLAYER + maxPlayers){
			final Player player = players.get(object - PLAYER);
			playersAlive.remove(player.getId());
			destroyObject(PLAYER, player.getId());
			player.destroy(); //increment player score
			entityDestroyed = player; 
		} else if(object == OBSTACLE) {
			Iterator<Obstacle> it = obstacles.iterator();
			while(it.hasNext()){
				Obstacle obstacle = it.next();
				if(obstacle.getX() == posX && obstacle.getY() == posY){
					destroyObject(OBSTACLE, /*put obstacleId here*/ 0);
					it.remove();
					break;
				}
			}
		}
		return entityDestroyed;
	}
	
	public Player getPlayer(int id){
		return players.get(id);
	}
	
	public void endGame(){
		if(isRunning){
			isRunning = false;
			mHandler.removeCallbacks(null);
			synchronized (this) {
				this.notifyAll();
			}
		}
	}
	
	// TODO: to optimize this function the game can store and update this list
	public List<DrawableObject> getObjectsToDraw(){
		List<DrawableObject> objects = new LinkedList<DrawableObject>();
		objects.addAll(robots.values());
		objects.addAll(obstacles);
		objects.addAll(playersAlive.values());
		objects.addAll(bombs);
		for (List<ExplosionPart> drawableObjects : explosionParts.values()) {
			objects.addAll(drawableObjects);
		}
		return objects;
	}
	
	public List<DrawableObject> getFixedObjects(){
		List<DrawableObject> objects = new LinkedList<DrawableObject>();
		objects.addAll(walls);
		return objects;
	}
	
	public int getGameWidth(){
		return width;
	}
	public int getGameHeight(){
		return height;
	}
	
	protected boolean isTheEndOfTheGame(){
		return timeLeft <= 0 || (robots.size() == 0 && playersAlive.size() <= 1) || playersAlive.size() == 0;
	}
	
	public boolean isPlayerWinner(int id){
		return playersAlive.get(id) != null;
	}
	
	public void pausePlayer(int playerId){
		Player p = playersAlive.get(playerId);
		map[p.getY()][p.getX()] = EMPTY;
		pausedPlayers.put(playerId, p);
	}
	
	public void resumePlayer(final int playerId){
		final Player p = pausedPlayers.get(playerId);
		checkIfPlayerCanPlay(p);
		pausedPlayers.remove(playerId);
		//activity.draw(isTheEndOfTheGame()); // it's not mandatory.
	}
	
	// This is used when the game resumes the game or a client joins a game that already was started
	private void checkIfPlayerCanPlay(final Player p){
		final char object = map[p.getY()][p.getX()];
		if(object == EMPTY && !robotNear(p.getX(), p.getY())){
			map[p.getY()][p.getX()] = (char) (PLAYER + p.getId());
		} else {
			playersAlive.remove(p.getId());
			p.destroy();
			if(object == EXPLOSION){
				Player player = getPlayerOwnerOfTheExplosion(p.getX(), p.getY());
				if(player != null){
					player.addScore(p);
				}
			} else if(object > PLAYER && object <= PLAYER + maxPlayers){ // when there is a player on the same position 
				players.get(object - PLAYER).addScore(p);
			}

		}
	}
	
	public int getPlayerScore(int playerId){
		return players.get(playerId).getScore();
	}
	
	public int addIncomingPlayer() {
		for (int i = 0; i < playersToEnterInGame.size(); i++) {
			Player p = playersToEnterInGame.get(i);
			if (p != null) {
				playersToEnterInGame.delete(i);
				playersAlive.put(i, p);
				checkIfPlayerCanPlay(p);
				return i;
			}
		}
		return 0;		
	}

	public GameState getGameState(){
		GameState result = new GameState();
		result.setPlayersAlive(playersAlive);
		result.setPlayers(players);
		result.setPausedPlayers(pausedPlayers);
		result.setPlayersToEnterInGame(playersToEnterInGame);
		result.setLevel(mapProperties.getLevel());
		result.setMapWidth(width);
		result.setMapHeight(height);
		result.setWalls(walls);
		result.setBombs(bombs);
		result.setRobots(robots);
		result.setObstacles(obstacles);
		result.setExplosionParts(explosionParts);
		return result;
	}
	
	public Map<Integer, Robot> getRobots() {
		return robots;
	}

	public void setRobots(Map<Integer, Robot> robots) {
		this.robots = robots;
	}
	protected void destroyObject(char type, int id){
		// .....
	}
	protected void destroyExplosion(int explosionId){
		// .....
	}
	
	protected void moveRobot(int id){
		// .....
	}
	
	protected void sendExplosion(List<ExplosionPart> parts){
	}
	
	protected void sendBomb(Bomb bomb){
	}
}
