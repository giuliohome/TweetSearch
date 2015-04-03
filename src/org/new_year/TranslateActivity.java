package org.new_year;

import java.net.URLDecoder;
import java.net.URLEncoder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.os.Bundle;

public class TranslateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_translate);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		EditText et =  (EditText)findViewById(R.id.editText3);
		Bundle myBundle = this.getIntent().getExtras();
		if (myBundle != null) {
			et.setText((String)myBundle.get("originalTweet"));
		}
		Button  bt_tran = (Button)findViewById(R.id.button1);
		bt_tran.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TranslateClick();
			}
		});
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}


	void TranslateClick()
	{
		EditText et1 =  (EditText)findViewById(R.id.editText1);
		EditText et2 =  (EditText)findViewById(R.id.editText2);
		EditText et3 =  (EditText)findViewById(R.id.editText3);
		//EditText et_en =  (EditText)findViewById(R.id.editText4);
		String encoding = "utf-8"; //et_en.getText().toString(); 
		WebView et4 =  (WebView)findViewById(R.id.webView1);
		WebSettings settings = et4.getSettings();
		settings.setDefaultTextEncodingName(encoding);
		try {
			
			String myData= Translate.translate(et3.getText().toString(), et1.getText().toString(),et2.getText().toString());
			//URLEncoder.encode(myData).replaceAll("\\+"," ")
			//et4.loadData(URLEncoder.encode(myData).replaceAll("\\+"," ") , "text/html" , "UTF-8");
			et4.loadDataWithBaseURL(null,myData, "text/html", encoding, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	}


