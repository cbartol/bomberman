package ist.meic.bomberman.multiplayer;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
	public void createserver(View view) {
		Intent intent = new Intent(MultiplayerActivity.this, CreateServerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}
	public void joinserver(View view) {
		Intent intent = new Intent(MultiplayerActivity.this, FindServerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}
}
