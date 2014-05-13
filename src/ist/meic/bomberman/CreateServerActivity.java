package ist.meic.bomberman;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class CreateServerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_server);
	}


	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    CreateServerActivity.this.overridePendingTransition(0, 0);
	}

}
