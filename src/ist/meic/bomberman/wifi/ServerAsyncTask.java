package ist.meic.bomberman.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public class ServerAsyncTask extends AsyncTask<Void, Void, String>{

	/**
	 * @param context
	 * @param statusText
	 */
	public ServerAsyncTask() {
	}

	@Override
	protected String doInBackground(Void... params) {
		while(true) {
			try {
				ServerSocket serverSocket = new ServerSocket(4444);
				Socket client = serverSocket.accept();
				InputStreamReader inputStreamReader =
						new InputStreamReader(client.getInputStream());
				BufferedReader  bufferedReader =
						new BufferedReader(inputStreamReader);
				String  message = bufferedReader.readLine();

				System.out.println(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
