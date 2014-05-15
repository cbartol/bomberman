package ist.meic.bomberman;

import ist.meic.bomberman.engine.ClientGame;
import android.os.Bundle;

public class MultiplayerClientGameActivity extends GameActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.isSinglePlayer = false;
		//read from intent the server and port
		super.onCreate(savedInstanceState);
		String server = "localhost";
		int port = 20000;
		
		try {
			this.game = new ClientGame(this, gameArea, server, port);
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
}
