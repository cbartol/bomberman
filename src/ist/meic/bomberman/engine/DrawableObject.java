package ist.meic.bomberman.engine;

import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public abstract class DrawableObject implements Serializable{
	private char type;
	private int x;
	private int y;
	private static final int MATRIX_TO_PIXEL = 40; 
	transient private Bitmap image;
	transient private Context context;
	private int imageResId;
	
	public DrawableObject(Context c, int imageResId, int x, int y, char type){
		this.context = c;
		this.x = x;
		this.y = y;
		this.imageResId = imageResId;
		this.type = type;
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
	
	private void reloadImage(){
		setImage(imageResId);
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
	public char getType() {
		return type;
	}
	public void setContext(Context c){
		this.context = c;
		reloadImage();
	}
}
