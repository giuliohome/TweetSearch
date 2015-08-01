package org.new_year;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Tweet implements Serializable {
	public String username;
	public String screen_name;
	public String message;
	public String date;
	public String source;
	public String profile;
	public long id;
	public ArrayList<String>urls = new ArrayList<String>();

	public Tweet(String username, String tw_screen_name, String message, String date, 
			String tw_source, String tw_profile, long tw_id,
			JSONArray json_urls, JSONArray json_media) {
		this.username = username;
		this.screen_name=tw_screen_name;
		message=message.replace("&amp;", "&");
		this.message = message;
		this.date = date;
		this.source = tw_source;
		this.profile = tw_profile;
		this.id = tw_id;
		int base=0;
		if	(json_urls != null)
		{
			for(int i=0; i< json_urls.length();i++)
			{
				try {
					String display_url = json_urls.getJSONObject(i).getString("display_url"); 
					urls.add(json_urls.getJSONObject(i).getString("expanded_url"));
					this.message = this.message.replaceAll(json_urls.getJSONObject(i).getString("url"), " ");// display_url);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if	(json_media != null)
		{
			for(int i=0; i< json_media.length();i++)
			{
				try {
					String media_url = json_media.getJSONObject(i).getString("media_url").replace("http://", "https://"); 
					urls.add("media_url:"+media_url);
					this.message = this.message.replaceAll(json_media.getJSONObject(i).getString("url"), " ");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/*int i_url = message.indexOf("//t.co/", base);
		while (i_url>=0)
		{
			base=i_url+7;
			i_url = message.indexOf(" ", base);
			int i_url2 = message.indexOf(".", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf(")", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf(",", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf(";", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf("!", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf("?", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			i_url2 = message.indexOf("\n", base);
			if	(i_url>i_url2 && i_url2>0)
			{
				i_url=i_url2;
			}
			if (i_url<0) {
				urls.add("http://t.co/"+message.substring(base));
			} else {
				urls.add("http://t.co/"+message.substring(base,i_url));
				base=i_url;
				i_url = message.indexOf("//t.co/", base);
			}
		} */
	} 
}
