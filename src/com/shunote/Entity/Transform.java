package com.shunote.Entity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Transform {
	
	private static Transform instance = null;
	
	private Transform(){};
	
	/**
	 * Singleton模式
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
	 * 将JSON转化为Node节点
	 * @param json
	 * @return root节点
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
	 * JSON递归遍历
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
	 * Node转化为JSON
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
	 * Node递归遍历
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
}
