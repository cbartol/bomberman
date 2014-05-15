package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Obstacle extends DrawableObject {

	public Obstacle(Context c, int x, int y, char type) {
		super(c, R.drawable.obstacle, x, y, type);
	}

}
