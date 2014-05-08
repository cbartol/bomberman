package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class ExplosionPart extends DrawableObject {

	public ExplosionPart(Context c, Direction direction, boolean end, int x, int y) {
		super(c, calculateImageId(direction, end), x, y);
	}

	private static int calculateImageId(Direction direction, boolean end){
		if(end){
			switch (direction) {
			case UP:
				return R.drawable.explosion_end_top;
			case DOWN:
				return R.drawable.explosion_end_bottom;
			case LEFT:
				return R.drawable.explosion_end_left;
			case RIGHT:
				return R.drawable.explosion_end_right;
			}
		} else {
			if(direction == Direction.UP || direction == Direction.DOWN){
				return R.drawable.explosion_vertical;
			} else {
				return R.drawable.explosion_horizontal;
			}
		}
		return 0;
	}
}
