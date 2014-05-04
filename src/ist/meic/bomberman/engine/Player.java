package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;
import android.widget.RelativeLayout;

public class Player extends Entity {

	private int id; 
	private int score = 0;
	private boolean alive = true;
	
	public Player(int id, Context c, RelativeLayout gameArea, int x, int y) {
		super(c, gameArea, R.drawable.bomberman, x, y);
		this.id = id;
	}

	public int getId(){
		return id;
	}
	
	public int getScore(){
		return score;
	}
	public int addScore(int points){
		return score += points;
	}
	
	public void resetScore(){
		score = 0;
	}

	public boolean isDead() {
		return !alive;
	}
	
	@Override
	public void destroy() {
		if (alive) {
			alive = false;
			super.destroy();
		}
	}
}
