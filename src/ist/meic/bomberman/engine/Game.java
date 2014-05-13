package ist.meic.bomberman.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.raw;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;
/*
 * This class will be used for multiplayer on the server side.
 * For clients it's necessary to create a 'GameProxy' class that communicates with the server
 */
public class Game {
	// '0' + playerId --> player char on map
	private static final char PLAYER = '0';
	private static final char ROBOT = 'R';
	private static final char EXPLOSION = '#';
	private static final char WALL = 'W';
	private static final char OBSTACLE = 'O';
	private static final char EMPTY = '-';
	
	private int maxPlayers = 1;
	
	private MapProperties mapProperties;
	private char[][] map;
	private int width; // number of cells of map's width
	private int height; // number of cells of map's height
	private int stripeSize = 0;
	private GameMapView gameMap;
	private SurfaceHolder surfaceHolder;
	
	private List<Wall> walls = Collections.synchronizedList(new LinkedList<Wall>());
	private List<Bomb> bombs =  Collections.synchronizedList(new LinkedList<Bomb>());
	private List<Robot> robots =  Collections.synchronizedList(new LinkedList<Robot>());
	private List<Obstacle> obstacles =  Collections.synchronizedList(new LinkedList<Obstacle>());
	private Map<Integer, List<ExplosionPart>> explosionParts =  Collections.synchronizedMap(new TreeMap<Integer, List<ExplosionPart>>());
	private Map<Integer,Player> players =  Collections.synchronizedMap(new TreeMap<Integer,Player>());
	private int explosionIdGenerator = 0;
	
	private Handler mHandler;
	private Runnable moveRobots;


	/******************************************************
	 ******************** Init section *********************
	 ******************************************************/
	public Game(Activity a, GameMapView gameArea, MapProperties mapProp, int maxPlayers){
		this.mapProperties = mapProp;
		this.maxPlayers = maxPlayers;
		gameMap = gameArea;
		surfaceHolder = gameMap.getHolder();
		readMapFile(a, mapProperties.getLevel());
		setMapBackgroud(a);
		mHandler = new Handler();
		final double robotSpeed = mapProperties.getRobotSpeed();
		moveRobots = new Runnable() {
			@Override
			public synchronized void run() {
				synchronized (robots) {
					moveRobots();
				}
				draw();
				mHandler.postDelayed(moveRobots,(long) (1000/robotSpeed));
			}
		};
		mHandler.postDelayed(moveRobots,  (long) (1000/robotSpeed));
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
					walls.add(new Wall(activity, j, i));
				} else if(object == ROBOT){
					robots.add(new Robot(activity, mapProperties.getRobotKilledPoints(), j, i));
				} else if(object == OBSTACLE){
					obstacles.add(new Obstacle(activity, j, i));
				} else if(object > PLAYER && object <= PLAYER + maxPlayers){
					final int playerId = object - PLAYER; 
					players.put(playerId, new Player(playerId, activity, mapProperties.getPlayerKilledPoints(), j, i));
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
		if(getPlayer(id).isDead()) return;
		move(getPlayer(id), direction, (char) (PLAYER + id));
		
		// the player must die if he gets near a robot
		if(robotNear(getPlayer(id).getX(), getPlayer(id).getY())){
			killPlayer(id);
		}
		draw();
	}
	
