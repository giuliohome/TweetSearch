package org.new_year;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpClient;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveScopes;
import com.google.android.gms.drive.model.File;




public class new_yearActivity extends TabActivity {
	/** Called when the activity is first created. */


	static final int 				REQUEST_ACCOUNT_PICKER = 1;
	static final int 				REQUEST_AUTHORIZATION = 2;
	static final int 				RESULT_STORE_FILE = 4;
	private static Uri 				mFileUri;
	private static Drive 			mService;
	private GoogleAccountCredential mCredential;
	private Context 				mContext;
	private List<File> 				mResultList;
	private ListView 				mListView;
	private String[] 				mFileArray;
	private String 					mDLVal;
	private ArrayAdapter 			mAdapter;
	private static int				runCount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabHost tabHost = getTabHost();
		
		tabHost.clearAllTabs();
		

		// Tab for Searching
		TabSpec tweetsearch = tabHost.newTabSpec("Searching");
			
		// setting Title and Icon for the Tab
		tweetsearch.setIndicator("Searching", getResources().getDrawable(R.drawable.icon_search_tab));
		Intent tweetsearchIntent = new Intent(this, TweetSearchActivity.class);
		tweetsearch.setContent(tweetsearchIntent);
		
		// Tab for Searching
		TabSpec listsmgr = tabHost.newTabSpec("Lists");
					
		// setting Title and Icon for the Tab
		listsmgr.setIndicator("Lists", getResources().getDrawable(R.drawable.icon_list_tab));
		Intent listsmgrIntent = new Intent(this, ListsMgrActivity.class);
		listsmgr.setContent(listsmgrIntent);
				
		
		// Tab for Twitting
		TabSpec twitting = tabHost.newTabSpec("Twitting");
		twitting.setIndicator("Twitting", getResources().getDrawable(R.drawable.icon_twitter_tab));
		Intent twitterIntent = new Intent(this, twitterActivity.class);
		twitting.setContent(twitterIntent);
	    
		// Tab for Google Drive
		TabSpec googledrive = tabHost.newTabSpec("GDrive");
		googledrive.setIndicator("GDrive", getResources().getDrawable(R.drawable.icon_gdrive_tab));
		Intent driveIntent = new Intent(this, DriveActivity.class);
		googledrive.setContent(driveIntent);

		
		// Tab for Configuration
		TabSpec settings = tabHost.newTabSpec("Settings");
		settings.setIndicator("Setting", getResources().getDrawable(R.drawable.icon_conf_tab));
		Intent confIntent = new Intent(this, confActivity.class);
		settings.setContent(confIntent);

		// Adding all TabSpec to TabHost
		
		tabHost.addTab(tweetsearch); // Adding photos tab
		tabHost.addTab(listsmgr);
		tabHost.addTab(twitting); // Adding songs tab
		tabHost.addTab(googledrive);
		tabHost.addTab(settings); // Adding videos tab
		
		
		// mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
	     //   startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	        
	       // mContext = getApplicationContext();
		int appOrientationInt = getResources().getConfiguration().orientation;
		String appOrientation = ")";
		if (appOrientationInt == getResources().getConfiguration().ORIENTATION_LANDSCAPE)
			appOrientation = ") Landscape";
		if (appOrientationInt == getResources().getConfiguration().ORIENTATION_PORTRAIT)
			appOrientation = ") Portrait";
		
		Toast.makeText(getBaseContext(), "starting (".concat(Integer.toString(++runCount).concat(appOrientation)), Toast.LENGTH_LONG).show();

