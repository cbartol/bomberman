package ist.meic.bomberman;

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

	}
	
	public void onQuit(View view) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
