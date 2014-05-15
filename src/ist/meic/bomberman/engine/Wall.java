package ist.meic.bomberman.engine;

import java.io.Serializable;

import ist.meic.bomberman.R;
import android.content.Context;

public class Wall extends DrawableObject implements Serializable{
	public Wall(Context c, int x, int y, char type) {
		super(c, R.drawable.wall, x, y, type);
	}
}
