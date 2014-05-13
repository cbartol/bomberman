package ist.meic.bomberman.engine;

import android.content.Context;

public abstract class Entity extends DrawableObject {
	private double dyingPoints;
	
	public Entity(Context c, int imageResId, double dyingPoints, int x, int y) {
		super(c, imageResId, x, y);
		this.dyingPoints = dyingPoints;
	}

	public void move(Direction direction) {
		switch (direction) {
		case UP:
			setY(getY() - 1);
			break;
		case DOWN:
			setY(getY() + 1);
			break;
		case LEFT:
			setX(getX() - 1);
			break;
		case RIGHT:
			setX(getX() + 1);
			break;
		}
	}
	
	public double destroy(){
		setImage(0);
		return dyingPoints;
	}
}
