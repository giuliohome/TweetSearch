package org.new_year;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.widget.ImageView;

class OpenStreamBG extends AsyncTask<Object, Void, Bitmap> {
    private ImageView imageView;
	protected Bitmap doInBackground(Object... urls) {
        imageView = (ImageView)urls[1];
        InputStream in;
		try {
			in = new java.net.URL((String)urls[0]).openStream();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        Bitmap map = BitmapFactory.decodeStream(in);
        return map;
    }

    // Sets the Bitmap returned by doInBackground
    @Override
    protected void onPostExecute(Bitmap result) {
    	if	(result == null) {
    		return;
    	}
        imageView.setImageBitmap(result);
        imageView.setAdjustViewBounds(true);
    }

}

