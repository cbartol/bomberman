package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Robot extends Entity {

	public Robot(Context c, double dyingPoints, int x, int y) {
		super(c, R.drawable.robot, dyingPoints, x, y);
	}

}
