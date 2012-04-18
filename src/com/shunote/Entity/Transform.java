package com.shunote.Entity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Transform {
	
	private static Transform instance = null;
	
	private Transform(){};
	
	/**
	 * Singleton
	 * @return instance
	 */
	public static Transform getInstance(){
		if (instance == null) {
			instance = new Transform();
			return instance;
		}else{
			return instance;
		}
	}
	
	
	/**
	 * JSON TO Node
	 * @param json
	 * @return root
	 * @throws JSONException
	 */
	public Node json2Node(JSONObject json) throws JSONException{
		Node root = new Node(json.getInt("node"),json.getString("title"),json.isNull("content")?null:json.getString("content"),null,null);
		Node father = root;
		JSONArray sons = json.getJSONArray("sons");
		Log.v("Jeffrey","root:"+root.getTitle());
		Transform.getInstance().jsonTraverse(sons, father);		
		return root;
	}
	
	/**
	 * JSON Traverse
	 * @param sons
	 * @param father
	 * @throws JSONException
	 */
	public void jsonTraverse(JSONArray sons, Node father) throws JSONException{
		for(int i=0;i<sons.length();i++){
			int id = sons.getJSONObject(i).getInt("node");
			String title = sons.getJSONObject(i).getString("title");
			String content = sons.getJSONObject(i).isNull("content")?null:sons.getJSONObject(i).getString("content");
			//String img = sons.getJSONObject(i).getString("img");
			Node son = new Node(id,title,content,null,father);
			father.addSons(son);
			JSONArray newsons = sons.getJSONObject(i).getJSONArray("sons");
			if(newsons.length()>0){
				jsonTraverse(newsons,son);
			}
		}
	}
	
	/**
	 * Nodeת to JSON
	 * @param root
	 * @return JSONObject
	 * @throws JSONException
	 */
	public JSONObject node2Json(Node root) throws JSONException{
		JSONObject result = new JSONObject();
		result.put("node",root.getId());
		result.put("title",root.getTitle());
		result.put("content", root.getContent());		
		List<Node> sons = root.getSons();
		JSONArray jsons = new JSONArray();
		Transform.getInstance().nodeTraverse(sons, jsons);
		result.put("sons",jsons);		
		return result;
	}
	
	/**
	 * Node Traverse
	 * @param sons
	 * @param sonsOfFather
	 * @throws JSONException
	 */
	public void nodeTraverse(List<Node> sons , JSONArray sonsOfFather) throws JSONException{
		
		for (int i = 0; i < sons.size(); i++) {
			Node son = sons.get(i);
			JSONObject json = new JSONObject();
			json.put("node",son.getId());
			json.put("title",son.getTitle());
			json.put("content", son.getContent());
			if(son.getSons()!=null){
				JSONArray jsons = new JSONArray();
				nodeTraverse(son.getSons(),jsons);
				json.put("sons", jsons);
			}else{
				json.put("sons", "[]");
			}
			sonsOfFather.put(json);
		}
	}
	
	/**
	 * BMP to String
	 * @param bmp
	 * @return Base64
	 */
	public String bmp2String(Bitmap bmp){
		 ByteArrayOutputStream stream = new ByteArrayOutputStream();
		 bmp.compress(Bitmap.CompressFormat.PNG, 100, stream );
		 byte bytes[] = stream.toByteArray();
		 String base64 = Base64.encodeToString(bytes, Base64.DEFAULT); 
		 return base64;
	}
	
	/**
	 * String to BMP
	 * @param base64
	 * @return BMP
	 */
	public Bitmap String2Bmp(String base64){
		 byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
		 Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); 
		 return bmp;
	}
}
