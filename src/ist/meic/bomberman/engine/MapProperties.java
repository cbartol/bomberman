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
	private double gameDuration;
	private double explosionTimeout;
	private double explosionDuration;
	private int explosionRange;
	private double robotSpeed;
	private double robotKilledPoints;
	private double playerKilledPoints;
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
			gameDuration = Double.parseDouble(mapProperties.getProperty("game.duration"));
			explosionTimeout = Double.parseDouble(mapProperties.getProperty("explosion.timeout"));
			explosionDuration = Double.parseDouble(mapProperties.getProperty("explosion.duration"));
			explosionRange = Integer.parseInt(mapProperties.getProperty("explosion.range"));
			robotSpeed = Double.parseDouble(mapProperties.getProperty("robot.speed"));
			robotKilledPoints = Double.parseDouble(mapProperties.getProperty("robot.killed.points"));
			playerKilledPoints = Double.parseDouble(mapProperties.getProperty("opponent.killed.points"));
			
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
	public double getGameDuration() {
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
	public double getRobotKilledPoints() {
		return robotKilledPoints;
	}
	public double getPlayerKilledPoints() {
		return playerKilledPoints;
	}
	public int getLevel(){
		return level;
	}
}
