package ist.meic.bomberman;

import ist.meic.bomberman.engine.Direction;
import ist.meic.bomberman.engine.Game;
import ist.meic.bomberman.engine.GameMapView;
import ist.meic.bomberman.engine.MapProperties;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MultiplayerGameActivity extends Activity {
	
	private Game game;
	private MapProperties mapProperties;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_game);
		mapProperties = new MapProperties(this, getIntent().getIntExtra("level", 1));
		setContentView(R.layout.activity_game);
		
		TextView text = (TextView) findViewById(R.id.playerName);
		text.setText(getString(R.string.player_name) + "\nplayer1");
		
		text = (TextView) findViewById(R.id.playerScore);
		text.setText(getString(R.string.player_score) + "\n0");
		
		text = (TextView) findViewById(R.id.timeLeft);
		text.setText(getString(R.string.time_left) + "\n" + mapProperties.getGameDuration());

		text = (TextView) findViewById(R.id.numberPlayers);
		text.setText(getString(R.string.number_players) + "\n1");

		startGame();
	}
	
	@Override
	protected void onDestroy() {
		game.endGame();
		super.onDestroy();
	}
	
	public void startGame(){
		game = new Game(this, (GameMapView) findViewById(R.id.gameAreaSinglePlayer), mapProperties, 2);
	}
	
	public void onQuit(View v) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void onPause(View v) {
		// add code for pause functionality
	}
	
	public void placeBomb(View v) {
		game.dropBomb(this, 1);
	}
	
	public void moveUp(View v) {
		game.movePlayer(1, Direction.UP);
	}
	
	public void moveDown(View v) {
		game.movePlayer(1, Direction.DOWN);
	}
	
	public void moveLeft(View v) {
		game.movePlayer(1, Direction.LEFT);
	}
	
	public void moveRight(View v) {
		game.movePlayer(1, Direction.RIGHT);
	}
}
