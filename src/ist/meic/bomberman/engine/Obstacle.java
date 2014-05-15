package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Obstacle extends DrawableObject {
	private int id;
	public Obstacle(Context c, int x, int y, char type, int id) {
		super(c, R.drawable.obstacle, x, y, type);
		this.id = id;
	}
	public int getId(){
		return id;
	}
}
