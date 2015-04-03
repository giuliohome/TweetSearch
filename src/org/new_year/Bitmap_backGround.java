package org.new_year;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class Bitmap_backGround extends AsyncTask<URL, Void, Bitmap> {

	@Override
	protected Bitmap doInBackground(URL... params) {
		// TODO Auto-generated method stub
		try {
			return BitmapFactory.decodeStream(params[0].openConnection().getInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return null;
	}

}
