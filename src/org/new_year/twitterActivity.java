package org.new_year;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class twitterActivity extends Activity {
	private EditText eResp;
	private EditText et, replyId, replyText, twPhone, et_mediaId, showId;
	private LinearLayout tw_res;
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
	        "*", "%2A").replace("'", "%27").replace("\"", "%22");

	    // these characters are escaped by UrlEncode() but will fail if unescaped!
	    value = value.replace("%7E", "~");

	    return value;
	}
	
	String sharedText = null;
	Uri imageUri = null;
	
	void handleSendText(Intent intent) {
	    sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    //if (sharedText != null) {
	        // Update UI to reflect text being shared
	    //}
	}

	void handleSendImage(Intent intent) {
	    imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    //if (imageUri != null) {
	    //    onActivityResult(RESULT_STORE_FILE,0,null);
	    //}
	}
	
	@Override
	public void onStop() {
		super.onStop();  // Always call the superclass method first
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("et_text", et.getText().toString());
		editor.commit();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		sharedText = sharedPreferences.getString("et_text", "");

	    // Get intent, action and MIME type
	    Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	            handleSendText(intent); // Handle text being sent
	        } else if (type.startsWith("image/")) {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    } 
		

		LinearLayout ll = new LinearLayout(this); 
		ll.setOrientation( LinearLayout.VERTICAL );
		et = new EditText(this);
		et.setHint("your tweet");
		int maxLength = 140 + 100;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		et.setFilters(FilterArray);
		Bundle myBundle = this.getIntent().getExtras();
		if (sharedText != null) {
			et.setText(sharedText);
			sharedText = null;
		}
		if (myBundle != null) {
			et.setText("@"+(String)myBundle.get("replyTo")+" ");
		}
		replyId = new EditText(this);
		replyId.setEnabled(false);
		replyId.setHint("reply to id");
		replyText = new EditText(this);
		replyText.setEnabled(false);
		replyText.setHint("reply to text");
		myBundle = this.getIntent().getExtras();
		if (myBundle != null) {
			replyId.setText((String)myBundle.get("replyStr"));
			replyText.setText((String)myBundle.get("replyTweet"));
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
				ll.addView(replyText);
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
		LinearLayout llID = new LinearLayout(this);
		llID.setOrientation(LinearLayout.HORIZONTAL);
		Button bID = new Button(this);
		bID.setText("Show id:");
		bID.setOnClickListener(handlerShowID); 
		llID.addView(bID);
		ll.addView(llID);
		tw_res = new LinearLayout(this);
		tw_res.setOrientation(LinearLayout.VERTICAL);
		ll.addView(tw_res);
		showId = new EditText(this);
		showId.setHint("tweet status id");
		llID.addView(showId);
		ll.addView(eResp);
		//webView = new WebView(this);
		//ll.addView(webView);
		ScrollView sc = new ScrollView(this);
		sc.addView(ll);
		setContentView(sc);
		
		if (imageUri != null) {
	        onActivityResult(RESULT_STORE_FILE,0,null);
	    }
		
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
        	if (data == null && imageUri == null )
        		return;
        	if	(imageUri != null) {
        		mFileUri = imageUri;
        		imageUri = null;
        	} else {
        		mFileUri = data.getData();
        	}
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
				Log.w("OpenTweetSearch - Gallery intent failed: ", e.toString());
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
	
	private void showTweet(String response) {
		tw_res.removeAllViews();
		final Tweet item;
		try	{
			JSONObject jsonObject = new JSONObject(response);
			if ( jsonObject != null ) { 
				//Log.i("new_year_ok", jsonObject.getString("text"));
				Long replyId = null;
				if	(!jsonObject.isNull("in_reply_to_status_id")) {
					replyId = jsonObject.getLong("in_reply_to_status_id");
				}
				item = new Tweet( 
						jsonObject.getJSONObject("user").getString("name"),
						jsonObject.getJSONObject("user").getString("screen_name"),
						jsonObject.getString("text"),
						jsonObject.getString("created_at"),
						jsonObject.getString("source"),
						jsonObject.getJSONObject("user").getString("profile_image_url"),
						jsonObject.getLong("id"), replyId,
						jsonObject.getJSONObject("entities").getJSONArray("urls"),
						jsonObject.getJSONObject("entities").isNull("media")?null:jsonObject.getJSONObject("entities").getJSONArray("media")
						);
			} else {
				eResp.setText(response);
				return;
			}
		} catch (Exception e) {
			eResp.setText(e.getMessage());
			return;
		}
		EditText newtweet = new EditText(this);
		newtweet.setBackgroundColor(Color.CYAN);
		newtweet.setTextColor(Color.BLACK);
		String source_str = TweetSearchActivity.extract_source(item.source);
		String ReplID="";
		if (item.reply_id != null) {
			ReplID=" > " + String.valueOf(item.reply_id);
		}
		newtweet.setText(
				item.username +"("+ item.screen_name +")"+ String.valueOf(item.id) 
						+ ReplID + ": " 
						+ item.message+
						" - "+item.date.replace("+0000", "") + " - " + 
						source_str
						+ "\n");
		final Button opSw = new Button(this);
		opSw.setText("open swipe");
		final WrappingSlidingPaneLayout swipeL = new WrappingSlidingPaneLayout(this);
		LinearLayout .LayoutParams SlayoutParams= new 
				LinearLayout .LayoutParams ( LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
		swipeL.setLayoutParams(SlayoutParams);
		
		LinearLayout detailL = new LinearLayout(this);
		detailL.setOrientation(LinearLayout.VERTICAL);
		swipeL.addView(detailL);
		swipeL.addView(newtweet);
		ImageView newprofile = new ImageView(this);
		TweetSearchActivity.loadBitmap(newprofile,item.profile);
		tw_res.addView(newprofile);
		tw_res.addView(opSw); 
		tw_res.addView(swipeL);
		opSw.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (opSw.getText().toString().equals("open swipe")) {
					swipeL.openPane();
					opSw.setText("close swipe");
					return;
				}
				if (opSw.getText().toString().equals("close swipe")) {
					swipeL.closePane();
					opSw.setText("open swipe");
				}
			}
		}); 
		for(final String url : item.urls)
		{
			if (url.startsWith("media_url:"))
			{
				ImageView newphoto = new ImageView(this);
				tw_res.addView(newphoto);
				OpenStreamBG openStr = new OpenStreamBG();
	            openStr.execute(new Object[] { url.replaceFirst("media_url:", ""), newphoto });
			}
			else {
				Button bt_link = new Button(this);
				bt_link.setText(url.replaceFirst("media_url:", ""));
				tw_res.addView(bt_link);
				bt_link.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Uri uri =  Uri.parse(((Button)v).getText().toString()); 
						Intent intent = new Intent(Intent.ACTION_VIEW,uri);
						try {
		                      // Start the activity
		                      startActivity(intent);
		                    } catch (ActivityNotFoundException e) {
		                      // Raise on activity not found
		                      Toast.makeText(getBaseContext(), "Browser not found.", Toast.LENGTH_SHORT).show();
		                    }
					}
				});
			}
		}
		Button bt_link = new Button(this);
		bt_link.setBackgroundColor(Color.YELLOW);
		bt_link.setTextColor(Color.BLACK);
		bt_link.setText("reply to " + item.screen_name);
		detailL.addView(bt_link);
		bt_link.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent twitterActivityIntent = new Intent(getParent(), twitterActivity.class);
				twitterActivityIntent.putExtra("replyStr", String.valueOf(item.id));
				twitterActivityIntent.putExtra("replyTo", item.screen_name);
				twitterActivityIntent.putExtra("replyTweet", item.message);
				TabActivity parentActivity = (TabActivity)getParent();
				parentActivity.startActivity(twitterActivityIntent);
			}
		});
		bt_link = new Button(this);
		bt_link.setBackgroundColor(Color.CYAN);
		bt_link.setTextColor(Color.BLACK);
		bt_link.setText("Translate ");
		detailL.addView(bt_link);
		bt_link.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent translateActivityIntent = new Intent(getParent(), TranslateActivity.class);
				translateActivityIntent.putExtra("originalTweet", item.message);
				startActivity(translateActivityIntent);
			}
		});
		
	}
	private OnClickListener handlerShowID = new OnClickListener() {
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
				String show_id = showId.getText().toString();
				String searchUrl = "https://api.twitter.com/1.1/statuses/show.json?id="+show_id ;
				OAUTHreadTwitterFeed_BackGround myTask = new OAUTHreadTwitterFeed_BackGround();
				String[] params = new String[5];
				params[1] = consumerKey;
				params[2] = consumerSecret;
				params[0] = searchUrl;
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
				
				
				showTweet( response );
				//JSONObject array = new JSONObject(response);
				//eResp.setText(array.toString());
				
				
			} catch (Exception e) { 
				Log.w("OpenTweetSearch - Retweet failed: ", e.toString());
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
