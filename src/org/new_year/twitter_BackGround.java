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

public class twitter_BackGround extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpPost post = new HttpPost(params[0]);
		UrlEncodedFormEntity form;
		if (params.length == 7)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("status", params[5] ));
		    nameValuePairs.add(new BasicNameValuePair("in_reply_to_status_id", params[6] ));
			if (twitterActivity.mediaId != null && twitterActivity.mediaId.length()>0) {
				nameValuePairs.add(new BasicNameValuePair("media_ids", twitterActivity.mediaId ));
			}
		    try {
				form = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
				//form.setContentEncoding(HTTP.UTF_8);
		    	post.setEntity(form);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "{\"error\":\""+e.getMessage()+"\"}";
			}
		}
		if (params.length == 6)
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (params[5].startsWith("status=")) {
				nameValuePairs.add(new BasicNameValuePair("status", params[5].replaceFirst("status=", "") ));
			}
			if (params[5].startsWith("id=")) {
				nameValuePairs.add(new BasicNameValuePair("id", params[5].replaceFirst("id=", "") ));
			}
			if (params[5].equals("media=?")) {
				if (twitterActivity.encodedImage == null)
					return "{\"error\":\"no encoded image\"}";
				nameValuePairs.add(new BasicNameValuePair("media", twitterActivity.encodedImage ));
			}
			if (twitterActivity.mediaId != null && twitterActivity.mediaId.length()>0) {
				nameValuePairs.add(new BasicNameValuePair("media_ids", twitterActivity.mediaId ));
			}
		    try {
		    	form = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		    	//form.setContentEncoding(HTTP.UTF_8);
		    	post.setEntity(form);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "{\"error\":\""+e.getMessage()+"\"}";
			}
		}
		if (params.length == 5)
		{
			//nothing: just retweet - without status
		}

		CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(params[1],params[2]);
		httpOauthConsumer.setTokenWithSecret(params[3],params[4]);
		try {
			httpOauthConsumer.sign(post);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		}

		DefaultHttpClient client = new DefaultHttpClient();
		String response="";
		
		
		
		try {
			response = client.execute(post, new BasicResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{\"error\":\""+e.getMessage()+"\"}";
		}
		twitterActivity.mediaId="";
		return response;
	}

}
