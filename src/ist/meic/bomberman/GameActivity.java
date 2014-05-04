package ist.meic.bomberman;

import ist.meic.bomberman.engine.Direction;
import ist.meic.bomberman.engine.Game;
import ist.meic.bomberman.engine.MapProperties;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	private Game game;
	private MapProperties mapProperties;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapProperties = new MapProperties(this, getIntent().getIntExtra("level", 1));
		setContentView(R.layout.activity_game);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int width = dm.widthPixels / 4;
		
		TextView text = (TextView) findViewById(R.id.playerName);
		text.setBackgroundColor(Color.RED);
		text.setWidth(width);
		
		text = (TextView) findViewById(R.id.playerScore);
		text.setBackgroundColor(Color.BLUE);
		text.setWidth(width);
		
		text = (TextView) findViewById(R.id.timeLeft);
		text.setBackgroundColor(Color.GREEN);
		text.setWidth(width);

		text = (TextView) findViewById(R.id.numberPlayers);
		text.setBackgroundColor(Color.YELLOW);
		text.setWidth(width);

		startGame();
	}
	
	@Override
	protected void onDestroy() {
		game.endGame();
		super.onDestroy();
	}
	
	public void startGame(){
		game = new Game(this, (RelativeLayout) findViewById(R.id.gameArea), mapProperties);
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
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.gameArea);
		ImageView image = game.getImage();
		
		Log.i("Layout", "x: " + layout.getWidth() + " y: " + layout.getHeight());
		Log.i("Image", "getWidth: " + image.getWidth() + " getHeight: " + image.getHeight());
		Log.i("Image", "getLeft: " + image.getLeft() + " getTop: " + image.getTop());
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
