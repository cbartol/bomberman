package ist.meic.bomberman;

import ist.meic.bomberman.engine.MapProperties;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateServerActivity extends Activity {
	private int levels = 1;
	private MapProperties selectedMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_server);
		Resources res = getResources();
		levels = res.getInteger(R.integer.max_levels);
		addItemsOnLevelSpinner();
		
		//TODO: show some where the IP and the PORT of the server
	}


	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    CreateServerActivity.this.overridePendingTransition(0, 0);
	}
	
	public void newGame(View v) {
		Intent intent = new Intent(CreateServerActivity.this,
				MultiplayerServerGameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("level", selectedMap.getLevel());
		startActivity(intent);
	}
	
	public void addItemsOnLevelSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.levelSpinner);
		List<String> list = new ArrayList<String>();
		for(int i = 1 ; i <= levels ; i++){
			list.add("Level " + i);
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		final CreateServerActivity context = this;
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				int level = pos +1;
				context.setLevel(level);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	private void setLevel(int level){
		selectedMap = new MapProperties(this, level);
		TextView levelTitle = (TextView) findViewById(R.id.levelTitle);
		levelTitle.setText(selectedMap.getName());
		ImageView preview = (ImageView) findViewById(R.id.mapView);
		int mapId = getResources().getIdentifier("map" + selectedMap.getLevel() + "_preview", "drawable", getPackageName());
		preview.setImageDrawable(getResources().getDrawable(mapId));
	}

}
