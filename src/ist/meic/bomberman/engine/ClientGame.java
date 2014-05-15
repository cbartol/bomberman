package ist.meic.bomberman.engine;

import ist.meic.bomberman.wifi.ClientReceiveActionsThread;
import ist.meic.bomberman.wifi.PlayerAction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class ClientGame implements IGame {
	private ClientReceiveActionsThread receiveThread;
	private Socket sendSocket;
	
	private List<Wall> walls;
	private Map<Integer, Obstacle> obstacles; 
	private Map<Integer, Player> players;
	private Map<Integer, Robot> robots;
	private Map<Integer, Bomb> bombs;
	private Map<Integer, List<ExplosionPart>> explosionParts;
	private DataOutputStream out;
	
	public ClientGame() throws UnknownHostException, IOException{
		String server = "localhost";
		int port = 20000;
		Socket receiveSocket = new Socket(server,port);
		DataOutputStream outToServer = new DataOutputStream(receiveSocket.getOutputStream());
		outToServer.writeBoolean(false);
		//TODO: read from the server all the state of the game (I need this before start the thread)
		receiveThread = new ClientReceiveActionsThread(this, receiveSocket);
		
		sendSocket = new Socket(server,port);
		outToServer = new DataOutputStream(sendSocket.getOutputStream());
		outToServer.writeBoolean(true);
		out = outToServer;
			
			
	}
	
	@Override
	public void start() {
		// it does not need to be a thread
	}

	@Override
	public void movePlayer(int playerId, Direction direction) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.MOVE.ordinal()); // move instruction
			out.writeInt(direction.ordinal());
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void dropBomb(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.DROP_BOMB.ordinal()); // bomb instruction
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void endGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pausePlayer(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.PAUSE.ordinal()); // pause instruction
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void resumePlayer(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.RESUME.ordinal()); // resume instruction
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean isPlayerWinner(int playerId) {
		try{
			out.writeInt(playerId);
			out.writeInt(PlayerAction.IS_WINNER.ordinal()); // is winner instruction
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getPlayerScore(int playerId) {
		return players.get(playerId).getScore();
	}

	public void changeObject(DrawableObject object) {
		// object.getType()
		// change the list
	}

}
