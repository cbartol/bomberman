package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;

public class Bomb extends DrawableObject {
	private int playerId;
	private int explosionId;

	public Bomb(Context c, int playerId, int explosionId, int x, int y) {
		super(c, R.drawable.bomb, x, y);
		this.explosionId = explosionId;
		this.playerId = playerId;
	}
	
	public int getPlayerId(){
		return playerId;
	}

	public void explode(){
		setImage(R.drawable.explosion_center);
	}
	
	public int getExplosionId(){
		return explosionId;
	}
}
