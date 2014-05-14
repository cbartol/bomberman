package ist.meic.bomberman;

import android.os.Bundle;

public class CreateServerActivity extends MultiplayerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_server);
	}


	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    CreateServerActivity.this.overridePendingTransition(0, 0);
	}

}
