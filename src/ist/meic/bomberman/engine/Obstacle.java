package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;
import android.widget.RelativeLayout;

public class Obstacle extends DrawableObject {

	public Obstacle(Context c, RelativeLayout gameArea,
			int x, int y) {
		super(c, gameArea, R.drawable.obstacle, x, y);
	}



}
