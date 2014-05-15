package ist.meic.bomberman;

import ist.meic.bomberman.engine.ClientGame;
import android.os.Bundle;

public class MultiplayerClientGameActivity extends GameActivity {
	private boolean stuck = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.isSinglePlayer = false;
		String server = getIntent().getExtras().getString("ip");
		int port = getIntent().getExtras().getInt("port", 20000);
		super.onCreate(savedInstanceState);
		this.game = new ClientGame(this, gameArea, server, port);
		game.start();
		while(stuck);
		super.updateInfo();
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void releaseTheUI() {
		stuck = false;
	}
}
