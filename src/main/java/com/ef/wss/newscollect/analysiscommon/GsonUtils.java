/*
 * Copyright (c) 2010-2015 EEFUNG Software Co.Ltd. All rights reserved.
 * 版权所有(c)2010-2015湖南蚁坊软件有限公司。保留所有权利。
 */
package com.ef.wss.newscollect.analysiscommon;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

import org.apache.avro.util.Utf8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * <p>Gson 工具类。<p>
 * 
 * @author chendan{chendan@eefung.com)
 * 
 */
public class GsonUtils {


	/**
	 * 获取一个支持格式化 {@link CharSequence} 类型数据的Gson对象.
	 * 
	 * @return
	 */
	public static Gson getGson() {
		return getGson(false);
	}
	
	/**
	 * 获取一个支持格式化 {@link CharSequence} 类型数据的Gson对象，使用该对象序列化一个对象得到的json数据会进行格式化。
	 * 
	 * @return
	 */
	public static Gson getPrettyGson() {
		return getGson(true);
	}
	
	private static Gson getGson(boolean isPrettyPrinting) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (isPrettyPrinting) {
			gsonBuilder.setPrettyPrinting();
		}
		gsonBuilder.registerTypeAdapter(CharSequence.class, new CharSequenceDeserializer());
		gsonBuilder.registerTypeAdapter(ByteBuffer.class, new ByteBufferDeserializer());
		gsonBuilder.registerTypeAdapter(ByteBuffer.class, new ByteBufferSerializer());
		gsonBuilder.registerTypeAdapter(Utf8.class, new Utf8Serializer());

		return gsonBuilder.create();
	}

	private static class ByteBufferSerializer implements JsonSerializer<ByteBuffer> {

		@Override
		public JsonElement serialize(ByteBuffer src, Type typeOfSrc, JsonSerializationContext context) {
			return new Gson().toJsonTree(src.array());
		}

	}

	private static class ByteBufferDeserializer implements JsonDeserializer<ByteBuffer> {

		@Override
		public ByteBuffer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			return ByteBuffer.wrap(new Gson().fromJson(json, byte[].class));
		}

	}

	private static class Utf8Serializer implements JsonSerializer<Utf8> {

		@Override
		public JsonElement serialize(Utf8 src, Type typeOfSrc, JsonSerializationContext context) {
			return new Gson().toJsonTree(src.toString());
		}

	}

	private static class CharSequenceDeserializer implements JsonDeserializer<CharSequence> {

		@Override
		public CharSequence deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

			return json.getAsString();

		}

	}
}
