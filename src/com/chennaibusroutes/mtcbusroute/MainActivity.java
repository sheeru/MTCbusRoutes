package com.chennaibusroutes.mtcbusroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class MainActivity extends Activity {

	String[] StageList = {
	         "A", "AA", "AAA", "AB", "ABCD","AAAA","AAAAAAA"
	     };
	
	TextView test;
	String BusNumbers;
	String StagesList;
	LinkedList<String> stages_row = new LinkedList<String>();
	
	String[] StagesList_Array;
	String[] BusNumber_Array;
	

//	LinkedList<LinkedList<String>> stages_row = new LinkedList<LinkedList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String stringUrl = "http://www.greenmesg.org/dictionary/routes/chennai_bus_routes.txt?161011";
		ConnectivityManager connMgr = (ConnectivityManager) 
	        getSystemService(Context.CONNECTIVITY_SERVICE);
		
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);

        } else {
            test.setText("No network connection available.");
        }
		
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, StageList);
		
		
		AutoCompleteTextView source = (AutoCompleteTextView)
                findViewById(R.id.Source);
		source.setThreshold(1);
		source.setAdapter(adapter);
		
        test = (TextView) findViewById(R.id.test);
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       private static final String DEBUG_TAG = "Stock Quote";

	@Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
               downloadUrl(urls[0]);
               return null;
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {   
    	   test.setText(BusNumbers);
      }
      
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private void downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();
	
	        // Convert the InputStream into a string
	        readIt(is);

	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
		// Reads an InputStream and converts it to a String.
		public void readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
			BufferedReader r = new BufferedReader(new InputStreamReader(stream));
			StringBuilder Stages = new StringBuilder();
			StringBuilder BusNoList = new StringBuilder();
			
			String line;
			String[] BusNoStation;

			while ((line = r.readLine()) != null) {
				BusNoStation=line.split(": ", 2);
				BusNoList.append(BusNoStation[0]).append(",");
			    Stages.append(BusNoStation[1]).append(",");
			    stages_row.add(BusNoStation[1].toString());
			}
			BusNumbers = BusNoList.toString().replace("- ", "");
			StagesList = Stages.toString();
			String debug_test = stages_row.get(0);
			int length = stages_row.size();
		}
   }
	

}
