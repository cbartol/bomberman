package ist.meic.bomberman.multiplayer;

import ist.meic.bomberman.engine.Bomb;
import ist.meic.bomberman.engine.ExplosionPart;
import ist.meic.bomberman.engine.Obstacle;
import ist.meic.bomberman.engine.Player;
import ist.meic.bomberman.engine.Robot;
import ist.meic.bomberman.engine.Wall;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.SparseArray;

public class GameState implements Serializable {
	private Map<Integer, Player> playersAlive = Collections
			.synchronizedMap(new HashMap<Integer, Player>());
	private Map<Integer, Player> players = Collections
			.synchronizedMap(new HashMap<Integer, Player>());
	private Map<Integer, Player> pausedPlayers = Collections
			.synchronizedMap(new HashMap<Integer, Player>());
	//private SparseArray<Player> playersToEnterInGame = new SparseArray<Player>();
	private int level;
	private int mapWidth;
	private int mapHeight;
	private int playerId;
	private List<Wall> walls = Collections
			.synchronizedList(new LinkedList<Wall>());
	private List<Bomb> bombs = Collections
			.synchronizedList(new LinkedList<Bomb>());
	private Map<Integer, Robot> robots = Collections
			.synchronizedMap(new HashMap<Integer, Robot>());
	private List<Obstacle> obstacles = Collections
			.synchronizedList(new LinkedList<Obstacle>());
	private Map<Integer, List<ExplosionPart>> explosionParts = Collections
			.synchronizedMap(new HashMap<Integer, List<ExplosionPart>>());

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public Map<Integer, Player> getPlayersAlive() {
		return playersAlive;
	}

	public void setPlayersAlive(Map<Integer, Player> playersAlive) {
		this.playersAlive = playersAlive;
	}

	public Map<Integer, Player> getPlayers() {
		return players;
	}

	public void setPlayers(Map<Integer, Player> players) {
		this.players = players;
	}

	public Map<Integer, Player> getPausedPlayers() {
		return pausedPlayers;
	}

	public void setPausedPlayers(Map<Integer, Player> pausedPlayers) {
		this.pausedPlayers = pausedPlayers;
	}

//	public SparseArray<Player> getPlayersToEnterInGame() {
//		return playersToEnterInGame;
//	}
//
//	public void setPlayersToEnterInGame(SparseArray<Player> playersToEnterInGame) {
//		this.playersToEnterInGame = playersToEnterInGame;
//	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}

	public List<Bomb> getBombs() {
		return bombs;
	}

	public void setBombs(List<Bomb> bombs) {
		this.bombs = bombs;
	}

	public Map<Integer, Robot> getRobots() {
		return robots;
	}

	public void setRobots(Map<Integer, Robot> robots) {
		this.robots = robots;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public void setObstacles(List<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	public Map<Integer, List<ExplosionPart>> getExplosionParts() {
		return explosionParts;
	}

	public void setExplosionParts(
			Map<Integer, List<ExplosionPart>> explosionParts) {
		this.explosionParts = explosionParts;
	}

}
