package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.raw;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import android.app.Activity;

public class MapProperties {
	
	private String name;
	private long gameDuration;
	private int explosionTimeout;
	private int explosionDuration;
	private int explosionRange;
	private int robotSpeed;
	private int robotKilledPoints;
	private int playerKilledPoints;
	private int level;
	
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
			gameDuration = Long.parseLong(mapProperties.getProperty("game.duration"));
			explosionTimeout = Integer.parseInt(mapProperties.getProperty("explosion.timeout"));
			explosionDuration = Integer.parseInt(mapProperties.getProperty("explosion.duration"));
			explosionRange = Integer.parseInt(mapProperties.getProperty("explosion.range"));
			robotSpeed = Integer.parseInt(mapProperties.getProperty("robot.speed"));
			robotKilledPoints = Integer.parseInt(mapProperties.getProperty("robot.killed.points"));
			playerKilledPoints = Integer.parseInt(mapProperties.getProperty("opponent.killed.points"));
			
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
	public long getGameDuration() {
		return gameDuration;
	}
	public int getExplosionTimeout() {
		return explosionTimeout;
	}
	public int getExplosionDuration() {
		return explosionDuration;
	}
	public int getExplosionRange() {
		return explosionRange;
	}
	public int getRobotSpeed() {
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
}
