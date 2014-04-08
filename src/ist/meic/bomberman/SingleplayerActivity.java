package ist.meic.bomberman;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class SingleplayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleplayer);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Button myButton = (Button) findViewById(R.id.continuegame);
		myButton.setBackgroundColor(R.drawable.black_button);
		myButton.setEnabled(false);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    SingleplayerActivity.this.overridePendingTransition(0, 0);
	}
}
