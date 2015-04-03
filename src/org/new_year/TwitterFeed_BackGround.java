/**
 * 
 */
package org.new_year;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

/**
 * @author MyHome
 *
 */
public class TwitterFeed_BackGround extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					//  Log.e("new_year_ok_line", line);
				}
			} else {
				//  Log.e("new_year_0", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			// Log.e("new_year_ko", "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			// Log.e("new_year_ko", "IOException");
			e.printStackTrace();
		}
		return builder.toString();
	}


	
	

}
