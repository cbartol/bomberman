package ist.meic.bomberman.engine;

import ist.meic.bomberman.R;
import android.content.Context;
import android.widget.RelativeLayout;


public class Robot extends Entity {

	public Robot(Context c, RelativeLayout gameArea,
			int x, int y) {
		super(c, gameArea, R.drawable.robot, x, y);
	}


}
