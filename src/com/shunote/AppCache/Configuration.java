package com.shunote.AppCache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import android.content.Context;
import android.util.Log;

public class Configuration {
	   private  Properties propertie;
	   private  InputStream inputFile;
	   private String tag = "Config"; 
	      /** 
	      * 初始化Configuration类
	       */ 
	      public  Configuration(Context con)
	      {
	         propertie  =   new  Properties();
	          try   {
	             inputFile  =  con.getResources().getAssets().open("settings.properties");
	             propertie.load(inputFile);
	             inputFile.close();
	         }   catch  (FileNotFoundException ex)  {
	             Log.e( tag," 读取属性文件--->失败！- 原因：文件路径错误或者文件不存在 " );
	             ex.printStackTrace();
	         }   catch  (IOException ex)  {
	             Log.e(tag, " 装载文件--->失败! " );
	             ex.printStackTrace();
	         } 
	     } // end ReadConfigInfo(...) 
	     
	      /** 
	      * 重载函数，得到key的值
	      *  @param  key 取得其值的键
	      *  @return  key的值
	       */ 
	      public  String getValue(String key)
	      {
	          if (propertie.containsKey(key)) {
	             String value  =  propertie.getProperty(key); // 得到某一属性的值 
	              return  value;
	         } 
	          else  
	              return   "" ;
	     } // end getValue(...) 

}