	public synchronized void moveRobots(){
		List<Robot> robotsCopy = new LinkedList<Robot>();
		synchronized(robots){
			robotsCopy.addAll(robots);
		}
		for (Robot robot : robotsCopy) {
			move(robot, Direction.random(whereToMove(robot.getX(),robot.getY())), ROBOT);
			List<Integer> playerIds = playerNear(robot.getX(), robot.getY());
			for (Integer playerId : playerIds) {
				killPlayer(playerId);
			}
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
			destroy(x, y);
		} else {
			map[y][x] = EMPTY;
			map[yDest][xDest] = entityChar;
		}
		entity.move(direction);
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
	
	private synchronized void killPlayer(int id){
		Player player = getPlayer(id);
		map[player.getY()][player.getX()] = EMPTY;
		player.destroy();
	}
	
	/******************************************************
	 ******************** Bombs section *******************
	 ******************************************************/
	
	public synchronized void dropBomb(Context c, int playerId){
		final Player player = players.get(playerId);
		final int x = player.getX();
		final int y = player.getY();
		final int explosionId = ++explosionIdGenerator;
		final Bomb bomb = new Bomb(c, playerId, explosionId, x, y);
		bombs.add(bomb);
		final Runnable explosionTimer = new Runnable() {
			@Override
			public void run() {
				map[bomb.getY()][bomb.getX()] = EMPTY;
				for (ExplosionPart explosionPart : explosionParts.get(bomb.getExplosionId())) {
					map[explosionPart.getY()][explosionPart.getX()] = EMPTY;
				}
				explosionParts.get(bomb.getExplosionId()).clear();
				explosionParts.remove(bomb.getExplosionId());
				bombs.remove(bomb);
				draw();				
			}
		};
		final Runnable bombTimer = new Runnable() {
			@Override
			public void run() {
				bomb.explode();
				createExplosion(bomb);
				draw();
				mHandler.postDelayed(explosionTimer, (long) (1000*mapProperties.getExplosionDuration()));
			}
		};
		mHandler.postDelayed(bombTimer, (long) (1000*mapProperties.getExplosionTimeout()));
		draw();
	}
	
	private synchronized void createExplosion(final Bomb bomb){
		List<ExplosionPart> parts = new LinkedList<ExplosionPart>();
		
		parts.addAll(createExplosionAux(bomb, 1, 0, Direction.RIGHT));
		parts.addAll(createExplosionAux(bomb, 0, 1, Direction.DOWN));
		parts.addAll(createExplosionAux(bomb, -1, 0, Direction.LEFT));
		parts.addAll(createExplosionAux(bomb, 0, -1, Direction.UP));
		explosionParts.put(bomb.getExplosionId(), parts);
	}
	
	// this function is not bullet proof. It was made to avoid repeating 4 times the same cycle.
	// values for incrX and incrY are 0, 1 or -1. when one of the variables is equal to 1 or -1 the other variable must be 0 and vice-versa. 
	private List<ExplosionPart> createExplosionAux(final Bomb bomb, int incrX, int incrY, Direction direction){
		int startX = bomb.getX();
		int startY = bomb.getY();
		List<ExplosionPart> parts = new LinkedList<ExplosionPart>();
		int x, y;
		
		//note that one of the variables is set to 0 and the other is 1 or -1
		final int range = mapProperties.getExplosionRange() * incrX + mapProperties.getExplosionRange() * incrY; 
		destroy(startX, startY);
		map[startY][startX] = EXPLOSION;
		for(x = incrX, y = incrY; x != range && y != range ; x+= incrX, y+=incrY){
			final char pos = map[startY+y][startX+x];
			if(pos == OBSTACLE || (pos != WALL && map[startY+y+incrY][startX+x+incrX] == WALL)){
				parts.add(new ExplosionPart(gameMap.getContext(), direction, true, startX+x, startY+y));
				destroy(startX+x, startY+y);
				map[startY+y][startX+x] = EXPLOSION;
				break;
			} else if(pos != WALL){
				parts.add(new ExplosionPart(gameMap.getContext(), direction, false, startX+x, startY+y));
				destroy(startX+x, startY+y);
				map[startY+y][startX+x] = EXPLOSION;
			} else {
				break;
			}
		}
		if(x == range || y == range){
			if(map[startY+y][startX+x] != WALL){
				parts.add(new ExplosionPart(gameMap.getContext(), direction, true, startX+x, startY+y));
				destroy(startX+x, startY+y);
				map[startY+y][startX+x] = EXPLOSION;
			}
		}
		return parts;
	}
	
	private synchronized void destroy(int posX, int posY){
		final char object = map[posY][posX];
		if(object == ROBOT){
			synchronized(robots){
				Iterator<Robot> it = robots.iterator();
				while(it.hasNext()){
					Robot robot = it.next();
					if(robot.getX() == posX && robot.getY() == posY){
						robot.destroy(); //increment player score
						it.remove();
						break;
					}
				}
			}
		} else if(object > PLAYER && object <= PLAYER + maxPlayers){
			players.get(object - PLAYER).destroy(); //increment player score
		} else if(object == OBSTACLE) {
			Iterator<Obstacle> it = obstacles.iterator();
			while(it.hasNext()){
				Obstacle obstacle = it.next();
				if(obstacle.getX() == posX && obstacle.getY() == posY){
					it.remove();
					break;
				}
			}
		}
	}
	
	public Player getPlayer(int id){
		return players.get(id);
	}
	
	void startRepeatingTask() {
		moveRobots.run();
	}
	
	void stopRepeatingTask() {
		mHandler.removeCallbacks(moveRobots);
	}
	
	public void endGame(){
		mHandler.removeCallbacks(moveRobots);
	}
	
	private void draw(){
		Canvas canvas = null;
		// try locking the canvas for exclusive pixel editing
		// in the surface
		try {
			canvas = surfaceHolder.lockCanvas();
			synchronized (surfaceHolder) {
				// update game state 
				// render state to the screen
				// draws the canvas on the panel
				//this.gameMap.onDraw(canvas);
				this.gameMap.postInvalidate();
			}
		} finally {
			// in case of an exception the surface is not left in 
			// an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}	// end finally
	}
	
	// TODO: to optimize this function the game can store and update this list
	public List<DrawableObject> getObjectsToDraw(){
		List<DrawableObject> objects = new LinkedList<DrawableObject>();
		objects.addAll(robots);
		objects.addAll(obstacles);
		objects.addAll(players.values());
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
}
