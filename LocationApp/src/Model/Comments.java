package Model;

import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.location.Location;
// this is a master comment class
public class Comments implements Serializable{
	private int master_comment_ID;
	private int sub_comments_ID;
	private int master_ID;
	private int sub_ID;
	private String the_comment,subject_comment;
	//Location comment_location;
	private Date comment_date;
	private boolean master_comment;
	private Location comment_location ;
	private double lon ;
	private double lat;
	private double distance;
	private Bitmap comment_image;
	private String image_encode;
	private int userID;
	//consturctor for creating a master comment
	public Comments(int user_id,int masterid , int sid, int subid,String title, String subject,Date the_date,Location location,double lon,double lat)
	{   
		this.master_ID=masterid;
		this.sub_comments_ID=sid;
		this.sub_ID= subid;
		//this.comment_location= the_location;
		this.comment_date=the_date;
		this.the_comment=title;
		this.subject_comment=subject;
		this.master_comment=true;
		this.comment_location=location;
		this.lon = lon;
		this.lat = lat;
		this.userID=user_id;
		
	}
	// constrcutor for creating a sub comment;
	/*public Comments(int master_commentid,int masterid , int subid, DateTime the_date,String comment,boolean master)
	{   this.master_comment_ID=master_commentid;
		this.master_ID=masterid;
		this.sub_ID= subid;
		//this.comment_location= the_location;
		//this.comment_date=the_date;
		this.the_comment=comment;
		this.master_comment=false;
	}*/
	
	// make a comments with image in them
	public Comments(int user_id,int masterid , int sid,int subid,String title, String subject,Date the_date,Location location,double lon,double lat,String encode)
	{   
		this.master_ID=masterid;
		this.sub_comments_ID=sid;
		this.sub_ID= subid;
		//this.comment_location= the_location;
		this.comment_date=the_date;
		this.the_comment=title;
		this.subject_comment=subject;
		this.master_comment=true;
		this.comment_location=location;
		this.lon = lon;
		this.lat = lat;
		this.image_encode=encode;
		this.userID=user_id;
	}

	public int getMaster_comment_ID() {
		return master_comment_ID;
	}

	public void setMaster_comment_ID(int master_comment_ID) {
		this.master_comment_ID = master_comment_ID;
	}

	public int getSub_comments_ID() {
		return sub_comments_ID;
	}

	public void setSub_comments_ID(int sub_comments_ID) {
		this.sub_comments_ID = sub_comments_ID;
	}

	public int getMaster_ID() {
		return master_ID;
	}

	public void setMaster_ID(int master_ID) {
		this.master_ID = master_ID;
	}

	public int getSub_ID() {
		return sub_ID;
	}

	public void setSub_ID(int sub_ID) {
		this.sub_ID = sub_ID;
	}

	public String getThe_comment() {
		return the_comment;
	}

	public void setThe_comment(String the_comment) {
		this.the_comment = the_comment;
	}

	public String getSubject_comment() {
		return subject_comment;
	}

	public void setSubject_comment(String subject_comment) {
		this.subject_comment = subject_comment;
	}

	public Date getComment_date() {
		return comment_date;
	}

	public void setComment_date(Date comment_date) {
		this.comment_date = comment_date;
	}

	public boolean isMaster_comment() {
		return master_comment;
	}

	public void setMaster_comment(boolean master_comment) {
		this.master_comment = master_comment;
	}

	public Location getComment_location() {
		return comment_location;
	}

	public void setComment_location(Location comment_location) {
		this.comment_location = comment_location;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Bitmap getComment_image() {
		return comment_image;
	}

	public void setComment_image(Bitmap comment_image) {
		this.comment_image = comment_image;
	}

	public String getImage_encode() {
		return image_encode;
	}

	public void setImage_encode(String image_encode) {
		this.image_encode = image_encode;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	

}