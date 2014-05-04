package ist.meic.bomberman.engine;

import java.util.List;
import java.util.Random;

public enum Direction {
	UP, DOWN, LEFT, RIGHT;
	
	private static Random random = new Random();
	
	public static Direction random(){
		switch (random.nextInt(4)) {
		case 0:
			return UP;
		case 1:
			return DOWN;
		case 2:
			return LEFT;
		case 3:
			return RIGHT;
		}
		return null;
	}
	
	public static Direction random(List<Direction> directions){
		if(directions.size() > 0){
			return directions.get(random.nextInt(directions.size()));
		}
		return UP; // cannot move....
	}
}
