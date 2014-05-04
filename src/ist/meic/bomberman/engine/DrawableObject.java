package ist.meic.bomberman.engine;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public abstract class DrawableObject {
	private int x;
	private int y;
	private static final int MATRIX_TO_PIXEL = 40; 
	private ImageView image;
	
	public DrawableObject(Context c, RelativeLayout gameArea, int imageResId, int x, int y){
		this.x = x;
		this.y = y;
		image = new ImageView(c);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = coordToPixel(x);
		params.topMargin = coordToPixel(y);
		image.setLayoutParams(params);
		image.setImageDrawable(c.getResources().getDrawable(imageResId));

		gameArea.addView(image);
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	protected int setX(int x){
		this.x = x;
		draw();
		return this.x;
	}
	

	protected int setY(int y){
		this.y = y;
		draw();
		return this.y;
	}
	
	private void draw() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		image.setLayoutParams(params);
		params.leftMargin = coordToPixel(x);
		params.topMargin = coordToPixel(y);
		image.setLayoutParams(params);
	}
	
	public ImageView getImage(){
		return image;
	}
	
	public int coordToPixel(int coord){
		return coord*MATRIX_TO_PIXEL;
	}
	
	public void hide(){
		image.setVisibility(View.INVISIBLE);
	}
	
	public void unhide(){
		image.setVisibility(View.VISIBLE);
	}
	
	public void destroy(){
		((RelativeLayout)image.getParent()).removeView(image);
	}
}
