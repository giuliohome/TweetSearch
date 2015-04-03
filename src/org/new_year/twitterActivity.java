package org.new_year;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.http.HttpResponse;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class twitterActivity extends Activity {
	private EditText eResp;
	private EditText et, replyId, twPhone, et_mediaId;
	WebView webView;
	public static String mediaId="";
	public static String encodedImage=null;
    static final int REQUEST_AUTHORIZATION = 2;
	static final int RESULT_STORE_FILE = 4;	
	private static Uri mFileUri;

	public String UrlEncodeForOAuth(String value)
	{
	    value = URLEncoder.encode(value).replace("+", "%20");

	    // UrlEncode escapes with lowercase characters (e.g. %2f) but oAuth needs %2F
	    value = value.toUpperCase();

	    // these characters are not escaped by UrlEncode() but needed to be escaped
	    value = value.replace("(", "%28").replace(")", "%29").replace("$", "%24").replace("!", "%21").replace(
	        "*", "%2A").replace("'", "%27");

	    // these characters are escaped by UrlEncode() but will fail if unescaped!
	    value = value.replace("%7E", "~");

	    return value;
	}
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


		LinearLayout ll = new LinearLayout(this); 
		ll.setOrientation( LinearLayout.VERTICAL );
		et = new EditText(this);
		et.setHint("your tweet");
		int maxLength = 140;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		et.setFilters(FilterArray);
		Bundle myBundle = this.getIntent().getExtras();
		if (myBundle != null) {
			et.setText("@"+(String)myBundle.get("replyTo")+" ");
		}
		replyId = new EditText(this);
		replyId.setEnabled(false);
		replyId.setHint("reply to id");
		myBundle = this.getIntent().getExtras();
		if (myBundle != null) {
			replyId.setText((String)myBundle.get("replyStr"));
		}
		Button bRT = new Button(this);
		bRT.setText("retweet");
		bRT.setOnClickListener(handlerRT); 
		Button bFT = new Button(this);
		bFT.setText("favorite");
		bFT.setOnClickListener(handlerFT); 
		
		Button bT = new Button(this);
		bT.setText("send tweet");
		bT.setOnClickListener(handlerT); 
		eResp = new EditText(this);
		eResp.setHint("response");
		eResp.setEnabled(false);
		//twPhone = new EditText(this);
		//twPhone.setHint("twitter handle to call");
		Button bCall = new Button(this);
		bCall.setText("Twelephone's call");
		bCall.setOnClickListener(handlerCall); 
		ll.addView(et);
		
		et_mediaId = new EditText(this);
		et_mediaId.setEnabled(false);
		et_mediaId.setHint("photo id");
		Button bMT = new Button(this);
		bMT.setText("add a photo");
		bMT.setOnClickListener(handlerMT); 
		Button bRMT = new Button(this);
		bRMT.setText("remove");
		bRMT.setOnClickListener(handlerRMT); 
		
		if (myBundle != null) {	
				ll.addView(replyId);
				ll.addView(bRT);
				ll.addView(bFT);
			} else {
				LinearLayout mediall = new LinearLayout(this);
				mediall.setOrientation(LinearLayout.HORIZONTAL);
				ll.addView(et_mediaId);
				mediall.addView(bMT);	
				mediall.addView(bRMT);	
				ll.addView(mediall);
			}
		ll.addView(bT);
		//ll.addView(twPhone);
		ll.addView(bCall);
		ll.addView(eResp);
		//webView = new WebView(this);
		//ll.addView(webView);
		ScrollView sc = new ScrollView(this);
		sc.addView(ll);
		setContentView(sc);
	}
    
	private OnClickListener handlerRMT = new OnClickListener() {
		public void onClick(View v) {
			mediaId="";
			et_mediaId.setText("");
			encodedImage=null;
			eResp.setText("media removed");
		}
	} ;
	
    private String getPathFromUri(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        switch (requestCode)
        {
        
        case RESULT_STORE_FILE:
        	if (data == null)
        		return;
            mFileUri = data.getData();
            if (mFileUri==null)
            	return;
            // Save the file to Google Drive
            //saveFileToDrive();
            // Create URI from real path
            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
            String path;
            path = getPathFromUri(mFileUri);
            if (path==null)
            	return;
            mFileUri = Uri.fromFile(new java.io.File(path));
            //eResp.setText("photo added: "+mFileUri.getPath());
            Bitmap bm = BitmapFactory.decodeFile(mFileUri.getPath());
            if (bm == null) {
            	//eResp.setText("null photo's bitmap in: "+mFileUri.getPath());
            	return;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
            byte[] byteArrayImage = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            
                    } catch (Exception e) { 
        				Log.w("OpenTweetSearch - Photo add failed: ", e.toString());
        				//eResp.setText( "Photo add failed: "+ e.getMessage());
        			}
                }
            });
            
            try {
            	t.start();
            	t.join(10000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.w("OpenTweetSearch - Photo add interrupted: ", e.toString());
				eResp.setText( "Photo add interrupted: " +e.getMessage());
				break;
			}
            if (encodedImage==null) {
            	eResp.setText( "Photo not encoded ");
            	break;
            }
            eResp.setText("encoded photo: ok");
            
            try {
				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String userKey = settings.getString("user_key", "");
				String userSecret = settings.getString("user_secret", "");

				if	(userKey.isEmpty() || userSecret.isEmpty())
				{
					Toast.makeText(getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
					return;

				}

				String consumerKey = getString(R.string.consumerKey);
				String consumerSecret = getString(R.string.consumerSecret);
				byte[] utf8Bytes = (et.getText().toString()).getBytes("UTF-8");
				String status_par = new String(utf8Bytes, "UTF-8");
				String post_str = "https://upload.twitter.com/1.1/media/upload.json" ;
				twitter_BackGround myTask = new twitter_BackGround();
				String[] params = new String[6];
				params[1] = consumerKey;
				params[2] = consumerSecret;
				params[0] = post_str;
				params[3] = userKey;
				params[4] = userSecret;
				params[5]="media=?";
				String response;
				myTask.execute(params);
				try {

					response = myTask.get();
					 
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
				
				
				
				JSONObject array = new JSONObject(response);
				mediaId = array.getString("media_id_string");
				et_mediaId.setText(mediaId);					
				eResp.setText(array.toString());
				
				
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - Uploading Media failed: ", e.toString());
				eResp.setText("Uploading Media failed: "+ e.getMessage());
			}
            
            
            break;
    }
}
	
	private OnClickListener handlerMT = new OnClickListener() {
		public void onClick(View v) {
			eResp.setText("");
			try {
                final Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("*/*");
                startActivityForResult(galleryIntent, RESULT_STORE_FILE);				
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - Favorite failed: ", e.toString());
				eResp.setText( e.getMessage());
			}
		}
	} ;
	
	
	private OnClickListener handlerFT = new OnClickListener() {
		public void onClick(View v) {
			eResp.setText("");
			try {
				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String userKey = settings.getString("user_key", "");
				String userSecret = settings.getString("user_secret", "");

				if	(userKey.isEmpty() || userSecret.isEmpty())
				{
					Toast.makeText(v.getContext(), "no authorization", Toast.LENGTH_LONG).show();
					return;

				}

				String consumerKey = getString(R.string.consumerKey);
				String consumerSecret = getString(R.string.consumerSecret);
				String reply_id = replyId.getText().toString();
				String post_str = "https://api.twitter.com/1.1/favorites/create.json" ;
				twitter_BackGround myTask = new twitter_BackGround();
				String[] params = new String[6];
				params[1] = consumerKey;
				params[2] = consumerSecret;
				params[0] = post_str;
				params[3] = userKey;
				params[4] = userSecret;
				params[5] = "id="+reply_id;
				String response;
				myTask.execute(params);
				try {

					response = myTask.get();
					 
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
				
				
				
				JSONObject array = new JSONObject(response);
				eResp.setText(array.toString());
				
				
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - Favorite failed: ", e.toString());
				eResp.setText( e.getMessage());
			}
		}
	} ;
	
	
	
	private OnClickListener handlerRT = new OnClickListener() {
		public void onClick(View v) {
			eResp.setText("");
			try {
				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String userKey = settings.getString("user_key", "");
				String userSecret = settings.getString("user_secret", "");

				if	(userKey.isEmpty() || userSecret.isEmpty())
				{
					Toast.makeText(v.getContext(), "no authorization", Toast.LENGTH_LONG).show();
					return;

				}

				String consumerKey = getString(R.string.consumerKey);
				String consumerSecret = getString(R.string.consumerSecret);
				String reply_id = replyId.getText().toString();
				String post_str = "https://api.twitter.com/1.1/statuses/retweet/"+reply_id+".json" ;
				twitter_BackGround myTask = new twitter_BackGround();
				String[] params = new String[5];
				params[1] = consumerKey;
				params[2] = consumerSecret;
				params[0] = post_str;
				params[3] = userKey;
				params[4] = userSecret;
				String response;
				myTask.execute(params);
				try {

					response = myTask.get();
					 
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
				
				
				
				JSONObject array = new JSONObject(response);
				eResp.setText(array.toString());
				
				
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - Retweet failed: ", e.toString());
				eResp.setText( e.getMessage());
			}
		}
	} ;
	
	private OnClickListener handlerT = new OnClickListener() {
		public void onClick(View v) {
			eResp.setText("");


			try {

				//HttpParams params = new BasicHttpParams();
				//post.setHeader("status", et.getText().toString());


				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String userKey = settings.getString("user_key", "");
				String userSecret = settings.getString("user_secret", "");

				if	(userKey.isEmpty() || userSecret.isEmpty())
				{
					Toast.makeText(v.getContext(), "no authorization", Toast.LENGTH_LONG).show();
					return;

				}

				String consumerKey = getString(R.string.consumerKey);
				String consumerSecret = getString(R.string.consumerSecret);
				String post_str = "https://api.twitter.com/1.1/statuses/update.json" ; //?status="
						//+ et.getText().toString().replace(" ", "%20").replace("/", "%2F").replace("#", "%23").replace("=", "%3D").replace("\"", "%22")
										//+"&in_reply_to_status_id="+replyId.getText().toString();
						

				
				twitter_BackGround myTask = new twitter_BackGround();
				String reply_id = replyId.getText().toString();
				byte[] utf8Bytes = (et.getText().toString()).getBytes("UTF-8");
				String status_par = new String(utf8Bytes, "UTF-8");
				//String status_par = et.getText().toString();//.replace(" ", "%20").replace("/", "%2F").replace("#", "%23").replace("=", "%3D").replace("\"", "%22");
				//status_par = UrlEncodeForOAuth(status_par);
				String[] params;
				if	(reply_id.length()>0)
				{
					params = new String[7];
					params[5]=status_par;
					params[6]=reply_id;
				} else {
					params = new String[6];
					params[5]="status="+status_par;
				}
				params[1] = consumerKey;
				params[2] = consumerSecret;
				params[0] = post_str;
				params[3] = userKey;
				params[4] = userSecret;
				String authUrl,response;
				myTask.execute(params);
				try {

					response = myTask.get();
					 
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
				
				
				
				JSONObject array = new JSONObject(response);
				eResp.setText(array.toString());
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - oauth fail: ", e.toString());
				eResp.setText( e.getMessage());
				//Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				// handle this somehow
			}



		}
	} ;


	private OnClickListener handlerCall = new OnClickListener() {
		public void onClick(View v) {
			eResp.setText("");
			
		/*try {	
			if (twPhone==null) {
				Toast.makeText(getBaseContext(), "Enter twPhone", Toast.LENGTH_SHORT).show();
				return;
			}
			Uri uri = Uri.parse( "http://api.twelephone.com/voice/" 
			+ twPhone.getText().toString() );
	startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
		} catch (ActivityNotFoundException e) {
            // Raise on activity not found
             Toast.makeText(getBaseContext(), "Browser not found.", Toast.LENGTH_SHORT).show();
          }*/

			try {
				
				
						

						//SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
						//String userKey = settings.getString("twelephone", "");
						//String userKey =  getString(R.string.twelephone);
						SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
						
						String userKey = settingsPref.getString("twelephone","");
						
						if	(userKey.isEmpty() )
						{
							Toast.makeText(v.getContext(), "no authorization", Toast.LENGTH_LONG).show();
							return;

						}

						//String consumerKey = getString(R.string.consumerKey);
						//String consumerSecret = getString(R.string.consumerSecret);

						
						//CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
						//httpOauthConsumer.setTokenWithSecret(userKey, userSecret);
						//httpOauthConsumer.sign(get);

						HttpGet get = new HttpGet("http://api.twelephone.com" 
								+"/calls/" + userKey);
								//+"/voice/" 
								//+ twPhone.getText().toString() );

						//httpOauthConsumer.sign(get);

						
						DefaultHttpClient client = new DefaultHttpClient();
						String response = client.execute(get, new BasicResponseHandler());
						//webView.loadData(response, "text/html", null);
						JSONObject array = new JSONObject(response);
						JSONArray inbCalls = array.getJSONArray("inboundlist");
						String myCalls="";
						for (int i = 0; i < inbCalls.length(); i++) {
							JSONObject call = inbCalls.getJSONObject(i);
							myCalls=myCalls+String.valueOf(i)+": "+ call.getString("username")+"\n";
						}
						eResp.setText(myCalls);

			} catch (Exception e) { 
				Log.w("OpenTweetSearch - oauth fail: ", e.toString());
				eResp.setText( e.getMessage());

			} 



		}
	} ;

}
