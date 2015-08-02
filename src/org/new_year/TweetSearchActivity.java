package org.new_year;

import android.app.Activity;
import android.app.TabActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
//import android.widget.TextView;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
//import android.widget.RlativeLayout;
import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.LinearLayout; 
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View; 
import android.view.View.OnClickListener; 
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
//import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
//import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.HttpClient;

import android.support.v4.widget.SlidingPaneLayout;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log; 
//import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
/*import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;*/
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TweetSearchActivity extends Activity {


	private EditText eT ;
	private EditText myName;//, myList;
	private Spinner comboList;
	public static List<String> list_arr;
	public static ArrayAdapter<String> dataAdapter;
	private LinearLayout tw_res;
	CheckBox checkIcon;
	//private EditText hello; 
	
	public static ArrayList<Tweet> mytweets = null;
	public static Boolean skipHome = false;
	public static TweetSearchActivity thisClass = null; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		eT = new EditText(this);
		eT.setHint(R.string.initq);
		myName = new EditText(this);
		myName.setHint(R.string.username); 
		Button bR = new Button(this);
		bR.setText("recent");
		bR.setOnClickListener(handlerR); 
		Button bP = new Button(this);
		bP.setText("popular");
		bP.setOnClickListener(handlerP); 
		Button bM = new Button(this);
		bM.setText("mixed"); 
		bM.setOnClickListener(handlerM); 
		Button bT = new Button(this);
		bT.setText("tweets"); 
		bT.setOnClickListener(handlerT); 
		Button bMent = new Button(this);
		bMent.setText("@"); 
		bMent.setOnClickListener(handlerMent); 
		Button bFav = new Button(this);
		bFav.setText("*"); 
		bFav.setOnClickListener(handlerFav); 
		Button bFollow = new Button(this);
		bFollow.setText("follow"); 
		bFollow.setOnClickListener(handlerFollow); 
		Button bUnFollow = new Button(this);
		bUnFollow.setText("unf"); 
		bUnFollow.setOnClickListener(handlerUnFollow); 
		Button bHome = new Button(this);
		bHome.setText("home view"); 
		bHome.setOnClickListener(handlerHome); 
		//myList = new EditText(this);
		//myList.setHint("enter list");
		comboList = new Spinner(this);
		list_arr = new ArrayList<String>();
		ListsMgrActivity.list_arr = new ArrayList<String>();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String my_lists_pref = settings.getString("mylists", "--");

		for (String curr_list : my_lists_pref.split(",") )  
	      {  
	         list_arr.add(curr_list);
	         ListsMgrActivity.list_arr.add(curr_list);
	      }  
		
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_arr);
		ListsMgrActivity.dataAdapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, ListsMgrActivity.list_arr);
		comboList.setAdapter(dataAdapter);	
		
				
		
		Button bList = new Button(this);
		bList.setText("view list"); 
		bList.setOnClickListener(handlerList); 
		
		ScrollView sc = new ScrollView(this);
		LinearLayout llb = new LinearLayout(this);
		LinearLayout llb1 = new LinearLayout(this);
		LinearLayout llb2 = new LinearLayout(this);
		LinearLayout ll = new LinearLayout(this); 
		ll.setOrientation( LinearLayout.VERTICAL );
		ll.addView(eT); 
		ll.addView(llb);
		ll.addView(myName); 
		ll.addView(llb1);
		ll.addView(llb2);

		LinearLayout llicon = new LinearLayout(this);
		checkIcon = new CheckBox(this);
		checkIcon.setChecked(false);
		TextView iconView = new TextView(this);
		iconView.setText("load icons");
		//Button bGetLists = new Button(this);
		//bGetLists.setText("get lists"); 
		//bGetLists.setOnClickListener(handlerGetLists); 
		llicon.addView(checkIcon);
		llicon.addView(iconView);
		//llicon.addView(bGetLists);
		//llicon.addView(bGetMyLists);
		ll.addView(llicon);

		llb.addView(bR);
		llb.addView(bP); 
		llb.addView(bM); 
		llb1.addView(bT);
		llb1.addView(bMent);
		llb1.addView(bFav);
		llb1.addView(bFollow);
		llb1.addView(bUnFollow);
		
		llb2.addView(bHome);
		llb2.addView(bList); 
		llb2.addView(comboList);//myList); 
		
		EditText hello = new EditText(this);
		hello.setText(R.string.hello);

		tw_res = new LinearLayout(this);
		tw_res.setOrientation(LinearLayout.VERTICAL);
		tw_res.addView(hello);
		//sc.addView(hello);
		//ll.addView(sc);
		//setContentView(ll);
		ll.addView(tw_res);
		sc.addView(ll);
		setContentView(sc);
		
		String tweetsSerial = "";
		if (mytweets == null)
		{
			tweetsSerial = settings.getString("tweets", "");
			if	(!tweetsSerial.isEmpty() )
			{
				mytweets = (ArrayList<Tweet>)stringToObject(tweetsSerial);
			}
		}
		
			
		if (mytweets != null)
		{
			tw_res.removeAllViews();
			tweetHome(tw_res ) ;
		}
		checkIcon.setChecked(true);
		
		thisClass = this;
	}
	
	public void show_tweets()
	{
		if (mytweets != null)
		{
			tw_res.removeAllViews();
			tweetHome(tw_res ) ;
		}		
	}

	private OnClickListener handlerR = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText(""); 

			tweetGet(tw_res,eT.getText().toString().replace(" ","%20").replace("#", "%23").replace("@", "%40")
					, "recent"); 
		}
	} ;
	private OnClickListener handlerP = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			tweetGet(tw_res, eT.getText().toString().replace(" ","%20").replace("#", "%23").replace("@", "%40") 
					, "popular"); 
		}
	} ;
	private OnClickListener handlerM = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText(""); 
			tweetGet(tw_res, eT.getText().toString().replace(" ","%20").replace("#", "%23").replace("@", "%40")
					,"mixed"); 
		}
	} ;
	
	private OnClickListener handlerT = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText(""); 
			tweet2Get(tw_res,myName.getText().toString().replace(" ","").replace("@","") ) ; 
		}
	} ;  

	private OnClickListener handlerMent = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			String name_to_search=myName.getText().toString().replace(" ","").replace("@","");//.replace("@", "%40")
			if	(name_to_search.equals(""))
			{
				mention2Get(tw_res, "") ; // my mentions
			} else {
				tweetGet(tw_res, "%40"+name_to_search+"%20-RT"
						,"mixed"); 
			}
			 
		}
	} ;  


	private OnClickListener handlerFav = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			String name_to_search=myName.getText().toString().replace(" ","").replace("@","");//.replace("@", "%40")
			favorite2Get(tw_res, name_to_search) ; // my mentions
			 
		}
	} ;  

	
	
	/*private OnClickListener handlerMyMent = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			//String name_to_search=myName.getText().toString().replace(" ","").replace("@","");//.replace("@", "%40")
			mention2Get(tw_res, "") ; // my mentions
			 
		}
	} ;  */

	private OnClickListener handlerFollow = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			String name_to_search=myName.getText().toString().replace(" ","").replace("@","");//.replace("@", "%40")
			follow2Get(tw_res, name_to_search) ; // my mentions
			 
		}
	} ;
	private OnClickListener handlerUnFollow = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			//hello.setText("");
			String name_to_search=myName.getText().toString().replace(" ","").replace("@","");//.replace("@", "%40")
			unfollow2Get(tw_res, name_to_search) ; // my mentions
			 
		}
	} ;
	/* private OnClickListener handlerGetLists = new OnClickListener() {
		public void onClick(View v) {
			list_arr.clear();
			ArrayList<String> res =	get2Lists(myName.getText().toString(). replace(" ","").replace("@",""));
			for (int i = 0; i < res.size(); i++) {
				list_arr.add(res.get(i));
			}
			
		}
	} ;  	*/

	
	private OnClickListener handlerHome = new OnClickListener() {
		public void onClick(View v) {
			if	(!skipHome)
			{
				mytweets = null;
			} 
			tw_res.removeAllViews();
			//hello.setText(""); 
			tweetHome(tw_res ) ; 
			if (skipHome) {
				skipHome = false;
			}
		}
	} ;  
	

	private OnClickListener handlerList = new OnClickListener() {
		public void onClick(View v) {
			tw_res.removeAllViews();
			if (comboList.getSelectedItem() == null) {
			    
				return;
			} 
			String add_screen_name=myName.getText().toString().trim().replace(" ","").replace("@","");
			if (add_screen_name.length()>0)
			{
			//hello.setText(""); 
			list2Get(tw_res,comboList.getSelectedItem().toString(). replace(" ","").replace("#", "%23") +
					"&owner_screen_name="+myName.getText().toString().replace(" ","").replace("@","")) ;
			} else {
				SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
				String my_owner = settings.getString("owner", "");
				list2Get(tw_res,comboList.getSelectedItem().toString(). replace(" ","").replace("#", "%23") +
						"&owner_screen_name="+my_owner.replace(" ","").replace("@","")) ;
			}
		}
	} ;  


	
	private void tweetGet(final LinearLayout tw_res, String searchTerm , String method)
	{
		// Log.e("new_year_info", "Starting");

		int id = 0;
		int id_link=0;


		//ArrayList<Tweet> 
		mytweets = this.getTweets(searchTerm,method);//"giuliohome");

		//Log.e("new_year_ko", "tweets retrieved"); 
		if ( mytweets == null) {return;}; 
		
		SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		SharedPreferences.Editor editor_tweets = settingsPref.edit();
		String tweetSerial = objectToString(mytweets);
		editor_tweets.putString("tweets",tweetSerial);
		editor_tweets.commit();
		
		
		EditText[] newtweets = new EditText[mytweets.size()];

		for(final Tweet item : mytweets ){
			newtweets[id] = new EditText(this);
			newtweets[id].setId(id);
			String source_str = extract_source2(item.source);
			newtweets[id].setText(
					item.username+": " 
							+	item.message+" - "+item.date.replace("+0000", "") + " - " + 
							source_str
							+ "\n");
			if (id % 2 == 0) {
				newtweets[id].setBackgroundColor(Color.YELLOW);
			} else {
				newtweets[id].setBackgroundColor(Color.CYAN);
			}
			newtweets[id].setTextColor(Color.BLACK);
			LinearLayout swpL = new LinearLayout(this);
			swpL.setOrientation(LinearLayout.HORIZONTAL);
			
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
			swipeL.addView(newtweets[id]);
			
			
			OnClickListener openSW = new OnClickListener() {
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
			}; 
			opSw.setOnClickListener( openSW ); 
			
			if (checkIcon.isChecked()) {
				ImageView newprofile = new ImageView(this);
				newprofile.setId(id);
				loadBitmap(newprofile,item.profile);
				tw_res.addView(newprofile);
			}
			tw_res.addView(opSw);
			tw_res.addView(swipeL);
			for(final String url : item.urls)
			{
				if (checkIcon.isChecked() && url.startsWith("media_url:"))
				{
					
						WebView newphoto = new WebView(this);
						newphoto.setId(id_link);
						tw_res.addView(newphoto);
						//loadBitmap(newphoto,url);
						newphoto.setWebViewClient(new WebViewClient());
						newphoto.loadUrl(url.replaceFirst("media_url:", ""));
						newphoto.getLayoutParams().height= LayoutParams.WRAP_CONTENT;
						id_link++;	
				}
				else {
					
				
				Button bt_link = new Button(this);
				bt_link.setId(id_link);
				bt_link.setText(url.replaceFirst("media_url:", ""));
				tw_res.addView(bt_link);
				id_link++;
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
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("reply to " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent twitterActivityIntent = new Intent(getParent(), twitterActivity.class);
					twitterActivityIntent.putExtra("replyStr", String.valueOf(item.id));
					twitterActivityIntent.putExtra("replyTo", item.screen_name);
					TabActivity parentActivity = (TabActivity)getParent();
					parentActivity.startActivity(twitterActivityIntent);
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 1) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("tweets of " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TO DO tweets of
					//myName.setText(item.screen_name);
					tw_res.removeAllViews();
					//hello.setText(""); 
					tweet2Get(tw_res,item.screen_name.replace(" ","").replace("@","") ) ; 
					
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("Translate ");
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent translateActivityIntent = new Intent(getParent(), TranslateActivity.class);
					translateActivityIntent.putExtra("originalTweet", item.message);
					startActivity(translateActivityIntent);
				}
			});
			id++;

			//Log.e("new_year_message", item.message);
			//setContentView(hello);
		}

		//    Log.e("new_year_info", "Done"); 
	} 

	private String extract_source(String source)
	{
		int source1=source.indexOf(">")+1;
		int source2= source.indexOf("</a");
		String source_str;
		if (source1>=0 && source2 >= source1)
		{
			source_str = source.substring(source1,source2);
		} else {
			source_str=source;
		}
		return source_str;
	}
	private String extract_source2(String source)
	{
		int source1=source.indexOf("&gt;")+4;
		int source2= source.indexOf("&lt;/a");
		String source_str;
		if (source1>=0 && source2 >= source1)
		{
			source_str = source.substring(source1,source2);
		} else {
			source_str=source;
		}
		return source_str;
	}
	
	private void tweet2Get(LinearLayout tw_res, String searchTerm)
	{
		command2Get(tw_res, searchTerm, MyEnum.TWEET);
	}
	private void favorite2Get(LinearLayout tw_res, String searchTerm)
	{
		command2Get(tw_res, searchTerm, MyEnum.FAVORITES);
	}
	private void mention2Get(LinearLayout tw_res, String searchTerm)
	{
		command2Get(tw_res, searchTerm, MyEnum.MENTION);
	}
	private void follow2Get(LinearLayout tw_res, String searchTerm)
	{
		command2Get(tw_res, searchTerm, MyEnum.FOLLOW);
	}
	private void unfollow2Get(LinearLayout tw_res, String searchTerm)
	{
		command2Get(tw_res, searchTerm, MyEnum.UNFOLLOW);
	}
	private void command2Get(final LinearLayout tw_res, String searchTerm, MyEnum command)
	{
		// Log.e("new_year_info", "Starting");

		int id = 0;
		int id_link=0;

		//ArrayList<Tweet> 
		mytweets = this.get2Tweets(searchTerm, command);//"giuliohome");
		
		if	(mytweets != null) {
			SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
			SharedPreferences.Editor editor_tweets = settingsPref.edit();
			String tweetSerial = objectToString(mytweets);
			editor_tweets.putString("tweets",tweetSerial);
			editor_tweets.commit();
		}
		
		for(final Tweet item : mytweets ){
			EditText newtweet = new EditText(this);
			newtweet.setId(id);
			if (id % 2 == 0) {
				newtweet.setBackgroundColor(Color.YELLOW);
			} else {
				newtweet.setBackgroundColor(Color.CYAN);
			}
			newtweet.setTextColor(Color.BLACK);
			
			String source_str = extract_source(item.source);
			newtweet.setText(
					item.username +"("+ item.screen_name +"): " 
							+ item.message+
							" - "+item.date.replace("+0000", "") + " - " + 
							source_str
							+ "\n");
			final Button opSw = new Button(this);
			opSw.setText("open swipe");
			final WrappingSlidingPaneLayout swipeL = new WrappingSlidingPaneLayout(this);
			//LinearLayout mainL = new LinearLayout(this);
			//mainL.setOrientation(LinearLayout.VERTICAL);
			
			LinearLayout .LayoutParams SlayoutParams= new 
					LinearLayout .LayoutParams ( LinearLayout.LayoutParams.MATCH_PARENT, 
							LinearLayout.LayoutParams.WRAP_CONTENT);
			swipeL.setLayoutParams(SlayoutParams);
			
			//mainL.setLayoutParams(layoutParams);
			//mainL.addView(newtweet);
			LinearLayout detailL = new LinearLayout(this);
			detailL.setOrientation(LinearLayout.VERTICAL);
			swipeL.addView(detailL);
			swipeL.addView(newtweet);
			if (checkIcon.isChecked()) {
				ImageView newprofile = new ImageView(this);
				newprofile.setId(id);
				loadBitmap(newprofile,item.profile);
				tw_res.addView(newprofile);
			}
			tw_res.addView(opSw);
			tw_res.addView( swipeL ) ; //newtweet);
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
				if (checkIcon.isChecked() && url.startsWith("media_url:"))
				{
					
						WebView newphoto = new WebView(this);
						newphoto.setId(id_link);
						tw_res.addView(newphoto);
						//loadBitmap(newphoto,url);
						newphoto.setWebViewClient(new WebViewClient());
						newphoto.loadUrl(url.replaceFirst("media_url:", ""));
						newphoto.getLayoutParams().height= LayoutParams.WRAP_CONTENT;
						id_link++;	
				}
				else {
					
				
				Button bt_link = new Button(this);
				bt_link.setId(id_link);
				bt_link.setText(url.replaceFirst("media_url:", ""));
				tw_res.addView(bt_link);
				id_link++;
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
			//replacing tw_res with detailL - start
			Button bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("reply to " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent twitterActivityIntent = new Intent(getParent(), twitterActivity.class);
					twitterActivityIntent.putExtra("replyStr", String.valueOf(item.id));
					twitterActivityIntent.putExtra("replyTo", item.screen_name);
					TabActivity parentActivity = (TabActivity)getParent();
					parentActivity.startActivity(twitterActivityIntent);
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 1) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("tweets of " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TO DO tweets of
					//myName.setText(item.screen_name);
					tw_res.removeAllViews();
					//hello.setText(""); 
					tweet2Get(tw_res,item.screen_name.replace(" ","").replace("@","") ) ; 
					
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("Translate ");
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent translateActivityIntent = new Intent(getParent(), TranslateActivity.class);
					translateActivityIntent.putExtra("originalTweet", item.message);
					startActivity(translateActivityIntent);
				}
			});
			id++;
			//replacing tw_res with detailL - end
			//Log.e("new_year_message", item.message);
			//setContentView(hello);
		}

		// Log.e("new_year_info", "Done"); 
	} 
	

	private void list2Get(final LinearLayout tw_res, String searchTerm)
	{
		// Log.e("new_year_info", "Starting");

		int id = 0;
		int id_link=0;

		//ArrayList<Tweet> 
		mytweets = this.list2Tweets(searchTerm);//"giuliohome");
		
		if	(mytweets != null) {
			SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
			SharedPreferences.Editor editor_tweets = settingsPref.edit();
			String tweetSerial = objectToString(mytweets);
			editor_tweets.putString("tweets",tweetSerial);
			editor_tweets.commit();
		}
		
		for(final Tweet item : mytweets ){
			EditText newtweet = new EditText(this);
			newtweet.setId(id);
			if (id % 2 == 0) {
				newtweet.setBackgroundColor(Color.YELLOW);
			} else {
				newtweet.setBackgroundColor(Color.CYAN);
			}
			newtweet.setTextColor(Color.BLACK);
			
			String source_str = extract_source(item.source);
			newtweet.setText(
					item.username +"("+ item.screen_name +"): " 
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
			if (checkIcon.isChecked()) {
				ImageView newprofile = new ImageView(this);
				newprofile.setId(id);
				loadBitmap(newprofile,item.profile);
				tw_res.addView(newprofile);
			}
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
				if (checkIcon.isChecked() && url.startsWith("media_url:"))
				{
					
						WebView newphoto = new WebView(this);
						newphoto.setId(id_link);
						tw_res.addView(newphoto);
						//loadBitmap(newphoto,url);
						newphoto.setWebViewClient(new WebViewClient());
						newphoto.loadUrl(url.replaceFirst("media_url:", ""));
						newphoto.getLayoutParams().height= LayoutParams.WRAP_CONTENT;
						id_link++;	
				}
				else {
					
				
				Button bt_link = new Button(this);
				bt_link.setId(id_link);
				bt_link.setText(url.replaceFirst("media_url:", ""));
				tw_res.addView(bt_link);
				id_link++;
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
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("reply to " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent twitterActivityIntent = new Intent(getParent(), twitterActivity.class);
					twitterActivityIntent.putExtra("replyStr", String.valueOf(item.id));
					twitterActivityIntent.putExtra("replyTo", item.screen_name);
					TabActivity parentActivity = (TabActivity)getParent();
					parentActivity.startActivity(twitterActivityIntent);
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 1) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("tweets of " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TO DO tweets of
					//myName.setText(item.screen_name);
					tw_res.removeAllViews();
					//hello.setText(""); 
					tweet2Get(tw_res,item.screen_name.replace(" ","").replace("@","") ) ; 
					
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("Translate ");
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent translateActivityIntent = new Intent(getParent(), TranslateActivity.class);
					translateActivityIntent.putExtra("originalTweet", item.message);
					startActivity(translateActivityIntent);
				}
			});
			id++;
			//Log.e("new_year_message", item.message);
			//setContentView(hello);
		}

		// Log.e("new_year_info", "Done"); 
	} 
	

	 /**
     * Create a String from the Object using Base64 encoding
     * @param object - any Object that is Serializable
     * @return - Base64 encoded string.
     */
    public static String objectToString(Serializable object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(out).writeObject(object);
            byte[] data = out.toByteArray();
            out.close();

            out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out,0);
            b64.write(data);
            b64.close();
            out.close();

            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a generic object that needs to be cast to its proper object
     * from a Base64 ecoded string.
     * 
     * @param encodedObject
     * @return
     */
    public static Object stringToObject(String encodedObject) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(encodedObject.getBytes()), 0)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	
	
	private void tweetHome(final LinearLayout tw_res)
	{
		// Log.e("new_year_info", "Starting");

		SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		SharedPreferences.Editor editor_tweets = settingsPref.edit();
		
		
		int id = settingsPref.getInt("id_tweet", 0); //0;
		int id_link= settingsPref.getInt("id_link_tweet", 0); //0;
		
		if	(id>1000)
			id=0;
		if	(id_link>1000)
			id_link=0;
		
		//ArrayList<Tweet> 
		if (mytweets == null)
		{
			mytweets = this.getHome();//"giuliohome");
		
        //save the tweet list to preference
        
		if	(mytweets == null)
			return;
		String tweetSerial = objectToString(mytweets);
		editor_tweets.putString("tweets",tweetSerial);
		editor_tweets.commit();
		} 

        
		for(final Tweet item : mytweets ){
			EditText newtweet = new EditText(this);
			newtweet.setId(id);
			if (id % 2 == 0) {
				newtweet.setBackgroundColor(Color.YELLOW);
			} else {
				newtweet.setBackgroundColor(Color.CYAN);
			}
			newtweet.setTextColor(Color.BLACK);
			String source_str = extract_source(item.source);
			newtweet.setText(
					item.username +"("+ item.screen_name +"): " 
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
			if (checkIcon.isChecked()) {
				ImageView newprofile = new ImageView(this);
				newprofile.setId(id);
				loadBitmap(newprofile,item.profile);
				tw_res.addView(newprofile);
			}
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
				if (checkIcon.isChecked() && url.startsWith("media_url:"))
				{
					
						WebView newphoto = new WebView(this);
						newphoto.setId(id_link);
						tw_res.addView(newphoto);
						//loadBitmap(newphoto,url);
						newphoto.setWebViewClient(new WebViewClient());
						newphoto.loadUrl(url.replaceFirst("media_url:", ""));
						newphoto.getLayoutParams().height= LayoutParams.WRAP_CONTENT;
						id_link++;	
				}
				else {
					
				Button bt_link = new Button(this);
				bt_link.setId(id_link);
				bt_link.setText(url.replaceFirst("media_url:", ""));
					tw_res.addView(bt_link);
					id_link++;
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
			bt_link.setId(id_link);
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("reply to " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent twitterActivityIntent = new Intent(getParent(), twitterActivity.class);
					twitterActivityIntent.putExtra("replyStr", String.valueOf(item.id));
					twitterActivityIntent.putExtra("replyTo", item.screen_name);
					TabActivity parentActivity = (TabActivity)getParent();
					parentActivity.startActivity(twitterActivityIntent);
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			if (id % 2 == 1) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("tweets of " + item.screen_name);
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TO DO tweets of
					//myName.setText(item.screen_name);
					tw_res.removeAllViews();
					//hello.setText(""); 
					tweet2Get(tw_res,item.screen_name.replace(" ","").replace("@","") ) ; 
					
				}
			});
			bt_link = new Button(this);
			bt_link.setId(id_link);
			bt_link.setTextColor(Color.BLACK);
			bt_link.setText("Translate ");
			if (id % 2 == 0) {
				bt_link.setBackgroundColor(Color.YELLOW);
			} else {
				bt_link.setBackgroundColor(Color.CYAN);
			}
			detailL.addView(bt_link);
			id_link++;
			bt_link.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent translateActivityIntent = new Intent(getParent(), TranslateActivity.class);
					translateActivityIntent.putExtra("originalTweet", item.message);
					startActivity(translateActivityIntent);
				}
			});
			//tw_res.addView(newtweet);
			id++;
			//Log.e("new_year_message", item.message);
			//setContentView(hello);
		}

		editor_tweets.putInt("id_tweet", id++);
		editor_tweets.putInt("id_link_tweet", id_link++);
		editor_tweets.commit();
		
		// Log.e("new_year_info", "Done"); 
	} 
	
	
	
	private String readTwitterFeed(String searchUrl) {
		
		TwitterFeed_BackGround myTask = new TwitterFeed_BackGround();
		String[] params = new String[1];
		params[0] = searchUrl;
		myTask.execute(params);
		try {
			return myTask.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		
		// re-engineered for background tasks
		/*StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(searchUrl);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					//  Log.e("new_year_ok_line", line);
				}
			} else {
				//  Log.e("new_year_0", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			// Log.e("new_year_ko", "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			// Log.e("new_year_ko", "IOException");
			e.printStackTrace();
		}
		return builder.toString();*/
	}

	private void OAUTHfollow(String name_to_follow, Boolean is_to_follow )
	{
		OAUTHfollow_BackGround myTask = new OAUTHfollow_BackGround();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String userKey = settings.getString("user_key", "");
		String userSecret = settings.getString("user_secret", "");

		if	(userKey.isEmpty() || userSecret.isEmpty())
		{
			Toast.makeText(getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
			//return "";

		}

		String consumerKey = getString(R.string.consumerKey);
		String consumerSecret = getString(R.string.consumerSecret);
		
		String postUrl;
		
		if	(is_to_follow) {
			postUrl= "https:/"+"/api.twitter.com/1.1/friendships/create.json";
		} else {
			postUrl= "https:/"+"/api.twitter.com/1.1/friendships/destroy.json";
		}

		String[] params = new String[6];
		params[0] = postUrl;
		params[1] = consumerKey;
		params[2] = consumerSecret;
		params[3] = userKey;
		params[4] = userSecret;
		params[5]=name_to_follow;
		myTask.execute(params);
		try {
			myTask.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//return "";
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//return "";
		}

	}
	
	private String OAUTHreadTwitterFeed(String searchUrl) {
		
		
		OAUTHreadTwitterFeed_BackGround myTask = new OAUTHreadTwitterFeed_BackGround();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String userKey = settings.getString("user_key", "");
		String userSecret = settings.getString("user_secret", "");

		if	(userKey.isEmpty() || userSecret.isEmpty())
		{
			Toast.makeText(getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
			return "";

		}

		String consumerKey = getString(R.string.consumerKey);
		String consumerSecret = getString(R.string.consumerSecret);

		String[] params = new String[5];
		params[0] = searchUrl;
		params[1] = consumerKey;
		params[2] = consumerSecret;
		params[3] = userKey;
		params[4] = userSecret;
		myTask.execute(params);
		try {
			return myTask.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		
		// re-engineered for background tasks
		/*StringBuilder builder = new StringBuilder();
		//HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(searchUrl);
		

		CommonsHttpOAuthConsumer  httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		httpOauthConsumer.setTokenWithSecret(userKey, userSecret);
		try {
			httpOauthConsumer.sign(httpGet);
		} catch (OAuthMessageSignerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthExpectationFailedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OAuthCommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		DefaultHttpClient client = new DefaultHttpClient();
		//String response = client.execute(httpGet, new BasicResponseHandler());
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					//  Log.e("new_year_ok_line", line);
				}
			} else {
				//  Log.e("new_year_0", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			// Log.e("new_year_ko", "ClientProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			// Log.e("new_year_ko", "IOException");
			e.printStackTrace();
		}
		return builder.toString(); */
	}



	private ArrayList<Tweet> getTweets (String searchTerm , String method) {

		String searchUrl =

				"https:/"+"/api.twitter.com/1.1/search/tweets.json?q="+searchTerm+"&count=30&result_type="+method; 

		ArrayList<Tweet> tweets =
				new ArrayList<Tweet>();

		String responseBody; 
		
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		
		String userKey = settings.getString("user_key", "");
		String userSecret = settings.getString("user_secret", "");
		if	(userKey.isEmpty() || userSecret.isEmpty())
		{
			Toast.makeText(this.getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
			return null;

		}
		String consumerKey = getString(R.string.consumerKey);
		String consumerSecret = getString(R.string.consumerSecret);
		
		
		GetTweets_BackGround myTask = new GetTweets_BackGround();
		String[] params = new String[5];
		params[0] = searchUrl;
		params[1] = consumerKey;
		params[2] = consumerSecret;
		params[3] = userKey;
		params[4] = userSecret;
		
		myTask.execute(params);
		try {
			responseBody =  myTask.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
		// re-engineered for background tasks
		/* try {

			HttpClient client = new  DefaultHttpClient();
			// Log.e("new_year_ko", "http client"); 
			HttpGet get = new HttpGet(searchUrl);
			// Log.e("new_year_ko","http get") ;
			ResponseHandler<String> responseHandler =
					new BasicResponseHandler();
			// Log.e("new_year_ko", "response handler");
			responseBody = null; 
			if ( client == null) {return null;}; 
			if ( get == null) {return null;}; 
			if ( responseHandler == null) {return null;}; 
			responseBody = client.execute(get, responseHandler);
			// Log.e("new_year_ko", "resp body");
		} catch(Exception ex) {
			// Log.e("new_year_ko no internet", ex .toString()); 
			//ex.printStackTrace();
			return null;
		} */

		JSONObject jsonObject = null;

		//Log.e("new_year ", "responseBody = " + responseBody);


		if ( responseBody == null) {return null;}; 
		// Log.e("new_year_ko", "response body") ;
		JSONArray arr = null;
		try {
			//arr = new JSONArray(responseBody);

			JSONObject JasonobjectR = new JSONObject( responseBody ); 
			if ( JasonobjectR == null) {return null;}; 
			arr = JasonobjectR.getJSONArray("statuses");


		} catch (Exception e1) {


			//    Log.e("new_year ", "exc  = " + e1.toString());

			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ( arr == null) {return null;};
		//Log.e("new_year_arr","not null");
		for (int i = 0; i < arr.length(); i++) {
			try {
				jsonObject = arr.getJSONObject(i);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Tweet tweet = null;
			try {
				if ( jsonObject != null ) { 
					if ( jsonObject.getString("text") != null ) {
						//Log.e("new_year_text ", jsonObject.getString("text") ); 
						tweet = new Tweet( 
								jsonObject.getJSONObject("user").getString("name"),
								jsonObject.getJSONObject("user").getString("screen_name"),
								jsonObject.getString("text"),
								jsonObject.getString("created_at"),
								jsonObject.getString("source"),
								jsonObject.getJSONObject("user").getString("profile_image_url"),
								jsonObject.getLong("id"),
								jsonObject.getJSONObject("entities").getJSONArray("urls"),
								jsonObject.getJSONObject("entities").isNull("media")?null:jsonObject.getJSONObject("entities").getJSONArray("media")
								);
						/*tweet = new Tweet(
								jsonObject.getString( "from_user"),
								jsonObject.getString( "from_user"),
								jsonObject.getString("text"),
								jsonObject.getString("created_at") ,
								jsonObject.getString("source"),
								jsonObject.getString("profile_image_url"),
								jsonObject.getLong("id")
								);*/ 
						tweets.add(tweet); 
					}; };
			} catch ( Exception e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return tweets;
	}
	
	private enum MyEnum {
        TWEET("tweet"), MENTION("mention"),FOLLOW("follow"),UNFOLLOW("unfollow"),FAVORITES("favorites");
        private String value;

        private MyEnum (String value) {
                this.value = value;
        }

	}

	private ArrayList<Tweet> get2Tweets(String searchTerm, MyEnum command) {

		//String searchUrl ="http:/"+"/search.twitter.com/search.json?q="+searchTerm+"&rpp=5&include_entities=true&result_type=mixed"; 

		String searchUrl="";
		//searchUrl= "https:/"+"/api.twitter.com/1.1/statuses/user_timeline.json?screen_name="+searchTerm;
		
		
		ArrayList<Tweet> tweets = 
				new ArrayList<Tweet>();

	
		switch (command) {
		case TWEET: 
			searchUrl= "https:/"+"/api.twitter.com/1.1/statuses/user_timeline.json?screen_name="+searchTerm;
			break;
		case FAVORITES:
			searchUrl= "https:/"+"/api.twitter.com/1.1/favorites/list.json";
			if (!searchTerm.equals(""))
				searchUrl+="?screen_name="+searchTerm;
			break;
		case MENTION:
			searchUrl= "https:/"+"/api.twitter.com/1.1/statuses/mentions_timeline.json";
			break;
		case FOLLOW:
			OAUTHfollow(searchTerm, true);
			return tweets;
		case UNFOLLOW:
			OAUTHfollow(searchTerm, false);
			return tweets;
		}
		
		
		//HttpClient client = new  DefaultHttpClient();
		//HttpGet get = new HttpGet(searchUrl);


		// ResponseHandler responseHandler =        new BasicResponseHandler();


		// Log.e("new_year_url", searchUrl);
		String readTwitterFeed = OAUTHreadTwitterFeed(searchUrl);
		// Log.e("new_year_feed", "reading feed now");//readTwitterFeed);
		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			// Log.i("new_year_ok",     "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if ( jsonObject != null ) { 
					Log.i("new_year_ok", jsonObject.getString("text"));

					Tweet tweet = new Tweet( 
							jsonObject.getJSONObject("user").getString("name"),
							jsonObject.getJSONObject("user").getString("screen_name"),
							jsonObject.getString("text"),
							jsonObject.getString("created_at"),
							jsonObject.getString("source"),
							jsonObject.getJSONObject("user").getString("profile_image_url"),
							jsonObject.getLong("id"),
							jsonObject.getJSONObject("entities").getJSONArray("urls"),
							jsonObject.getJSONObject("entities").isNull("media")?null:jsonObject.getJSONObject("entities").getJSONArray("media")
							);
					tweets.add(tweet);
				}

			}
		} catch (Exception e) {
			Log.e("new_year_ko", "json array");
			e.printStackTrace();
		}



		return tweets;
	} 



	protected ArrayList<String> get2Lists(String searchTerm) {

		//String searchUrl ="http:/"+"/search.twitter.com/search.json?q="+searchTerm+"&rpp=5&include_entities=true&result_type=mixed"; 

		String searchUrl = "https:/"+"/api.twitter.com/1.1/lists/list.json?screen_name="+searchTerm;

		ArrayList<String> myLists = 
				new ArrayList<String>();

		//HttpClient client = new  DefaultHttpClient();
		//HttpGet get = new HttpGet(searchUrl);


		// ResponseHandler responseHandler =        new BasicResponseHandler();


		// Log.e("new_year_url", searchUrl);
		String readTwitterFeed = OAUTHreadTwitterFeed(searchUrl);
		// Log.e("new_year_feed", "reading feed now");//readTwitterFeed);
		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			// Log.i("new_year_ok",     "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if ( jsonObject != null ) { 
					String curr_list = jsonObject.getString("name");
					myLists.add(curr_list);
				}
			}
						
		} catch (Exception e) {
			Log.e("new_year_ko", "json array");
			e.printStackTrace();
		}



		return myLists;
	} 
	
	protected ArrayList<String> get2MyLists() {

		//String searchUrl ="http:/"+"/search.twitter.com/search.json?q="+searchTerm+"&rpp=5&include_entities=true&result_type=mixed"; 

		String searchUrl = "https:/"+"/api.twitter.com/1.1/lists/list.json";

		ArrayList<String> myLists = 
				new ArrayList<String>();

		//HttpClient client = new  DefaultHttpClient();
		//HttpGet get = new HttpGet(searchUrl);


		// ResponseHandler responseHandler =        new BasicResponseHandler();


		// Log.e("new_year_url", searchUrl);
		String readTwitterFeed = OAUTHreadTwitterFeed(searchUrl);
		// Log.e("new_year_feed", "reading feed now");//readTwitterFeed);
		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			// Log.i("new_year_ok",     "Number of entries " + jsonArray.length());
			String my_lists_str="";
			String my_owner="";
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if ( jsonObject != null ) { 
					String curr_list = jsonObject.getString("name");
					if	(my_owner.equals(""))
					{
						my_owner=jsonObject.getJSONObject("user").getString("screen_name");
					}
					myLists.add(curr_list);
					my_lists_str = my_lists_str+","+curr_list;
				}
			}
			SharedPreferences settingsPref = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
			SharedPreferences.Editor editor = settingsPref.edit();
			editor.putString("mylists", my_lists_str );
			editor.putString("owner", my_owner );
			editor.commit();
		} catch (Exception e) {
			Log.e("new_year_ko", "json array");
			e.printStackTrace();
		}



		return myLists;
	} 


	
	
	private ArrayList<Tweet> list2Tweets(String searchTerm) {

		//String searchUrl ="http:/"+"/search.twitter.com/search.json?q="+searchTerm+"&rpp=5&include_entities=true&result_type=mixed"; 

		String searchUrl = "https:/"+"/api.twitter.com/1.1/lists/statuses.json?slug="+searchTerm;

		ArrayList<Tweet> tweets = 
				new ArrayList<Tweet>();

		//HttpClient client = new  DefaultHttpClient();
		//HttpGet get = new HttpGet(searchUrl);


		// ResponseHandler responseHandler =        new BasicResponseHandler();


		// Log.e("new_year_url", searchUrl);
		String readTwitterFeed = OAUTHreadTwitterFeed(searchUrl);
		// Log.e("new_year_feed", "reading feed now");//readTwitterFeed);
		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			// Log.i("new_year_ok",     "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if ( jsonObject != null ) { 
					Log.i("new_year_ok", jsonObject.getString("text"));

					Tweet tweet = new Tweet( 
							jsonObject.getJSONObject("user").getString("name"),
							jsonObject.getJSONObject("user").getString("screen_name"),
							jsonObject.getString("text"),
							jsonObject.getString("created_at"),
							jsonObject.getString("source"),
							jsonObject.getJSONObject("user").getString("profile_image_url"),
							jsonObject.getLong("id"),
							jsonObject.getJSONObject("entities").getJSONArray("urls"),
							jsonObject.getJSONObject("entities").isNull("media")?null:jsonObject.getJSONObject("entities").getJSONArray("media")
							);
					tweets.add(tweet);
				}

			}
		} catch (Exception e) {
			Log.e("new_year_ko", "json array");
			e.printStackTrace();
		}


		return tweets;
	} 

	

	private ArrayList<Tweet> getHome() {

		//String searchUrl ="http:/"+"/search.twitter.com/search.json?q="+searchTerm+"&rpp=5&include_entities=true&result_type=mixed"; 

		String searchUrl = "https:/"+"/api.twitter.com/1.1/statuses/home_timeline.json";

		ArrayList<Tweet> tweets = 
				new ArrayList<Tweet>();

		//HttpClient client = new  DefaultHttpClient();
		//HttpGet get = new HttpGet(searchUrl);


		// ResponseHandler responseHandler =        new BasicResponseHandler();


		// Log.e("new_year_url", searchUrl);
		String readTwitterFeed = OAUTHreadTwitterFeed(searchUrl);
		// Log.e("new_year_feed", "reading feed now");//readTwitterFeed);
		try {
			JSONArray jsonArray = new JSONArray(readTwitterFeed);
			// Log.i("new_year_ok",     "Number of entries " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if ( jsonObject != null ) { 
					Log.i("new_year_ok", jsonObject.getString("text"));

					Tweet tweet = new Tweet( 
							jsonObject.getJSONObject("user").getString("name"),
							jsonObject.getJSONObject("user").getString("screen_name"),
							jsonObject.getString("text"),
							jsonObject.getString("created_at"),
							jsonObject.getString("source"),
							jsonObject.getJSONObject("user").getString("profile_image_url"),
							jsonObject.getLong("id"),
							jsonObject.getJSONObject("entities").getJSONArray("urls"),
							jsonObject.getJSONObject("entities").isNull("media")?null:jsonObject.getJSONObject("entities").getJSONArray("media")
							);
					tweets.add(tweet);
				}

			}
		} catch (Exception e) {
			Log.e("new_year_ko", "json array");
			e.printStackTrace();
		}




		return tweets;
	} 

	
	
	
	private static void loadBitmap(ImageView profile_photo,String photo_url_str) {
		URL newurl=null;
		try {
			newurl = new URL(photo_url_str);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap mIcon_val=null;
		Bitmap_backGround myTask = new Bitmap_backGround();
		URL[] params = new URL[1];
		params[0] = newurl;
		myTask.execute(params);
		try {
			mIcon_val =  myTask.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		// re-engineered for background tasks
		/*try {
			mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		// end of re-engineering for background tasks
		
		profile_photo.setImageBitmap(mIcon_val);
	}


}
