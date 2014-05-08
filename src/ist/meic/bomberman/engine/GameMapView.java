package ist.meic.bomberman.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameMapView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Game game;
	private Bitmap map;
	private boolean firstTimeDraw = true;
	private int mapWidth;
	private int mapHeight;
	private int topOffset = 0;
	private int leftOffset = 0;
	
	public void setMap(int mapId){
		this.map = BitmapFactory.decodeResource(getResources(), mapId);
		Log.i("VIEW", "width: " +getWidth() + " height: " + getHeight());
		Log.i("MAP", "width: " +map.getWidth() + " height: " + map.getHeight());
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	@Override
	protected void onDraw(Canvas realCanvas) {
//		super.onDraw(realCanvas); // uncomment this line and comment the rest of the method to edit activity_game.xml 
		if(firstTimeDraw){ //omg... plz optimize this...  I can't get the size of the view anywhere else.
			firstTimeDraw = false;
			final int viewWidth = getWidth();
			final int viewHeight = getHeight();
			mapWidth = map.getWidth();
			mapHeight = map.getHeight();
			float scale;
			if(Math.abs(viewWidth-mapWidth) < Math.abs(viewHeight-mapHeight)){
				//the width of the map is equal to the width of the game area
				scale = viewWidth / mapWidth;
				mapWidth = viewWidth;
				mapHeight = (int)(mapHeight * scale);
				topOffset = Math.abs(viewHeight-mapHeight) / 2;
			} else {
				//the height of the map is equal to the height of the game area
				scale = viewHeight / mapHeight;
				mapWidth = (int)(mapWidth * scale);
				mapHeight = viewHeight;
				leftOffset = Math.abs(viewWidth-mapWidth) / 2;
			}
		}
		realCanvas.drawRGB(80, 160, 0);
		Bitmap bmOverlay = Bitmap.createBitmap(map.getWidth(), map.getHeight(), map.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(map, 0, 0, null);
		for (DrawableObject object : game.getObjectsToDraw()) {
			int side = object.getImage().getWidth(); // width is equal to height
			canvas.drawBitmap(object.getImage(), object.getX()*side, object.getY()*side, null);
		}
		realCanvas.drawBitmap(Bitmap.createScaledBitmap(bmOverlay, mapWidth, mapHeight, false), leftOffset, topOffset, null);
	}
	
	// Ignored methods section
	public GameMapView(Context context) {
		super(context);
	}
	
	public GameMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public GameMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

}
