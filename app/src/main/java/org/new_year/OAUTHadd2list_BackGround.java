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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.api.client.http.HttpResponse;

import android.os.AsyncTask;

public class OAUTHadd2list_BackGround extends
AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... params) {
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
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(org.apache.http.HttpResponse response)
					throws ClientProtocolException, IOException {
				// TODO Auto-generated method stub
				return EntityUtils.toString(response.getEntity());
			}
		    };
		
		String server_reply="";
		try {
			server_reply = client.execute(post, responseHandler); //new BasicResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			server_reply+=" - "+e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			server_reply+=" - "+e.getMessage();
		}
		
		return server_reply;
		//return response;
	}	

}
