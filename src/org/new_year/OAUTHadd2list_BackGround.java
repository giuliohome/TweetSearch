package org.new_year;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

public class OAUTHadd2list_BackGround extends
AsyncTask<String, Void, Void> {
	
	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpPost post = new HttpPost(params[0]);
		UrlEncodedFormEntity form;

		if (params.length == 6)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("name", params[5] ));
		    try {
		    	form = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		    	//form.setContentEncoding(HTTP.UTF_8);
		    	post.setEntity(form);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (params.length == 7)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("owner_screen_name", params[5] ));
		    nameValuePairs.add(new BasicNameValuePair("slug", params[6] ));
		    try {
		    	form = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		    	//form.setContentEncoding(HTTP.UTF_8);
		    	post.setEntity(form);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (params.length == 8)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("slug", params[5] ));
		    nameValuePairs.add(new BasicNameValuePair("screen_name", params[6] ));
		    nameValuePairs.add(new BasicNameValuePair("owner_screen_name", params[7] ));
		    try {
		    	form = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		    	//form.setContentEncoding(HTTP.UTF_8);
		    	post.setEntity(form);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(params[1],params[2]);
		httpOauthConsumer.setTokenWithSecret(params[3],params[4]);
		try {
			httpOauthConsumer.sign(post);
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
			client.execute(post, new BasicResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		//return response;
	}	

}
