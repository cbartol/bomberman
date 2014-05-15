package ist.meic.bomberman;

import ist.meic.bomberman.engine.Direction;
import ist.meic.bomberman.engine.Game;
import ist.meic.bomberman.engine.GameMapView;
import ist.meic.bomberman.engine.IGame;
import ist.meic.bomberman.engine.MapProperties;
import ist.meic.bomberman.engine.Player;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends Activity {
	protected IGame game;
	protected GameMapView gameArea;
	protected MapProperties mapProperties;
	protected boolean isSinglePlayer = true;
	protected int playerId = 1;
	
	private SurfaceHolder surfaceHolder;
	private Runnable drawCallback;
	private Handler mHandler;
	private boolean gameHasEnded = false;
	private boolean alreadyEnded = false; //this is required to avoid multiple dialogs with the same message

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapProperties = new MapProperties(this, getIntent().getIntExtra(
				"level", 1));
		setContentView(R.layout.activity_game);

		TextView text = (TextView) findViewById(R.id.playerName);
		text.setText(getString(R.string.player_name) + "\nplayer1");

		text = (TextView) findViewById(R.id.playerScore);
		text.setText(getString(R.string.player_score) + "\n0");

		text = (TextView) findViewById(R.id.timeLeft);
		text.setText(getString(R.string.time_left) + "\n"
				+ mapProperties.getGameDuration());

		text = (TextView) findViewById(R.id.numberPlayers);
		text.setText(getString(R.string.number_players) + "\n1");

		gameArea = (GameMapView) findViewById(R.id.gameArea);
		surfaceHolder = gameArea.getHolder();
		mHandler = new Handler();
		drawCallback = new Runnable() {
			@Override
			public void run() {
				if (gameHasEnded && !alreadyEnded) {
					alreadyEnded = true;
					game.endGame();
					showEndGameMessage();
				}
				Canvas canvas = null;
				// try locking the canvas for exclusive pixel editing
				// in the surface
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						// update game state
						// render state to the screen
						// draws the canvas on the panel
						// this.gameMap.onDraw(canvas);
						gameArea.postInvalidate();
					}
				} finally {
					// in case of an exception the surface is not left in
					// an inconsistent state
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				} // end finally
			}
		};
		if(isSinglePlayer){
			game = new Game(this, gameArea, mapProperties, 1);
			startGame();
		}
	}

	@Override
	protected void onDestroy() {
		game.endGame();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		onPause(null);
	}

	public void startGame() {
		game.start();
	}

	public void onQuit(View v) {
		finish();
	}

	public void onPause(View v) {
		game.pausePlayer(playerId);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
//		alertDialogBuilder.setTitle(R.string.pause_message);

		// set dialog message
		alertDialogBuilder
				.setMessage(R.string.pause_message)
				.setCancelable(false)
				.setNeutralButton(R.string.resume,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								game.resumePlayer(playerId);
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void placeBomb(View v) {
		game.dropBomb(playerId);
	}

	public void moveUp(View v) {
		game.movePlayer(playerId, Direction.UP);
	}

	public void moveDown(View v) {
		game.movePlayer(playerId, Direction.DOWN);
	}

	public void moveLeft(View v) {
		game.movePlayer(playerId, Direction.LEFT);
	}

	public void moveRight(View v) {
		game.movePlayer(playerId, Direction.RIGHT);
	}

	public void changeScore(Player player) {
		if (player.getId() == playerId) {
			int score = player.getScore();
			TextView text = (TextView) findViewById(R.id.playerScore);
			text.setText(getString(R.string.player_score) + "\n" + score);
		}
	}

	public void changeTime(int timeLeft) {
		TextView text = (TextView) findViewById(R.id.timeLeft);
		text.setText(getString(R.string.time_left) + "\n" + timeLeft);
	}

	public void draw(boolean endGame) {
		gameHasEnded = endGame;
		mHandler.post(drawCallback);
	}

	private void showEndGameMessage() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		if(game.isPlayerWinner(playerId)){
			alertDialogBuilder.setTitle(R.string.you_won);
		} else {
			alertDialogBuilder.setTitle(R.string.you_lost);
		}

		// set dialog message
		alertDialogBuilder
				.setMessage("Final score: " + game.getPlayerScore(playerId))
				.setCancelable(false)
				.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
