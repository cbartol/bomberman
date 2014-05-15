package ist.meic.bomberman;

import ist.meic.bomberman.engine.ClientGame;
import android.os.Bundle;

public class MultiplayerClientGameActivity extends GameActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isSinglePlayer = false;
		//read from intent the server and port
		super.onCreate(savedInstanceState);
		try {
			game = new ClientGame();
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}
}
