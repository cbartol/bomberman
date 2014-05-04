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

import android.app.Activity;
import android.os.Handler;
import android.util.Pair;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class Game {
	private static final char PLAYER = '0';
	private static final char PLAYER1 = '1';
	private static final char PLAYER2 = '2';
	private static final char PLAYER3 = '3';
	private static final char PLAYER_OVER_BOMB = 'a'-1;
	private static final char PLAYER1_OVER_BOMB = 'a';
	private static final char PLAYER2_OVER_BOMB = 'b';
	private static final char PLAYER3_OVER_BOMB = 'c';
	private static final char ROBOT = 'R';
	private static final char BOMB = '$';
	private static final char EXPLOSION = '#';
	private static final char WALL = 'W';
	private static final char OBSTACLE = 'O';
	private static final char EMPTY = '-';
	
	
	
	private MapProperties mapProperties;
	private char[][] map;
	private int width; // number of cells of map's width
	private int height; // number of cells of map's height
	private ImageView mapView;
	
	private List<Bomb> bombs = new LinkedList<Bomb>();
	private List<Robot> robots = new LinkedList<Robot>();
	private List<Obstacle> obstacles = new LinkedList<Obstacle>();
	private SparseArray<Player> players = new SparseArray<Player>();
	
	private Handler mHandler;
	private Runnable moveRobots;


	/******************************************************
	 ******************** Init section *********************
	 ******************************************************/
	public Game(Activity a, RelativeLayout gameArea, MapProperties mapProp){
		this.mapProperties = mapProp;
		setMapBackgroud(a, gameArea);
		readMapFile(a, gameArea, mapProperties.getLevel(), 1);
		Pair<Integer,Integer> p1 = getPosFromPlayer(1);
		players.put(1, new Player(1, a, gameArea, p1.first, p1.second));
		mHandler = new Handler();
		final int robotSpeed = mapProperties.getRobotSpeed();
		moveRobots = new Runnable() {
			@Override
			public void run() {
				moveRobots();
				mHandler.postDelayed(moveRobots, 1000/robotSpeed);
			}
		};
		moveRobots.run();
	}
	
	private void readMapFile(Activity activity, RelativeLayout gameArea, int level, int maxPlayers) {
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
				switch (object) {
				case ROBOT:
					robots.add(new Robot(activity, gameArea, j, i));
					break;

				case OBSTACLE:
					obstacles.add(new Obstacle(activity, gameArea, j, i));
					break;
				case PLAYER2:
					if(maxPlayers >= 2){
						players.put(2, new Player(2, activity, gameArea, j, i));
					} else {
						this.map[i][j] = EMPTY;
					}
					break;
				case PLAYER3:
					if(maxPlayers >= 3){
						players.put(3, new Player(3, activity, gameArea, j, i));
					} else {
						this.map[i][j] = EMPTY;
					}
					break;
				}
			}
		}
	}
	
	private void setMapBackgroud(Activity a, RelativeLayout layout){
		int mapId = a.getResources().getIdentifier("map" + mapProperties.getLevel(), "drawable", a.getPackageName());
		mapView = new ImageView(a);
		RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mapView.setImageDrawable(a.getResources().getDrawable(mapId));
		mapView.setAdjustViewBounds(true);
		mapView.setLayoutParams(vp);
		layout.addView(mapView);
		mapView.setScaleType(ScaleType.CENTER_INSIDE);
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
		if(map[y-1][x] >= PLAYER1 && map[y-1][x] <= PLAYER3){
			playersFound.add(map[y-1][x] - PLAYER);
		} else if(map[y+1][x] >= PLAYER1 && map[y+1][x] <= PLAYER3){
			playersFound.add(map[y+1][x] - PLAYER);
		} else if(map[y][x-1] >= PLAYER1 && map[y][x-1] <= PLAYER3){
			playersFound.add(map[y][x-1] - PLAYER);
		} else if(map[y][x+1] >= PLAYER1 && map[y][x+1] <= PLAYER3){
			playersFound.add(map[y][x+1] - PLAYER);
		}
		return playersFound;
	}
	
	private void killPlayer(int id){
		Player player = getPlayer(id);
		map[player.getY()][player.getX()] = EMPTY;
		player.destroy();
	}
	
	public ImageView getImage(){
		return mapView;
	}
	
	public Player getPlayer(int id){
		return players.get(id);
	}
	
	private Pair<Integer,Integer> getPosFromPlayer(int player){
		for(int y = 0 ; y < height ; y++){
			for(int x = 0 ; x < width ; x++){
				if(map[y][x] == PLAYER + player || map[y][x] == (PLAYER_OVER_BOMB + player)){
					return new Pair<Integer,Integer>(x, y);
				}
			}
		}
		return null;
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
}
