package org.new_year;


import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.net.Uri;
import android.os.AsyncTask;

public class Authorising_BackGround extends
		AsyncTask<String, Void, String[]> {

	@Override
	protected String[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		String[] myBG_results = new String[2];
		String verifier = Uri.parse(params[0]).getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
		 
		CommonsHttpOAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
		CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(params[1], params[2]);
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
    	myBG_results[0] =  httpOauthConsumer.getToken();
    	myBG_results[1] =  httpOauthConsumer.getTokenSecret();
        return myBG_results;
	}

}