		Uri uri = getIntent().getData();
		String CALLBACKURL = "app://myDroid";
		if (uri != null  && uri.toString().startsWith(CALLBACKURL)) {
			Toast.makeText(getBaseContext(), "CallBackUrl !", Toast.LENGTH_LONG).show();

			

			try {
				String consumerKey=getString(R.string.consumerKey);
				String consumerSecret=getString(R.string.consumerSecret);
				SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String temp_userKey = settingsPref.getString("temp_user_key", "");
			    String temp_userSecret = settingsPref.getString("temp_user_secret", "");
				
			    
			    TokenAccess_BackGround myTask = new TokenAccess_BackGround();
				String[] params = new String[5];
				params[0] = consumerKey;
				params[1] = consumerSecret;
				params[2] = uri.toString();
				params[3] = temp_userKey;
				params[4] = temp_userSecret;
				String authUrl,userKey,userSecret;
				myTask.execute(params);
				try {

					String[] myBG_results = myTask.get();
					 userKey =  myBG_results[0];
					 userSecret =  myBG_results[1];
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Toast.makeText(getBaseContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
					return ;
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Toast.makeText(getBaseContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
					return ;
				}
				
				
				SharedPreferences.Editor editor = settingsPref.edit();
				editor.putString("user_key", userKey);
				editor.putString("user_secret", userSecret);
				editor.commit();
				//setContentView(ll);
				Toast.makeText(getBaseContext(), "Authorized! ", Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				Log.w("OpenTweetSearch - oauth fail: ", e.toString());
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}
	
	@Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) 
	{
		switch (requestCode) 
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null) 
				{
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						mCredential.setSelectedAccountName(accountName);
						mService = getDriveService(mCredential);
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					//account already picked
				} else {
					startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
				break;
			case RESULT_STORE_FILE:
				mFileUri = data.getData();
				// Save the file to Google Drive
        		saveFileToDrive();
				break;
		}
		
	    
	}
    
    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .build();
      }
	
    private void saveFileToDrive() 
    {
    	Thread t = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
				try 
				{
					// Create URI from real path
					String path;
					path = getPathFromUri(mFileUri);
					mFileUri = Uri.fromFile(new java.io.File(path));
					
					ContentResolver cR = new_yearActivity.this.getContentResolver();
					
					// File's binary content
					java.io.File fileContent = new java.io.File(mFileUri.getPath());
					FileContent mediaContent = new FileContent(cR.getType(mFileUri), fileContent);

					showToast("Selected " + mFileUri.getPath() + "to upload");

					// File's meta data. 
					File body = new File();
					body.setTitle(fileContent.getName());
					body.setMimeType(cR.getType(mFileUri));

					com.google.api.services.drive.Drive.Files f1 = mService.files();
					com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body, mediaContent);
					File file = i1.execute();
					
					if (file != null) 
					{
						showToast("Uploaded: " + file.getTitle());
					}
				} catch (UserRecoverableAuthIOException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				} catch (IOException e) {
					e.printStackTrace();
					showToast("Transfer ERROR: " + e.toString());
				}
    		}
    	});
    	t.start();
	}

	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}
    
	public String getPathFromUri(Uri uri) 
	{
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
	}    
	@Override
	protected void onNewIntent(Intent intent) {
		//super.onNewIntent(intent);
		Toast.makeText(getBaseContext(), "CallBack! ", Toast.LENGTH_LONG).show();

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


			String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

			try {
				// this will populate token and token_secret in consumer

				String consumerKey=getString(R.string.consumerKey);
				String consumerSecret=getString(R.string.consumerSecret);
				OAuthProvider httpOauthprovider = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token","https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
				CommonsHttpOAuthConsumer httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
				httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
				String userKey =  httpOauthConsumer.getToken();
				String userSecret =  httpOauthConsumer.getTokenSecret();

				// Save user_key and user_secret in user preferences and return
				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("user_key", userKey);
				editor.putString("user_secret", userSecret);
				editor.commit();
				//setContentView(ll);
				Toast.makeText(getBaseContext(), "Authorized! ", Toast.LENGTH_LONG).show();

			} catch (Exception e) {
				Log.w("OpenTweetSearch - oauth fail: ", e.toString());
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			}
		} else {
			// Do something if the callback comes from elsewhere
		}
	}

}
