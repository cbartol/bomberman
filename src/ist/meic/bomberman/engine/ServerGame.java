package ist.meic.bomberman.engine;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import ist.meic.bomberman.GameActivity;
import ist.meic.bomberman.wifi.MainThreadServer;

@SuppressLint("UseSparseArrays")
public class ServerGame extends Game {
	
	// client sockets to update the game state
	private HashMap<Integer, Socket> clientsSockets;
	private MainThreadServer mainThreadServer;
	private int idClient = 0;

	public ServerGame(GameActivity a, GameMapView gameArea,
			MapProperties mapProp, int maxPlayers) {
		super(a, gameArea, mapProp, maxPlayers);
		clientsSockets = new HashMap<Integer, Socket>();
		mainThreadServer = new MainThreadServer((ServerGame) this);
		mainThreadServer.run();
	}

	@Override
	public synchronized void movePlayer(int id, Direction direction) {
		super.movePlayer(id, direction);
		/* Send message to players with the movement */
	}
	
	public void addClient(Socket socketClient) {
			clientsSockets.put(++idClient, socketClient);
			// Send game state do socketClient
			updateGameState(socketClient);
	}
	
	private void updateGameState(Socket socketclient) {
		
	}
}
