//package com.shunote.AppCache;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import com.shunote.Entity.Node;
//import com.shunote.Entity.Note;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.database.sqlite.SQLiteDatabase;
//
///**
// * Cache层操作方法类
// * @author Jeffrey
// *
// */
//public class Cache extends Activity{
//	
//	private static Cache instance = null;
//	
//	private SQLiteDatabase db;
//	
//	private DBHelper dbHelper;
//	
//	private SharedPreferences sp = this.getSharedPreferences("USER", MODE_PRIVATE);
//	
//	private Cache(){};
//	
//	/**
//	 * Singleton模式
//	 * @return instance
//	 */
//	public static Cache getInstance(){
//		if (instance == null) {
//			instance = new Cache();
//			return instance;
//		}else{
//			return instance;
//		}
//	}
//	
//	/**
//	 * 从server获取JSON
//	 * @param url
//	 * @return JSON
//	 * @throws JSONException
//	 */
//	public JSONObject getJSON(String url) throws JSONException{
//		url = "http://shunote.com/"+url;
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpGet httpGet = new HttpGet(url);
//		String strResult = "";
//		try{
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//				strResult = EntityUtils.toString(httpResponse.getEntity());
//			}
//		}catch(ClientProtocolException e){			 
//			    e.printStackTrace(); 			 
//		} catch (IOException e) { 			 
//			    e.printStackTrace(); 			 
//		} 
//		return new JSONObject(strResult);
//	}
//	
//	/**
//	 * 以名值对的方式post数据
//	 * @param url
//	 * @param pairs NameValuePairs
//	 * @return
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public String postData(String url,List<NameValuePair> pairs) throws ClientProtocolException, IOException{ 
//		url = "http://shunote.com/"+url;
//		HttpPost httpPost = new HttpPost(url);
//		httpPost.setEntity(new UrlEncodedFormEntity(pairs));
//		HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
//		String result = EntityUtils.toString(httpResponse.getEntity());
//		result= "";
//		for(org.apache.http.Header h : httpResponse.getHeaders("cookie"))
//			result = result+"/n"+ "cookie:"+h.getName()+", "+h.getValue();
//		
//		return result;
//	}
//	
//	/**
//	 * 将JSON转化为Node节点
//	 * @param json
//	 * @return root节点
//	 * @throws JSONException
//	 */
//	public Node json2Node(JSONObject json) throws JSONException{
//		Node root = new Node(json.getInt("node"),json.getString("nickname"),json.getString("description"),json.getString("img"),null);
//		Node father = root;
//		JSONArray sons = json.getJSONArray("sons");
//		Cache.getInstance().jsonTraverse(sons, father);		
//		return root;
//	}
//	
//	/**
//	 * JSON递归遍历
//	 * @param sons
//	 * @param father
//	 * @throws JSONException
//	 */
//	public void jsonTraverse(JSONArray sons, Node father) throws JSONException{
//		for(int i=0;i<sons.length();i++){
//			int id = sons.getJSONObject(i).getInt("node");
//			String title = sons.getJSONObject(i).getString("nickname");
//			String description = sons.getJSONObject(i).getString("description");
//			String img = sons.getJSONObject(i).getString("img");
//			Node son = new Node(id,title,description,img,father);
//			father.addSons(son);
//			JSONArray newsons = sons.getJSONObject(i).getJSONArray("sons");
//			if(newsons.length()>0){
//				jsonTraverse(newsons,son);
//			}
//		}
//	}
//	
//	/**
//	 * Node转化为JSON
//	 * @param root
//	 * @return JSONObject
//	 * @throws JSONException
//	 */
//	public JSONObject node2Json(Node root) throws JSONException{
//		JSONObject result = new JSONObject();
//		result.put("node",root.getId());
//		result.put("nickname",root.getTitle());
//		result.put("description", root.getContent());		
//		List<Node> sons = root.getSons();
//		JSONArray jsons = new JSONArray();
//		Cache.getInstance().nodeTraverse(sons, jsons);
//		result.put("sons",jsons);		
//		return result;
//	}
//	
//	/**
//	 * Node递归遍历
//	 * @param sons
//	 * @param sonsOfFather
//	 * @throws JSONException
//	 */
//	public void nodeTraverse(List<Node> sons , JSONArray sonsOfFather) throws JSONException{
//		
//		for (int i = 0; i < sons.size(); i++) {
//			Node son = sons.get(i);
//			JSONObject json = new JSONObject();
//			json.put("node",son.getId());
//			json.put("nickname",son.getTitle());
//			json.put("description", son.getContent());
//			if(son.getSons()!=null){
//				JSONArray jsons = new JSONArray();
//				nodeTraverse(son.getSons(),jsons);
//				json.put("sons", jsons);
//			}else{
//				json.put("sons", "[]");
//			}
//			sonsOfFather.put(json);
//		}
//		
//	}
//	
//	/**
//	 * 初始化数据库
//	 */
//	public void initDB(){
//		dbHelper = new DBHelper(this);
//		db = dbHelper.getWritableDatabase() ;
//		dbHelper.onCreate(db);
//	}
//	
//	/**
//	 * 添加笔记
//	 * @param note
//	 * @throws JSONException
//	 * @throws ClientProtocolException
//	 * @throws IOException
//	 */
//	public void addNote(Note note) throws JSONException, ClientProtocolException, IOException{
//		
//		int userid = sp.getInt("ID",0);
//		String url = "/users/"+Integer.toString(userid)+"/nodes/";
//		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//		pairs.add(new BasicNameValuePair("title",note.getName()));
//		pairs.add(new BasicNameValuePair("parentid","-1"));
//		Cache.getInstance().postData(url, pairs);
//		
//		
//		dbHelper = new DBHelper(this);
//		dbHelper.insertNote(note);
//		
//	}
//	
//	
//	
//}
