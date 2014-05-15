package ist.meic.bomberman.engine;

public interface IGame {

	public void start();
	
	public void movePlayer(int playerId, Direction direction);
	
	public void dropBomb(int playerId);
	
	public void endGame(); // player quits the game
	
	public void pausePlayer(int playerId);
	
	public void resumePlayer(int playerId);
	
	public boolean isPlayerWinner(int playerId);
	
	public int getPlayerScore(int playerId);
	
}
