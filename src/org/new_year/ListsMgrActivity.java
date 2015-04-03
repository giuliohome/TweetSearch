package org.new_year;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;


public class ListsMgrActivity extends Activity {
	private Spinner comboList;
	public static List<String> list_arr;
	public static ArrayAdapter<String> dataAdapter;


	private EditText myName;
	private EditText newList;
	//public static ListsMgrActivity thisClass = null; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ScrollView sc = new ScrollView(this);
		Button bR = new Button(this);
		bR.setText("refresh lists");
		bR.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						list_arr.clear();
						if	(TweetSearchActivity.thisClass == null)
							return;
						TweetSearchActivity.list_arr.clear();
						ArrayList<String> res =	TweetSearchActivity.thisClass.get2MyLists();
						for (int i = 0; i < res.size(); i++) {
							TweetSearchActivity.list_arr.add(res.get(i));
							list_arr.add(res.get(i));
						}
						//TweetSearchActivity.list_arr = res;
						//list_arr = res;
						dataAdapter.notifyDataSetChanged();
						TweetSearchActivity.dataAdapter.notifyDataSetChanged();
					}
				} 
				
				
				); 
		LinearLayout ll = new LinearLayout(this); 
		ll.setOrientation( LinearLayout.VERTICAL );
		LinearLayout llb = new LinearLayout(this);
		llb.addView(bR); 
		comboList = new Spinner(this);
		list_arr = new ArrayList<String>();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String my_lists_pref = settings.getString("mylists", "--");
		for (String curr_list : my_lists_pref.split(",") )  
	      {  
	         list_arr.add(curr_list); 
	      }  
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list_arr);
		comboList.setAdapter(dataAdapter);	
		llb.addView(comboList); 
		myName = new EditText(this);
		myName.setHint(R.string.username); 
		ll.addView(llb); 
		ll.addView(myName); 
		LinearLayout llb2 = new LinearLayout(this);
		Button bAdd = new Button(this);
		bAdd.setText("add user to list");
		bAdd.setOnClickListener(handlerList);
		llb2.addView(bAdd);
		Button bRem = new Button(this);
		bRem.setText("remove from list");
		bRem.setOnClickListener(handlerRemList);
		llb2.addView(bRem);
		newList = new EditText(this);
		newList.setHint(R.string.listname); 
		LinearLayout llb3 = new LinearLayout(this);
		Button bNewL = new Button(this);
		bNewL.setText("create new list");
		bNewL.setOnClickListener(handlerNewList);
		llb3.addView(bNewL);
		Button bDelL = new Button(this);
		bDelL.setText("delete list");
		bDelL.setOnClickListener(handlerDelList);
		llb3.addView(bDelL);
		ll.addView(llb2); 
		ll.addView(newList); 
		ll.addView(llb3); 
		sc.addView(ll);
		setContentView(sc);
		
	}
	
	private OnClickListener handlerDelList = new OnClickListener() {
		public void onClick(View v) {
			String add_list_name = newList.getText().toString().trim().replace(" ","_").replace("@","");
			if (add_list_name.length()>0  )
			{
				OAUTHnewlist(add_list_name, false) ;
			}
		}
	} ;
	private OnClickListener handlerNewList = new OnClickListener() {
		public void onClick(View v) {
			String add_list_name = newList.getText().toString().trim().replace(" ","_").replace("@","").replace("#","");
			if (add_list_name.length()>0  )
			{
				OAUTHnewlist(add_list_name, true) ;
			}
		}
	} ;
	private OnClickListener handlerRemList = new OnClickListener() {
		public void onClick(View v) {
			if (comboList.getSelectedItem() == null) {
			    
				return;
			} 
			String add_screen_name = myName.getText().toString().trim().replace(" ","").replace("@","");
			String list_name = comboList.getSelectedItem().toString(). replace(" ","").replace("#", "%23");
			if (add_screen_name.length()>0 && list_name.length()>0 )
			{
				OAUTHadd2list(list_name,add_screen_name, false) ;
			}
		}
	} ;
	private OnClickListener handlerList = new OnClickListener() {
		public void onClick(View v) {
			if (comboList.getSelectedItem() == null) {
			    
				return;
			} 
			String add_screen_name = myName.getText().toString().trim().replace(" ","").replace("@","");
			String list_name = comboList.getSelectedItem().toString(). replace(" ","").replace("#", "%23");
			if (add_screen_name.length()>0 && list_name.length()>0 )
			{
				OAUTHadd2list(list_name,add_screen_name, true) ;
			}
		}
	} ;
	
	private void OAUTHnewlist(String list_to_add, Boolean is_to_add )
	{
		OAUTHadd2list_BackGround myTask = new OAUTHadd2list_BackGround();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String userKey = settings.getString("user_key", "");
		String userSecret = settings.getString("user_secret", "");
		String my_owner = settings.getString("owner", "");
		

		if	(userKey.isEmpty() || userSecret.isEmpty())
		{
			Toast.makeText(getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
			//return "";

		}

		String consumerKey = getString(R.string.consumerKey);
		String consumerSecret = getString(R.string.consumerSecret);
		
		String postUrl;
		
		if	(is_to_add) {
			postUrl= "https:/"+"/api.twitter.com/1.1/lists/create.json";
		} else {
			postUrl= "https:/"+"/api.twitter.com/1.1/lists/destroy.json";
		}

		String[] params;
		if	(is_to_add) {
			params = new String[6];
		} else {
			params = new String[7];
		}
		params[0] = postUrl;
		params[1] = consumerKey;
		params[2] = consumerSecret;
		params[3] = userKey;
		params[4] = userSecret;
		if	(is_to_add) {
			params[5] = list_to_add;
		} else {
			params[5] = my_owner;
			params[6] = list_to_add;
		}
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

	private void OAUTHadd2list(String list_name, String name_to_add, Boolean is_to_add )
	{
		OAUTHadd2list_BackGround myTask = new OAUTHadd2list_BackGround();
		SharedPreferences settings = getSharedPreferences("opentweetsearch_prefs", MODE_PRIVATE);
		String userKey = settings.getString("user_key", "");
		String userSecret = settings.getString("user_secret", "");
		String my_owner = settings.getString("owner", "");
		

		if	(userKey.isEmpty() || userSecret.isEmpty())
		{
			Toast.makeText(getBaseContext(), "no authorization", Toast.LENGTH_LONG).show();
			//return "";

		}

		String consumerKey = getString(R.string.consumerKey);
		String consumerSecret = getString(R.string.consumerSecret);
		
		String postUrl;
		
		if	(is_to_add) {
			postUrl= "https:/"+"/api.twitter.com/1.1/lists/members/create.json";
		} else {
			postUrl= "https:/"+"/api.twitter.com/1.1/lists/members/destroy.json";
		}

		String[] params = new String[8];
		params[0] = postUrl;
		params[1] = consumerKey;
		params[2] = consumerSecret;
		params[3] = userKey;
		params[4] = userSecret;
		params[5] = list_name;
		params[6] = name_to_add;
		params[7] = my_owner;
		
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
	
}
