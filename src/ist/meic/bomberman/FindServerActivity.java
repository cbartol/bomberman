package ist.meic.bomberman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FindServerActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_server);
	}
	
	public void connectToServer(View view) {
		//fetch the IP and the PORT of the server (from an InputView)
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FindServerActivity.this.overridePendingTransition(0, 0);
	}
}