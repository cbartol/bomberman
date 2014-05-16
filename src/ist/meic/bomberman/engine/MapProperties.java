package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

import android.app.Activity;

public class MapProperties implements Serializable{
	
	private String name;
	private int gameDuration;
	private double explosionTimeout;
	private double explosionDuration;
	private int explosionRange;
	private double robotSpeed;
	private int robotKilledPoints;
	private int playerKilledPoints;
	private int level;
	private int maxPlayers;
	
	public MapProperties(Activity activity, int level) {
		readPropertiesFile(activity, level);
		this.level = level;
	}
	
	private void readPropertiesFile(Activity activity, int level) {
		String levelName = "level" + level + "_config";
		
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
		
		InputStream stream = activity.getResources().openRawResource(levelConfig);
		Properties mapProperties = new Properties();
		try {
			mapProperties.load(stream);
			name = mapProperties.getProperty("level.name");
			gameDuration = Integer.parseInt(mapProperties.getProperty("game.duration"));
			explosionTimeout = Double.parseDouble(mapProperties.getProperty("explosion.timeout"));
			explosionDuration = Double.parseDouble(mapProperties.getProperty("explosion.duration"));
			explosionRange = Integer.parseInt(mapProperties.getProperty("explosion.range"));
			robotSpeed = Double.parseDouble(mapProperties.getProperty("robot.speed"));
			robotKilledPoints = Integer.parseInt(mapProperties.getProperty("robot.killed.points"));
			playerKilledPoints = Integer.parseInt(mapProperties.getProperty("opponent.killed.points"));
			maxPlayers = Integer.parseInt(mapProperties.getProperty("max.players"));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
	}

	public String getName() {
		return name;
	}
	public int getGameDuration() {
		return gameDuration;
	}
	public double getExplosionTimeout() {
		return explosionTimeout;
	}
	public double getExplosionDuration() {
		return explosionDuration;
	}
	public int getExplosionRange() {
		return explosionRange;
	}
	public double getRobotSpeed() {
		return robotSpeed;
	}
	public int getRobotKilledPoints() {
		return robotKilledPoints;
	}
	public int getPlayerKilledPoints() {
		return playerKilledPoints;
	}
	public int getLevel(){
		return level;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}
}
