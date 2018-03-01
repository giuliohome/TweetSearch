package org.new_year;

import android.net.Uri;
import android.os.AsyncTask;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;


public class TokenAccess_BackGround extends AsyncTask<String, Void, String[]> {

	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub

		String[] myBG_results = new String[2];
		
	    String verifier = Uri.parse(params[2]).getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
		CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(params[2],params[1] );
		httpOauthConsumer.setTokenWithSecret(params[3],params[4]);
		CommonsHttpOAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
		//httpOauthprovider.setHttpClient((HttpClient)httpOauthConsumer);
		httpOauthprovider.setOAuth10a(true);
		// this will populate token and token_secret in consumer

		try {
			httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
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

		// Save user_key and user_secret in user preferences and return
				
		
    	myBG_results[0] =  httpOauthConsumer.getToken();
    	myBG_results[1] =  httpOauthConsumer.getTokenSecret();
    	return myBG_results;
		
		
	}

}
