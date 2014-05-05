package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.raw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.SurfaceHolder;

public class Game {
	// '0' + playerId --> player char on map
	private static final char PLAYER = '0';
	
	// using letters from 'a' to 'z'  ('a' for player1, 'b' for player2, etc...) 
	private static final char PLAYER_OVER_BOMB = 'a'-1;

	private static final char ROBOT = 'R';
	private static final char BOMB = '$';
	private static final char EXPLOSION = '#';
	private static final char WALL = 'W';
	private static final char OBSTACLE = 'O';
	private static final char EMPTY = '-';
	
	private int maxPlayers = 1;
	
	private MapProperties mapProperties;
	private char[][] map;
	private int width; // number of cells of map's width
	private int height; // number of cells of map's height
	private GameMapView gameMap;
	private SurfaceHolder surfaceHolder;
	
	private List<Bomb> bombs = new LinkedList<Bomb>();
	private List<Robot> robots = new LinkedList<Robot>();
	private List<Obstacle> obstacles = new LinkedList<Obstacle>();
	private Map<Integer,Player> players = new TreeMap<Integer,Player>();
	
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
		setMapBackgroud(a);
		readMapFile(a, mapProperties.getLevel());
		mHandler = new Handler();
		final int robotSpeed = mapProperties.getRobotSpeed();
		moveRobots = new Runnable() {
			@Override
			public void run() {
				mHandler.postDelayed(moveRobots, 1000/robotSpeed);
				moveRobots();
				draw();
			}
		};
		moveRobots.run();
	}
	
	private void readMapFile(Activity activity, int level) {
		String levelName = "level" + level + "_map";
		
		Class<raw> c = R.raw.class;
		Field f = null;
		try {
			f = c.getField(levelName);
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int levelConfig = 0;
		try {
			levelConfig = f.getInt(f);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
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
				if(object == ROBOT){
					robots.add(new Robot(activity, j, i));
				} else if(object == OBSTACLE){
					obstacles.add(new Obstacle(activity, j, i));
				} else if(object > PLAYER && object <= PLAYER + maxPlayers){
					final int playerId = object - PLAYER; 
					players.put(playerId, new Player(playerId, activity, j, i));
				} else if(object > PLAYER + maxPlayers && object <= PLAYER + 9) {
					this.map[i][j] = EMPTY; // we have to delete players on map that are not playing.
				}
			}
		}
	}
	
	private void setMapBackgroud(Activity a){
		int mapId = a.getResources().getIdentifier("map" + mapProperties.getLevel(), "drawable", a.getPackageName());
		this.gameMap.setGame(this);
		this.gameMap.setMap(mapId);
	}
	
	
	/******************************************************
	 ****************** Movement section ******************
	 ******************************************************/
	public void movePlayer(int id, Direction direction){
		if(getPlayer(id).isDead()) return;
		move(getPlayer(id), direction, (char) (PLAYER + id));
		
		// the player must die if he gets near a robot
		if(robotNear(getPlayer(id).getX(), getPlayer(id).getY())){
			killPlayer(id);
		}
		draw();
	}
	
	public void moveRobots(){
		for (Robot robot : robots) {
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
		return map[y][x] == EMPTY;
	}

	private void move(Entity entity, Direction direction, char entityChar){
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
		map[y][x] = EMPTY;
		map[yDest][xDest] = entityChar;
		entity.move(direction);
	}
	
	private boolean robotNear(int xDest, int yDest) {
		return map[yDest-1][xDest] == ROBOT || map[yDest+1][xDest] == ROBOT || map[yDest][xDest-1] == ROBOT || map[yDest][xDest+1] == ROBOT;
	}
	
	private List<Integer> playerNear(int x, int y) {
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
	
	private void killPlayer(int id){
		Player player = getPlayer(id);
		map[player.getY()][player.getX()] = EMPTY;
		player.destroy();
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
		return objects;
	}
	
	public int getGameWidth(){
		return width;
	}
	public int getGameHeight(){
		return height;
	}
}
