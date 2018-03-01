package org.new_year;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.os.AsyncTask;

public class callback_BackGround extends AsyncTask<String, Void, String[]> {

	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		String CALLBACKURL = "app://myDroid";
		String[] myBG_results = new String[3];
		
		OAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
		CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(params[0], params[1]);
		httpOauthprovider.setOAuth10a(true);
		 String authUrl="";
		try {
			authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				 
		 	
	    	myBG_results[0] =  httpOauthConsumer.getToken();
	    	myBG_results[1] =  httpOauthConsumer.getTokenSecret();
	    	myBG_results[2] =  authUrl;
	        return myBG_results;
	}

}
