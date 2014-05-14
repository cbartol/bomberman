package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Robot extends Entity {
	private int id;
	
	public Robot(Context c, int id, int dyingPoints, int x, int y) {
		super(c, R.drawable.robot, dyingPoints, x, y);
		this.id = id;
	}

	public int getId(){
		return id;
	}
}
