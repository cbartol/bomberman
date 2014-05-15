package ist.meic.bomberman.engine;

import java.io.Serializable;

import ist.meic.bomberman.R;
import android.content.Context;

public class ExplosionPart extends DrawableObject implements Serializable{
	private int playerOwner;
	private int explosionId;
	
	public ExplosionPart(Context c, int playerOwner, Direction direction, boolean end, int x, int y, char type, int explosionId) {
		super(c, calculateImageId(direction, end), x, y, type);
		this.playerOwner = playerOwner;
		this.explosionId = explosionId;
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
	
	public int getPlayerOwner(){
		return playerOwner;
	}
	
	public int getExplosionId(){
		return explosionId;
	}
}
