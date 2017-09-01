package org.new_year;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.CookieManager;

public class confActivity extends Activity {
	
	
	//
	//CommonsHttpOAuthProvider instead of DefaultOAuthProvider
	

	private TextView tV;
	private LinearLayout ll;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ll = new LinearLayout(this); 
		ll.setOrientation( LinearLayout.VERTICAL );
		Button bPolicy = new Button(this);
		bPolicy.setText("Privacy Policy"); 
		bPolicy.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						Uri uri = Uri.parse("https://giuliohome.wordpress.com/2017/02/08/my-open-tweet-privacy-policy/"); 
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);		
					}
				} 
				); 
		ll.addView(bPolicy);
		final Button oauth = new Button(this);
		oauth.setText("authorise my app");
		ll.addView(oauth);
		final EditText myTok = new EditText(this);
        myTok.setHint("your access token (only for developer)");
        final EditText mySec = new EditText(this);
        mySec.setHint("your access secret (only for developer)");
        Button Moauth = new Button(this);
		Moauth.setText("Maual settings (only for developer)");
		ll.addView(myTok);
		ll.addView(mySec);
		ll.addView(Moauth);
		final Button bTwCk = new Button(this);
		bTwCk.setText("set twelephone cookie");
		final EditText twCookie = new EditText(this);
		twCookie.setHint("your twelephone token (if you have one)");
        ll.addView(bTwCk);
        ll.addView(twCookie);
        Button bGetMyLists = new Button(this);
		bGetMyLists.setText("get my lists"); 
		bGetMyLists.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if	(TweetSearchActivity.thisClass == null)
							return;
						ArrayList<String> res =	TweetSearchActivity.thisClass.get2MyLists();
						ListsMgrActivity.list_arr.clear();
						TweetSearchActivity.list_arr.clear();
						for (int i = 0; i < res.size(); i++) {
							TweetSearchActivity.list_arr.add(res.get(i));
							ListsMgrActivity.list_arr.add(res.get(i));
						}
						//TweetSearchActivity.list_arr = res;
						TweetSearchActivity.dataAdapter.notifyDataSetChanged();
						//ListsMgrActivity.list_arr = res;
						ListsMgrActivity.dataAdapter.notifyDataSetChanged();
						
					}
				} 
				
				
				); 
		ll.addView(bGetMyLists);
		
		
		
		tV = new TextView(this);
		tV.setText("");
		ll.addView(tV);
		
		setContentView(ll);
		Toast.makeText(getBaseContext(), "settings", Toast.LENGTH_LONG).show();
		
		bTwCk.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				SharedPreferences.Editor editor = settingsPref.edit();
				editor.putString("twelephone", twCookie.getText().toString());
				editor.commit();
				
			}
			
		});

		oauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Requesting auth...", Toast.LENGTH_LONG).show();
				
				oauth.setEnabled(false);
				
				try {
					String consumerKey=getString(R.string.consumerKey);
					String consumerSecret=getString(R.string.consumerSecret);
					
					
					callback_BackGround myTask = new callback_BackGround();
					String[] params = new String[2];
					params[0] = consumerKey;
					params[1] = consumerSecret;
					String authUrl,tempuserKey,tempuserSecret;
					myTask.execute(params);
					try {

						String[] myBG_results = myTask.get();
						 authUrl = myBG_results[2];				 
						 tempuserKey =  myBG_results[0];
							tempuserSecret =  myBG_results[1];
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Toast.makeText(v.getContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
						return ;
					} catch (ExecutionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						Toast.makeText(v.getContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
						return ;
					}
					
					// re-engineered for background tasks					

						//String tempuserParam =  httpOauthConsumer.getRequestParameters();
						
						SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
						SharedPreferences.Editor editor = settingsPref.edit();
						editor.putString("temp_user_key", tempuserKey);
						editor.putString("temp_user_secret", tempuserSecret);
						editor.commit();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					Log.w("OpenTweetSearch - oauth fail: ", e.toString());
					Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
				oauth.setEnabled(true);
				
			}
		});
		
		Moauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	            SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putString("user_key", myTok.getText().toString());
	            editor.putString("user_secret", mySec.getText().toString());
	            editor.commit();				
				
				Toast.makeText(v.getContext(), "Manual settings", Toast.LENGTH_LONG).show();
			}
		});		
		
		
	}
	@Override
	  protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    Log.w("redirect-to-app onNewIntent", "going to save the key and secret");
	    Toast.makeText(getBaseContext(), "getting authorised", Toast.LENGTH_LONG).show();
	    
		String CALLBACKURL = "app://myDroid";
		/*String consumerKey=getString(R.string.consumerKey);
		String consumerSecret=getString(R.string.consumerSecret);
		 OAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
		 CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		*/


	    Uri uri = intent.getData();
	    if (uri != null && uri.toString().startsWith(CALLBACKURL)) {
	    	

	        
	        try {
	            // this will populate token and token_secret in consumer
	 
	        	String consumerKey=getString(R.string.consumerKey);
	    		String consumerSecret=getString(R.string.consumerSecret);
	    		
	        	String userKey;
	            String userSecret;
	    		Authorising_BackGround myTask = new Authorising_BackGround();
	    		String[] params = new String[2];
	    		params[0] = uri.toString();
	    		params[1] = consumerKey;
	    		params[2] = consumerSecret;
	    		myTask.execute(params);
	    		try {
	    			String [] myBG_results =  myTask.get();
	    			userKey = myBG_results[0];
	    			userSecret = myBG_results[1];
	    		} catch (InterruptedException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    			return;
	    		}
	    		// re-engineered for background tasks
	    		/*
	    		CommonsHttpOAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
	    		CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
	        	httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
	        	String userKey =  httpOauthConsumer.getToken();
	            String userSecret =  httpOauthConsumer.getTokenSecret();
	            */
	 
	            // Save user_key and user_secret in user preferences and return
	            SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putString("user_key", userKey);
	            editor.putString("user_secret", userSecret);
	            editor.commit();
	            //setContentView(ll);
	            tV.setText("Authorized: "+userKey +" - " + userSecret);
	        } catch (Exception e) {
	        	Log.w("OpenTweetSearch - oauth fail: ", e.toString());
	        	tV.setText(e.toString());
	        	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	        }
	    } else {
	        // Do something if the callback comes from elsewhere
	    }
	  }
}