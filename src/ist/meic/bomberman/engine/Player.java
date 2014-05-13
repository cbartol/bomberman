package ist.meic.bomberman.engine;

import android.content.Context;

public class Player extends Entity {

	private int id;
	private int score = 0;
	private boolean alive = true;

	public Player(int id, Context c, double dyingPoints, int x, int y) {
		super(c, getPlayerDrawable(c, id), dyingPoints, x, y);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public int addScore(int points) {
		return score += points;
	}

	public void resetScore() {
		score = 0;
	}

	public boolean isDead() {
		return !alive;
	}

	@Override
	public double destroy() {
		if (alive) {
			alive = false;
			return super.destroy();
		}
		return 0;
	}
	
	private static int getPlayerDrawable(final Context c, final int playerId){
		return c.getResources().getIdentifier("player" + playerId, "drawable", c.getPackageName());
	}
}
