package ist.meic.bomberman.engine;

import java.io.Serializable;

import ist.meic.bomberman.R;
import android.content.Context;

public class Robot extends Entity implements Serializable {
	private int id;
	
	public Robot(Context c, int id, int dyingPoints, int x, int y, char type) {
		super(c, R.drawable.robot, dyingPoints, x, y, type);
		this.id = id;
	}

	public int getId(){
		return id;
	}
}
