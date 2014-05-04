package ist.meic.bomberman.engine;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class Entity extends DrawableObject {

	public Entity(Context c, RelativeLayout gameArea, int imageResId,
		 int x, int y) {
		super(c, gameArea, imageResId, x, y);
	}

	public void move(Direction direction) {
		switch (direction) {
		case UP:
			setY(getY()-1);
			break;
		case DOWN:
			setY(getY()+1);
			break;
		case LEFT:
			setX(getX()-1);
			break;
		case RIGHT:
			setX(getX()+1);
			break;
		}
	}
}
