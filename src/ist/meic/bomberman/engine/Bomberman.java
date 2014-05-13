package ist.meic.bomberman.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Bomberman {
	
	private Bitmap bitmap;
	private int x;
	private int y;
	
	public Bomberman(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void draw(Canvas canvas) {
		Log.d("Bitmap", bitmap.toString());
		System.out.println("Bitmap x= " + ( x - (bitmap.getWidth() / 2)));
		System.out.println("Bitmap y= " + ( y - (bitmap.getHeight() / 2)));
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}
}
