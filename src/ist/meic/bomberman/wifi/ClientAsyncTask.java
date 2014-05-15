package ist.meic.bomberman.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public class ClientAsyncTask extends AsyncTask<Void, Void, String>{

	private Socket clientSocket = new Socket();

	/**
	 * @param context
	 * @param statusText
	 */
	public ClientAsyncTask() {
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			clientSocket.bind(null);
		    clientSocket.connect((new InetSocketAddress("10.0.2.2", 4444)), 500);
		    PrintWriter printwriter = new PrintWriter(clientSocket.getOutputStream(),true);
            printwriter.write("Enviei esta mensagem");
            printwriter.flush();
            printwriter.close();
            clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
