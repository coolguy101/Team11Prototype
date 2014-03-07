package com.example1.locationapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Model.Comments;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SubCommetsRead extends Activity {
	public static final String SERVER = "http://cmput301.softwareprocess.es:8080/cmput301w14t11/";
	public static final String MASTERCOMMENT = "emouse/";
	private ListView listViewSubComment;
	private EditText editText;
	private Button button1;
	private cutadapter ListAdapter;
	private ArrayList<Comments> comment_list; 
	private Bitmap bitmap;
	private int number; 
	private Context content;
	private Location location;
    private GPSTracker gps;
    private HttpClient httpclient;
    private double longitude;
    private double latitude;
    private Gson gson = new Gson();
    double radius= 0.01;
    //private EnterCommentsActivity callEnterComments = new EnterCommentsActivity();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_sub_commets_read);
	listViewSubComment = (ListView)findViewById(R.id.listViewSubComments);
	editText = (EditText)findViewById(R.id.editTextSubmitSubComments);
	button1 = (Button)findViewById(R.id.buttonSaveSubComments);
	button1.setText("Submit Sub Comments");
    comment_list = new ArrayList<Comments>();
    httpclient= new DefaultHttpClient();
	Intent intent = getIntent();
	number=intent.getIntExtra("masterID", 999999);
	Toast.makeText(getBaseContext(), number+"", Toast.LENGTH_SHORT).show();

    // add an example to test the list
    //comment_list.add(new Comments(1,0,0, 0, "It works", "Tesing", new Date(), null, 123, 123, null));
	ListAdapter = new cutadapter(this, R.layout.listlayout, comment_list);
	gps = new GPSTracker(this);
	
	new AsyncTask<Void, Void, Void>()
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			get_comments("get some comments man!");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ListAdapter.notifyDataSetChanged();
		}
		
	}.execute();
	
	content = this;
	if (gps.canGetLocation){
	location = gps.getLocation();
	System.out.println(location+"wocao");
	}
			
			
			
    //number = 0;
    //get comments
			
    System.out.println("lol"+location);
			
    longitude = location.getLongitude();
    latitude =location.getLatitude();
	
	listViewSubComment.setAdapter(ListAdapter);
	button1.setOnClickListener(new MyButton1Listener());
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.sub_commets_read, menu);
	return true;
	}
	class MyButton1Listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String title = editText.getText().toString();
			radius=radius+0.01;
	        if ("".equals(title))
	        {   
	        	
	        	Toast.makeText(getBaseContext(), "Title is empty! add some words please!", Toast.LENGTH_SHORT).show();
	        	
	        }else{
			new AsyncTask<Void,Void,Void>()
	    	{   ProgressDialog dialog1= new ProgressDialog(content);
	    		
	    		@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					dialog1.setTitle("Loading cause your internet is too slow!");
					dialog1.show();
					super.onPreExecute();
				}
				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					if(bitmap==null)
				       { 	   
				         final Comments new_comment = new Comments(0,0,1,0,editText.getText().toString(),editText.getText().toString(),new Date(),location,longitude,latitude);
				         insertMaster(new_comment);
				       }
				       else
				       { System.out.println("image posted");
	                         			       
				         String encode_image= convert_image_to_string(bitmap);
				    	 final Comments new_comment = new Comments(0,0,1,0,editText.getText().toString(),editText.getText().toString(),new Date(),location,longitude,latitude,encode_image);
				    	 insertMaster(new_comment);
				       }
					
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					dialog1.dismiss();
					super.onPostExecute(result);
					
					
				}
	    		
	    	}.execute();
	       
	    	setResult(RESULT_OK);
	    	finish();
		}
		}
	}
	public void insertMaster(Comments comm)
	 {
		 HttpClient httpclient  = new DefaultHttpClient();
		 HttpPost httpPost = new HttpPost(SERVER+MASTERCOMMENT+number);
		 try {
			StringEntity data = new StringEntity(gson.toJson(comm));
			httpPost.setEntity(data);
			httpPost.setHeader("Accept","application/json");
			HttpResponse response = httpclient.execute(httpPost); 
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	public String  convert_image_to_string(Bitmap bitmap)
	 {
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
		 bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		 byte[] byteArray = byteArrayOutputStream .toByteArray();
		 String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
		return encoded;
	 }
	String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader((response.getEntity().getContent())));
		String output;
		System.err.println("Output from Server -> ");
		String json = "";
		while ((output = br.readLine()) != null) {
			System.err.println(output);
			json += output;
		}
		System.err.println("JSON:"+json);
		return json;
	}
	
	public  void get_comments(String url)
	{
	HttpPost httpPost= new HttpPost("http://cmput301.softwareprocess.es:8080/cmput301w14t11/emouse/_search?pretty=1");
	//HttpGet  httpGet = new HttpGet("http://cmput301.softwareprocess.es:8080/testing/emouse/_search?pretty=1");
	Gson gson1 = new Gson();
	try {
		ArrayList<Comments> lat_object = new ArrayList<Comments>();
		ArrayList<Comments> lon_object = new ArrayList<Comments>();
		String query_range2 = "{\"query\":{\"bool\":{\"must\":{\"match\":{\"master_ID\":"+number+"}}} }}";
		// these are unused query , this is just for testing
		//String query = "{\"query\":{\"range\":{\"lat\":{\"gte\":-200,\"lte\":200,\"boost\":0.0} }}}";
		//String query = "{\"query\":{\"range\":{\"lat\":{\"gte\":"+lat_gte+",\"lte\":"+ lat_lte +",\"boost\":0.0} }}}";
		///String query2 = "{\"query\":{\"range\":{\"lon\":{\"gte\":"+lon_gte+",\"lte\":"+ lon_lte +",\"boost\":0.0} }}}";
		//String query = "{\"query\":{\"range\":{\"lat\":{\"gte\":-200,\"lte\":200,\"boost\":0.0} }}}";
		//String query = "{\"query\":{\"range\":{\"lat\":{\"gte\":"+lat_gte+",\"lte\":"+ lat_lte +",\"boost\":0.0},\"lon\":{\"gte\":"+lon_gte+",\"lte\":"+ lon_lte +",\"boost\":0.0} }}}";
		//String query1 = "{\"query\":{\"query_string\":{\"default_field\":\"master_ID\",\"query\":15}}}";
		//String query_location ="{\"query\": {\"geo_shape\": {\"location\": {\"shape\": {\"type\": \"envelope\",\"coordinates\": [[13, 53],[14, 52]]}}}}}";
		StringEntity entity = new StringEntity(query_range2);
		httpPost.setHeader("Accept","application/json");
		httpPost.setEntity(entity);
		HttpResponse response = httpclient.execute(httpPost);
		String json1 = getEntityContent(response);
		System.out.println(json1+"holy");
		Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Comments>>(){}.getType();
		ElasticSearchSearchResponse<Comments> esResponse = gson1.fromJson(json1, elasticSearchSearchResponseType);
//<<<<<<< HEAD
		for (ElasticSearchResponse<Comments> r : esResponse.getHits()) {
			Comments comms = r.getSource();

			//check weath the comment if already in the arraylist, if not then add it in there
			int flag=0;
			for (Comments com : comment_list)
			{ // turn on the flag if object is already inside the arary
			if(com.master_ID==comms.master_ID)
			{
			flag =1 ;
			break;
			}
			}
			// if flag not turned on then add the object into the arraylsit
			if (flag==0)
			{
			comment_list.add(comms);
			}

		    }
		}
      catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		System.out.println("client exe");
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("IO exe");
		e.printStackTrace();}
	}
}


