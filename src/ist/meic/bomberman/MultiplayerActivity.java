package ist.meic.bomberman;

import android.app.Activity;
import android.os.Bundle;

public class MultiplayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    MultiplayerActivity.this.overridePendingTransition(0, 0);
	}
}
