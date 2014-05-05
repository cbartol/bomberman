package ist.meic.bomberman.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class DrawableObject {
	private int x;
	private int y;
	private static final int MATRIX_TO_PIXEL = 40; 
	private Bitmap image;
	
	public DrawableObject(Context c, int imageResId, int x, int y){
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
	
	public int coordToPixel(int coord){
		return coord*MATRIX_TO_PIXEL;
	}
	
	public void destroy(){
		//?????
	}
}
