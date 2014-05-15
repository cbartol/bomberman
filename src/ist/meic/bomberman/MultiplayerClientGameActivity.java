package ist.meic.bomberman;

import ist.meic.bomberman.engine.ClientGame;
import android.os.Bundle;

public class MultiplayerClientGameActivity extends GameActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.isSinglePlayer = false;
		String server = getIntent().getExtras().getString("ip");
		int port = getIntent().getExtras().getInt("port", 20000);
		super.onCreate(savedInstanceState);
		try {
			this.game = new ClientGame(this, gameArea, server, port);
			super.updateInfo();
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
