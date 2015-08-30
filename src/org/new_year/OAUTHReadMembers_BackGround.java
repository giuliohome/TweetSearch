package org.new_year;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class OAUTHReadMembers_BackGround extends
AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		
		
		//Get non Post => no encoded parameters!!!    
		HttpGet httpGet = new HttpGet(params[0]);
		CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(params[1],params[2]);
		httpOauthConsumer.setTokenWithSecret(params[3],params[4]);
		try { 
			httpOauthConsumer.sign(httpGet); // it is not a Post !!!
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DefaultHttpClient client = new DefaultHttpClient();	
		//String response="";
		
		
		
		try {
			HttpResponse response = client.execute(httpGet);
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
		} catch (ClientProtocolException e) {
			// Log.e("new_year_ko", "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			// Log.e("new_year_ko", "IOException");
			e.printStackTrace();
		}
		return builder.toString();
		//return response;
	}	

}
