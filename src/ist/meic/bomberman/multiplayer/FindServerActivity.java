package ist.meic.bomberman.multiplayer;

import ist.meic.bomberman.R;
import ist.meic.bomberman.R.id;
import ist.meic.bomberman.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FindServerActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_server);
	}
	
	public void connectToServer(View view) {
		//fetch the IP and the PORT of the server (from an InputView)
		EditText serverIp = (EditText) findViewById(R.id.editText1);
		String[] serverIpString = serverIp.getText().toString().split(":");
		Intent intent = new Intent(FindServerActivity.this,
				MultiplayerClientGameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("ip", serverIpString[0]);
		intent.putExtra("port", serverIpString[1]);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FindServerActivity.this.overridePendingTransition(0, 0);
	}
}