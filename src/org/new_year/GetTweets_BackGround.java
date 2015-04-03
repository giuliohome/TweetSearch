package org.new_year;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;

public class GetTweets_BackGround extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpResponse response;
		StringBuilder builder = new StringBuilder();
		try {

			HttpClient client = new  DefaultHttpClient();
			// Log.e("new_year_ko", "http client"); 
			HttpGet httpGet = new HttpGet(params[0]);
			// Log.e("new_year_ko","http get") ;
			String consumerKey = params[1];
			String consumerSecret = params[2];
			String userKey = params[3];
			String userSecret = params[4];

			CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
			httpOauthConsumer.setTokenWithSecret(userKey, userSecret);
			try {
				httpOauthConsumer.sign(httpGet);
			} catch (OAuthMessageSignerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthExpectationFailedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OAuthCommunicationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			
			//ResponseHandler<String> responseHandler =
				//	new BasicResponseHandler();
			// Log.e("new_year_ko", "response handler");
			response = null; 
			if ( client == null) {return null;}; 
			if ( httpGet == null) {return null;}; 
			//if ( responseHandler == null) {return null;}; 
			response = client.execute(httpGet);//, responseHandler);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (
					//response != null
					statusCode == 200
					) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					//  Log.e("new_year_ok_line", line);
				}
				
			} else {
				  //Log.e("new_year_0", "Failed to download file");
			}
			
			// Log.e("new_year_ko", "resp body");
		} catch(Exception ex) {
			// Log.e("new_year_ko no internet", ex .toString()); 
			ex.printStackTrace();
			return null;
		}
		return builder.toString();
	}

}
