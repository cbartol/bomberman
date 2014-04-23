package ist.meic.bomberman;

import ist.meic.bomberman.engine.MapProperties;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		

		// Testing purposes
		new MapProperties(this, 1);

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
		// add code for placing bombs
	}
	
	public void moveUp(View v) {
		// add code for moving up
	}
	
	public void moveDown(View v) {
		// add code for moving down
	}
	
	public void moveLeft(View v) {
		// add code for moving left
	}
	
	public void moveRight(View v) {
		// add code for moving right
	}
}
