package ist.meic.bomberman.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public abstract class DrawableObject {
	private int x;
	private int y;
	private static final int MATRIX_TO_PIXEL = 40; 
	private Bitmap image;
	private Context context;
	
	public DrawableObject(Context c, int imageResId, int x, int y){
		this.context = c;
		this.x = x;
		this.y = y;
		image = BitmapFactory.decodeResource(c.getResources(), imageResId);
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	protected int setX(int x){
		return this.x = x;
	}
	

	protected int setY(int y){
		return this.y = y;
	}
	
	public Bitmap getImage(){
		return image;
	}
	
	protected void setImage(int imageId){
		if(imageId != 0){
			this.image = BitmapFactory.decodeResource(context.getResources(), imageId);
		} else {
			this.image = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		}
	}
	
	public int coordToPixel(int coord){
		return coord*MATRIX_TO_PIXEL;
	}
	
	public int destroy(){
		return 0;
	}
}
