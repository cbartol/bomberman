package ist.meic.bomberman.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameMapView extends SurfaceView implements SurfaceHolder.Callback{
	
	private IGame game;
	private Bitmap map;
	private boolean firstTimeDraw = true;
	private int originalMapWidth;
	private int originalMapHeight;
	private double mapWidth;
	private double mapHeight;
	private double topOffset = 0;
	private double leftOffset = 0;

	public void setGame(IGame game, int mapWidth, int mapHeight){
		this.game = game;
		this.originalMapWidth = mapWidth;
		this.originalMapHeight = mapHeight;
	}
	
	@Override
	protected void onDraw(Canvas realCanvas) {
//		super.onDraw(realCanvas); // uncomment this line and comment the rest of the method to edit activity_game.xml 

		if(firstTimeDraw){ //omg... plz optimize this...  I can't get the size of the view anywhere else.
			firstTimeDraw = false;
			final double viewWidth = getWidth();
			final double viewHeight = getHeight();
			mapWidth = originalMapWidth;
			mapHeight = originalMapHeight;
			Log.i("view", "width: " + viewWidth + " height: " + viewHeight);
			Log.i("image", "width: " + mapWidth + " height: " + mapHeight);
			if(viewWidth/viewHeight < mapWidth/mapHeight){
				//the width of the map is equal to the width of the game area
				mapHeight = mapHeight * viewWidth / mapWidth;
				mapWidth = viewWidth;
				topOffset = 0; // Math.abs(viewHeight-mapHeight) / 2;
			} else {
				//the height of the map is equal to the height of the game area
				mapWidth = mapWidth * viewHeight / mapHeight;
				mapHeight = viewHeight;
				leftOffset = Math.abs(viewWidth-mapWidth) / 2;
			}
			map = Bitmap.createBitmap(originalMapWidth, originalMapHeight, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(map);
			for (DrawableObject object : game.getFixedObjects()) {
				final int side = object.getImage().getWidth(); // width is equal to height
				canvas.drawBitmap(object.getImage(), object.getX()*side, object.getY()*side, null);
			}
		}
		Bitmap bmOverlay = Bitmap.createBitmap(originalMapWidth, originalMapHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawRGB(80, 160, 0);
		canvas.drawBitmap(map, 0, 0, null);
		for (DrawableObject object : game.getObjectsToDraw()) {
			final int side = object.getImage().getWidth(); // width is equal to height
			canvas.drawBitmap(object.getImage(), object.getX()*side, object.getY()*side, null);
		}
		realCanvas.drawBitmap(Bitmap.createScaledBitmap(bmOverlay,(int) mapWidth,(int) mapHeight, false), (int) leftOffset, (int) topOffset, null);
		
	}
	
	// Ignored methods section
	public GameMapView(Context context) {
		super(context);
	}
	
	public GameMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public GameMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
