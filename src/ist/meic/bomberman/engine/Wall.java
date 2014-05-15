package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Wall extends DrawableObject {
	public Wall(Context c, int x, int y, char type) {
		super(c, R.drawable.wall, x, y, type);
	}
}
