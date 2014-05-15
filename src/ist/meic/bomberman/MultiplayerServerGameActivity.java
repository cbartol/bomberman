package ist.meic.bomberman;

import ist.meic.bomberman.engine.Direction;
import ist.meic.bomberman.engine.Game;
import ist.meic.bomberman.engine.GameMapView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MultiplayerServerGameActivity extends GameActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isSinglePlayer = false;
		super.onCreate(savedInstanceState);
		// TODO: create the server thread and wait for clients requests to join the game
		game = new Game(this, gameArea, mapProperties, /*mapProperties.getMaxPlayers()*/ 2);
		startGame();
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    MultiplayerServerGameActivity.this.overridePendingTransition(0, 0);
	}
	
	@Override
	protected void onDestroy() {
		game.endGame();
		super.onDestroy();
	}
	
	@Override
	public void startGame(){
		game = new Game(this, (GameMapView) findViewById(R.id.gameArea), mapProperties, 2);
	}
	
	@Override
	public void onQuit(View v) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	/* TODO
	@Override
	public void placeBomb(View v) {
	}
	
	@Override
	public void moveUp(View v) {
	}
	
	@Override
	public void moveDown(View v) {
	}
	
	@Override
	public void moveLeft(View v) {
	}
	
	@Override
	public void moveRight(View v) {
	}
	*/
}
