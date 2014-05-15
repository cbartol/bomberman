package ist.meic.bomberman.wifi;

import ist.meic.bomberman.engine.ServerGame;

import java.net.Socket;

public class ServerReceiver extends Thread {
	private Socket socket;
	private ServerGame game;
	
	public ServerReceiver (Socket socket, ServerGame server){
		this.socket = socket;
		this.game = server;
	}
	
	@Override
	public void run() {
		super.run();
		while(true) {
			try {
				DataInputStream is = new DataInputStream(socket.getInputStream());
				int playerId = is.readInt();
				int action = is.readInt();
				switch(PlayerAction.values()[action]){
					case MOVE:
						int direction =  is.readInt();
						game.movePlayer(playerId, Direction.values()[direction]);
						break;
					case DROP_BOMB:
						game.dropBomb(playerId);
						break;
					case PAUSE:
						game.pausePlayer(playerId);
						break;
					case RESUME:
						game.resumePlayer(playerId);
						break;
					case IS_WINNER:
						DataOutputStream os = new DataOutputStream(socket.getOutputStream());
						os.writeBoolean(game.isPlayerWinner(playerId));
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}	
