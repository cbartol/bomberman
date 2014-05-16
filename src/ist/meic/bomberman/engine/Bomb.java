package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;

import java.io.Serializable;

import android.content.Context;

public class Bomb extends DrawableObject implements Serializable{
	private int playerId;
	private int explosionId;

	public Bomb(Context c, int playerId, int explosionId, int x, int y, char type) {
		super(c, R.drawable.bomb, x, y, type);
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
