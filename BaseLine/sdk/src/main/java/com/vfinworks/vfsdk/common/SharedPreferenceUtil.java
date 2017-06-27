package com.vfinworks.vfsdk.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存的工具类
 *
 */
public class SharedPreferenceUtil {
	public static final String TOKEN="token";
	public static final String TIME="time";
	private static SharedPreferenceUtil mInstance = null;
	
	public static SharedPreferenceUtil getInstance() {
		if(mInstance == null) {
			mInstance = new SharedPreferenceUtil();
		}
		
		return mInstance;
	}
	private Context mAppContext;
	/*
	 * the default mode, where the created file can only be accessed by the calling application 
	 * (or all applications sharing the same user ID).
	 */
	public final int MODE_PRIVATE = Context.MODE_PRIVATE;
	/*
	 * allow all other applications to have read access to the created file
	 * This constant was deprecated in API level 17.
	 */
	public final int MODE_WORLD_READABLE = 1;
	/*
	 * allow all other applications to have write access to the created file.
	 * This constant was deprecated in API level 17.
	 */
	public final int MODE_WORLD_WRITEABLE = 2;

	public void init(Context context) {
		if (context == null)
			throw new IllegalArgumentException("context can not be null");
		mAppContext = context;
	}

	/**
	 * 将一个String数据存入到缓存中
	 * @param keyStr 要存入缓存中的key
	 * @param valueStr 要存入缓存中的value
	 */
	public void setStringDataIntoSP(String keyStr, String valueStr) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().putString(keyStr, valueStr).commit();
	}

	/**
	 * 将一个Boolean数据存入到缓存中
	 * @param keyStr 要存入缓存中的key
	 * @param valueStr 要存入缓存中的value
	 */
	public void setBooleanDataIntoSP(String keyStr, Boolean valueStr) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().putBoolean(keyStr, valueStr).commit();
	}

	/**
	 * 将一个Int数据存入到缓存中
	 * @param keyStr 要存入缓存中的key
	 * @param valueStr 要存入缓存中的value
	 */
	public void setIntDataIntoSP( String keyStr, int valueStr) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().putInt(keyStr, valueStr).commit();
	}

	/**
	 * 将一个Float数据存入到缓存中
	 * @param keyStr 要存入缓存中的key
	 * @param valueStr 要存入缓存中的value
	 */
	public void setFloatDataIntoSP( String keyStr, Float valueStr) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().putFloat(keyStr, valueStr).commit();
	}

	/**
	 * 将一个Long数据存入到缓存中
	 * @param keyStr 要存入缓存中的key
	 * @param valueStr 要存入缓存中的value
	 */
	public void setLongDataIntoSP( String keyStr, Long valueStr) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().putLong(keyStr, valueStr).commit();
	}

	/**
	 * 获取缓存中的一个String数据
	 * @param keyStr 已存入缓存中的key
	 * @return 缓存中对应参数key的value
	 */
	public String getStringValueFromSP( String keyStr) {
		return getStringValueFromSP(keyStr, "");
	}

	/**
	 * 获取缓存中的一个String数据
	 * @param keyStr 已存入缓存中的key
	 * @param defaultValue 缓存中对应参数key的默认值
	 * @return 缓存中对应参数key的value
	 */
	public String getStringValueFromSP( String keyStr, String defaultValue) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.getString(keyStr, defaultValue);
	}

	/**
	 * 获取缓存中的一个Float数据
	 * @param keyStr 已存入缓存中的key
	 * @return 缓存中对应参数key的value
	 */
	public Float getFloatValueFromSP( String keyStr) {
		return getFloatValueFromSP( keyStr, 0.0f);
	}

	/**
	 * 获取缓存中的一个Float数据
	 * @param keyStr 已存入缓存中的key
	 * @param defaultValue 缓存中对应参数key的默认值
	 * @return 缓存中对应参数key的value
	 */
	public Float getFloatValueFromSP( String keyStr, Float defaultValue) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.getFloat(keyStr, defaultValue);
	}

	/**
	 * 获取缓存中的一个Int数据
	 * @param keyStr 已存入缓存中的key
	 * @return 缓存中对应参数key的value
	 */
	public int getIntValueFromSP(String keyStr) {
		return getIntValueFromSP(keyStr, 0);
	}

	/**
	 * 获取缓存中的一个Int数据
	 * @param keyStr 已存入缓存中的key
	 * @param defaultValue 缓存中对应参数key的默认值
	 * @return 缓存中对应参数key的value
	 */
	public int getIntValueFromSP( String keyStr, int defaultValue) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.getInt(keyStr, defaultValue);
	}

	/**
	 * 获取缓存中的一个Boolean数据
	 * @param keyStr 已存入缓存中的key
	 * @return 缓存中对应参数key的value
	 */
	public boolean getBooleanValueFromSP( String keyStr) {
		return getBooleanValueFromSP( keyStr, false);
	}

	/**
	 * 获取缓存中的一个Boolean数据
	 * @param keyStr 已存入缓存中的key
	 * @param defaultValue 缓存中对应参数key的默认值
	 * @return 缓存中对应参数key的value
	 */
	public boolean getBooleanValueFromSP(String keyStr, Boolean defaultValue) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.getBoolean(keyStr, defaultValue);
	}

	/**
	 * 获取缓存中的一个Long数据
	 * @param keyStr 已存入缓存中的key
	 * @return 缓存中对应参数key的value
	 */
	public Long getLongValueFromSP( String keyStr) {
		return getLongValueFromSP( keyStr, 0l);
	}

	/**
	 * 获取缓存中的一个Long数据
	 * @param keyStr 已存入缓存中的key
	 * @param defaultValue 缓存中对应参数key的默认值
	 * @return 缓存中对应参数key的value
	 */
	public Long getLongValueFromSP( String keyStr, Long defaultValue) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.getLong(keyStr, 0l);
	}

	/**
	 * 将键值对数组，存入到缓存中
	 * @param keyValueMap 要存入缓存中的键值对
	 */
	public void setDataIntoSP(HashMap<String, Object> keyValueMap) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		Editor editor = sp.edit();
		if(keyValueMap != null && !keyValueMap.isEmpty()) {
			Set<String> keySet = keyValueMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				Object value = keyValueMap.get(key);
				if(value.getClass() == String.class) {
					editor.putString(key, (String)value);
				} else if(value.getClass() == Integer.class) {
					editor.putInt(key, (Integer)value);
				} else if(value.getClass() == Boolean.class) {
					editor.putBoolean(key, (Boolean)value);
				} else if(value.getClass() == Long.class) {
					editor.putLong(key, (Long)value);
				} else if(value.getClass() == Float.class) {
					editor.putFloat(key, (Float)value);
				}
			}
			editor.commit();
		}
	}

	/**
	 * 获取多个key值对应的values
	 * @param keyValueMap 要获取的缓存中的key值
	 * @return
	 */
	public List<Object> getValuesFromSP(HashMap<String, Object> keyValueMap) {
		List<Object> values = new ArrayList<Object>();
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		if(keyValueMap != null && !keyValueMap.isEmpty()) {
			Set<String> keySet = keyValueMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				Object value = keyValueMap.get(key);
				if(value == String.class) {
					values.add(sp.getString(key, ""));
				} else if(value == Integer.class) {
					values.add(sp.getInt(key, 0));
				} else if(value == Boolean.class) {
					values.add(sp.getBoolean(key, false));
				} else if(value == Long.class) {
					values.add(sp.getLong(key, 0l));
				} else if(value == Float.class) {
					values.add(sp.getFloat(key, 0.0f));
				}
			}
		}
		return values;
	}

	/**
	 * 获取缓存中所有的数据
	 * @return 
	 */
	public Map<String, ?> getAllFromSP() {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(),MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * 验证缓存中是否已经有某个key值
	 * @param key 要查询的key值
	 * @return
	 */
	public  boolean hasKeyInSP( String key) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 删除缓存中的某个键值对
	 * @param key 欲删除的缓存中的key值
	 */
	public void deleteValueInSP( String key) {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		if(sp.contains(key)) {
			sp.edit().remove(key).commit();
		}
	}

	/**
	 * 删除缓存中的所有值
	 */
	public void deleteAllInSP() {
		SharedPreferences sp = mAppContext.getSharedPreferences(mAppContext.getPackageName(), MODE_PRIVATE);
		sp.edit().clear().commit();
	}
}
