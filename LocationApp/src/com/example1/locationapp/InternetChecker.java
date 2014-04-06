package com.example1.locationapp;

import java.io.IOException;
import java.util.Date;

import InternetConnection.ConnectToInternet;
import Model.Comments;
import Model.IDModel;
import Model.SubCommentModel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * This class will check the if the Internet service in on or not.
 * 
 * @author qyu4
 * 
 */
public class InternetChecker extends BroadcastReceiver {
	/**
	 * this is trying to received network states, if the app is connected to
	 * internet then it will start loading in mainactivity
	 * 
	 */
	private int MasterId;
	private boolean connect = false;
	private double lat;
	private double lon;
	public boolean connected(Context context)
	{
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isConnected() || mobile.isConnected()) 
		{
			return true;
		}
		else
			return connect;
	}
	@Override
	public void onReceive(final Context context, Intent intent) {
		final GPSTracker gps = new GPSTracker(context);
		
		connect = true;
		final ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		/**
		 * check for local comments, if there is local saved comments then push to server
		 * 
		 */
		
		SharedPreferences sharedPref = context.getSharedPreferences("mydata",Context.MODE_PRIVATE);
		final String title = sharedPref.getString("title","");
		final String subject = sharedPref.getString("subject", "");
		final String picture = sharedPref.getString("image", "");
		final String name = sharedPref.getString("name", "");
		if(!title.equals(""))
		{	
			// make new comments
			if(picture.equals(""))
			{
				new AsyncTask<Void,Void,Void>() {
					
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
						// get master ID
						new AsyncTask<Void, Void, Void>()
						{

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub
								ConnectToInternet con = new ConnectToInternet();
								MasterId = con.get_id(context);
								MasterId++;
								return null;
							}
							
						}.execute();
					}


					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						// make new comments
						Comments NewComment = new Comments(0, MasterId, 0, 0, title, subject, new Date(),gps.getLongitude(),gps.getLatitude(),name);
						SubCommentModel modle = new SubCommentModel(NewComment);
						modle.insertMaster(NewComment, MasterId);
						System.out.println("fale");
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						new AsyncTask<Void,Void	,Void>()
						{

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub
								ConnectToInternet internet = new ConnectToInternet();
								MasterId++;
								IDModel id_obj= new IDModel(MasterId);
								try {
									internet.insert(id_obj, context);
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
							
						}.execute();
					}
				}.execute();
			}
			// no picture
			else
			{

				new AsyncTask<Void,Void,Void>() {
					
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
						// get master ID
						new AsyncTask<Void, Void, Void>()
						{

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub
								ConnectToInternet con = new ConnectToInternet();
								MasterId = con.get_id(context);
								MasterId++;
								return null;
							}
							
						}.execute();
					}


					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						// make new comments with picture
						
						JsonElement element = new JsonParser().parse(picture);
						System.out.println("new picture:"+element.toString());
						Comments NewComment = new Comments(0, MasterId, 0, 0, title, subject, new Date(),gps.getLongitude(),gps.getLatitude(),element,name);
						SubCommentModel modle = new SubCommentModel(NewComment);
						modle.insertMaster(NewComment, MasterId);
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						new AsyncTask<Void,Void	,Void>()
						{

							@Override
							protected Void doInBackground(Void... params) {
								// TODO Auto-generated method stub
								ConnectToInternet internet = new ConnectToInternet();
								MasterId++;
								IDModel id_obj= new IDModel(MasterId);
								try {
									internet.insert(id_obj, context);
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
							
						}.execute();
					}
				}.execute();
			}
		}
		System.out.println("title is:"+title);
		sharedPref.edit().clear().commit();
		/**
		 * checking the internet connection
		 * 
		 */
		if (wifi.isConnected() || mobile.isConnected()) {
			Toast.makeText(context, "Connected to Internet", Toast.LENGTH_LONG)
					.show();
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("New Comments")
					.setContentText("Hey, you got new comments!");
		
			
			//upload saved comment to internet;
			Intent resultIntent = new Intent(context, MainActivity.class);

			// The stack builder object will contain an artificial back stack
			// for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out
			// of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainPage.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(1, mBuilder.build());
		} else {
			Toast.makeText(context, "No Internet!", Toast.LENGTH_LONG).show();
		}

	}

}
