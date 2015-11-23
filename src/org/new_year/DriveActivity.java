package org.new_year;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


public class DriveActivity extends Activity 
{
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
	private String					myFolderID;
	private String					myTextFolderID;
	private EditText 				textbox;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
    	super.onCreate(savedInstanceState);

    	

    	
        // Connect to Google Drive
        mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        
        mContext = getApplicationContext();

		
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.listView1);
		
		OnItemClickListener mMessageClickedHandler = new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView parent, View v, int position, long id) 
		    {
		    	downloadItemFromList(position);
		    }
		};

		mListView.setOnItemClickListener(mMessageClickedHandler); 
		
		
		textbox = (EditText) findViewById(R.id.editText1);
        
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	getDriveContents();
            }
        });
        
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	saveTweets2GDrive();
            }
        });
	}
	private void getDriveContents()
	{
		Thread t = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
                mResultList = new ArrayList<File>();
				com.google.api.services.drive.Drive.Files f1 = mService.files();
				com.google.api.services.drive.Drive.Files.List request = null;
				
				do 
				{
					try 
					{ 
						request = f1.list();
						request.setQ("'" + myFolderID + "' in parents and trashed=false");
						com.google.api.services.drive.model.FileList fileList = request.execute();
						
						mResultList.addAll(fileList.getItems());
						request.setPageToken(fileList.getNextPageToken());
					} catch (UserRecoverableAuthIOException e) {
						startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
					} catch (IOException e) {
						e.printStackTrace();
						if (request != null)
						{
							request.setPageToken(null);
						}
					}
				} while (request.getPageToken() !=null && request.getPageToken().length() > 0);
				
				populateListView();
    		}
    	});
    	t.start();
	}
	
	  /**
     * 
     * @param service google drive instance
     * @param title the title (name) of the folder (the one you search for)
     * @param parentId the parent Id of this folder (use root) if the folder is in the main directory of google drive
     * @return google drive file object 
     * @throws IOException
     */
    private File getExistsFolder(Drive service,String title,String parentId) throws IOException 
    {
        Drive.Files.List request;
        request = service.files().list();
        String query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='" + title + "' AND '" + parentId + "' in parents";
        //Logger.info(TAG + ": isFolderExists(): Query= " + query);
        request = request.setQ(query);
        com.google.api.services.drive.model.FileList files = request.execute();
        //Logger.info(TAG + ": isFolderExists(): List Size =" + files.getItems().size());
        if (files.getItems().size() == 0) //if the size is zero, then the folder doesn't exist
            return null;
        else
            //since google drive allows to have multiple folders with the same title (name)
            //we select the first file in the list to return
            return files.getItems().get(0);
    }
    /**
     * 
     * @param service google drive instance
     * @param title the folder's title
     * @param listParentReference the list of parents references where you want the folder to be created, 
     * if you have more than one parent references, then a folder will be created in each one of them  
     * @return google drive file object   
     * @throws IOException
     */
    private File createFolder(Drive service,String title,List<com.google.api.services.drive.model.ParentReference> listParentReference) throws IOException
    {
        File body = new File();
        body.setTitle(title);
        body.setParents(listParentReference);
        body.setMimeType("application/vnd.google-apps.folder");
        File file = service.files().insert(body).execute(); 
        return file;

    }
    
	
	private void downloadItemFromList(int position)
	{
		mDLVal = (String) mListView.getItemAtPosition(position);
    	showToast("You just pressed: " + mDLVal);
    	
    	Thread t = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
		    	for(File tmp : mResultList)
				{
					if (tmp.getTitle().equalsIgnoreCase(mDLVal))
					{
						
						if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
						{
							try
							{
								com.google.api.client.http.HttpResponse resp = 
										mService.getRequestFactory()
										.buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
										.execute();
								InputStream iStream = resp.getContent();
								BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
								StringBuilder total = new StringBuilder();
								String line;
								while ((line = r.readLine()) != null) {
								    total.append(line);
								}
								TweetSearchActivity.mytweets = null;
								TweetSearchActivity.mytweets = (ArrayList<Tweet>)TweetSearchActivity.stringToObject(total.toString());
								if	(TweetSearchActivity.mytweets != null)
								{
									showToast("Downloaded: " + tmp.getTitle()); 
									TweetSearchActivity.skipHome = true;
								} else {
									showToast("Error while downloading");
								}
								
								/*try 
								{
									final java.io.File file = new java.io.File(Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), 
											tmp.getTitle());
									showToast("Downloading: " + tmp.getTitle() + " to " + Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
									storeFile(file, iStream);
								} finally {
									iStream.close();
								}*/
								
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
    		}
    	});
    	t.start();
	}
	
	private void populateListView()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				mFileArray = new String[mResultList.size()];
				int i = 0;
				for(File tmp : mResultList)
				{
					mFileArray[i] = tmp.getTitle();
					i++;
				}
				mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mFileArray);
				mListView.setAdapter(mAdapter);
			}
		});
	}
	
	private void storeFile(java.io.File file, InputStream iStream)
	{
		try 
		{
			final OutputStream oStream = new FileOutputStream(file);
			try
			{
				try
				{
					final byte[] buffer = new byte[1024];
					int read;
					while ((read = iStream.read(buffer)) != -1)
					{
						oStream.write(buffer, 0, read);
					}
					oStream.flush();
				} finally {
					oStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
						
						InitDriveFolder();
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
    private void saveTweets2GDrive()
	{
    	Thread t = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
				try 
				{
					if	(mService == null)
					{
						showToast("Choose your account before!");
						return;
					}
					if (TweetSearchActivity.mytweets == null)
					{
						showToast("No tweet to save!");
						return;
					}
					String tweetSerial = TweetSearchActivity.objectToString(TweetSearchActivity.mytweets);
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String formattedDate = sdf.format(date);
					
					String tweetfilename = textbox.getText().toString() + "_"
					+formattedDate+".html";
					String tweetfilename2 = textbox.getText().toString() + "_"
					+formattedDate+".tweet";
					String MYFILE = "temp_tweet_file.txt";
					//String MYFILE2 = "temp_tweet_file.tweet";
					showToast("Saving tweets to temp file!");
					java.io.File myFile = null;
					try {
				 

						myFile = new java.io.File(Environment.getExternalStorageDirectory(), MYFILE);
						    if (!myFile.exists())
						        myFile.createNewFile();
						    FileOutputStream fos = new FileOutputStream(myFile);

						    
						
						
						
				         // MODE_APPEND, MODE_WORLD_READABLE, MODE_WORLD_WRITEABLE
						// create new file or rewrite existing
				                //FileOutputStream fos = openFileOutput(MYFILE, getApplicationContext().MODE_PRIVATE);
				                // append to file
				                //FileOutputStream fos = openFileOutput(MYFILE, getApplicationContext().MODE_APPEND);
						    	fos.write("\n<html>\n".getBytes());
						    	fos.write("\n<body>\n".getBytes());
				                for(final Tweet item : TweetSearchActivity.mytweets ){
				                	String source_str = extract_source2(item.source);
				        			String htmlurls = "";
				                	for (int index =0; index < item.urls.size(); index++){
				                		htmlurls  += format_url(item.urls.get(index));
				                	}
				                	
				                	String strText = "<p>"+item.username+" @ "+ item.date.replace("+0000", "") + "</p>" 
				                			+ "\n" + "<p>" + item.message + "</p>"
											+ "\n" + htmlurls  
											+ "\n" + source_str + "\n\n";
				                	fos.write(strText.getBytes());
				                	 fos.flush();
								}
				                fos.write("\n</body>\n".getBytes());
				                fos.write("\n</html>\n".getBytes());
						    	
				                
								
				                fos.flush();
						fos.close();		
						
					} catch (IOException e) {
						e.toString();
				    }
					
					// Create URI from real path
					//String path;
					//path = getPathFromUri(mFileUri);
					//mFileUri = Uri.fromFile(new java.io.File(path));
					showToast("Saving tweets to GoogleDrive!");
					
					//ContentResolver cR = DriveActivity.this.getContentResolver();
					
					// File's binary content
					java.io.File fileContent = myFile;
					FileContent mediaContent = new FileContent("text/html", fileContent);

										// File's meta data. 
					File body = new File();
					body.setTitle(tweetfilename);
					body.setParents(Arrays.asList(new com.google.api.services.drive.model.ParentReference().setId(myTextFolderID)));
					body.setMimeType("text/html");

					com.google.api.services.drive.Drive.Files f1 = mService.files();
					com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body, mediaContent);
					File file = i1.execute();
					
					if (file != null) 
					{
						showToast("Uploaded: " + file.getTitle());
					}
					
					body = new File();
					body.setTitle(tweetfilename2);
					body.setMimeType("text/plain");
					body.setParents(Arrays.asList(new com.google.api.services.drive.model.ParentReference().setId(myFolderID)));
					
					ByteArrayInputStream bis = new ByteArrayInputStream(tweetSerial.getBytes("UTF-8"));

					InputStreamContent content = new InputStreamContent("text/plain", bis);
					
					f1 = mService.files();
					i1 = f1.insert(body, content);
					file = i1.execute();
					
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
    private String format_url(String url)
    {
    	if (url.startsWith("media_url:")) {
    		return "<img src=\""+url.replace("media_url:", "")+"\">";
    	}
    	return "<p><a href = \""+ url.replace("", "")+ "\"> "+url+" </a></p>";
    }
    private void InitDriveFolder()
    {
    	Thread t = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{    	
    			try {
		            File TwitterFolder = getExistsFolder(mService, "OpenTweetSearch", "root");
		            if	(TwitterFolder == null)
		            	{
		    				TwitterFolder = createFolder(mService, "OpenTweetSearch",Arrays.asList(new com.google.api.services.drive.model.ParentReference().setId("root")));
		    			}
		            myFolderID = TwitterFolder.getId();
		            TwitterFolder = getExistsFolder(mService, "HtmlTweets", "root");
		            if	(TwitterFolder == null)
		            	{
		    				TwitterFolder = createFolder(mService, "HtmlTweets",Arrays.asList(new com.google.api.services.drive.model.ParentReference().setId("root")));
		    			}
		            myTextFolderID = TwitterFolder.getId();	
    			
			} catch (IOException e) {
				e.printStackTrace();
				showToast("Transfer ERROR: " + e.toString());
			}
		}
	});
	t.start();
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
					
					ContentResolver cR = DriveActivity.this.getContentResolver();
					
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

}
