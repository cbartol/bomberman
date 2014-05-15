package ist.meic.bomberman.engine;

import java.io.Serializable;

import android.content.Context;

public class Player extends Entity implements Serializable{

	private int id;
	private int score = 0;
	private boolean alive = true;
	private boolean canDropBomb = true;

	public Player(int id, Context c, int dyingPoints, int x, int y, char type) {
		super(c, getPlayerDrawable(c, id), dyingPoints, x, y, type);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public int addScore(Entity entity) {
		if(entity != null && !this.equals(entity) && this.alive){
			return score += entity.getDyingPoints();
		}
		return 0;
	}

	public void resetScore() {
		score = 0;
	}

	public boolean isDead() {
		return !alive;
	}

	@Override
	public int destroy() {
		if (alive) {
			alive = false;
			return super.destroy();
		}
		return 0;
	}
	
	private static int getPlayerDrawable(final Context c, final int playerId){
		return c.getResources().getIdentifier("player" + playerId, "drawable", c.getPackageName());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Player){
			Player p = (Player) o;
			this.equals(p);
		}
		return super.equals(o);
	}
	
	public boolean equals(Player p){
		return p.id == this.id;
	}
	
	public boolean canDropBomb(){
		return canDropBomb;
	}
	
	public void canDropBomb(boolean b){
		canDropBomb = b;
	}
}
