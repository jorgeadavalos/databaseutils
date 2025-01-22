package com.assoc.jad.database.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;

public class ASocketComm {
	private static final Logger LOGGER = Logger.getLogger( ASocketComm.class.getName() );
	private Method[] methods = this.getClass().getSuperclass().getDeclaredMethods();
	private JSONObject jsonObj = new JSONObject();
	private Socket socket = null;
	
	protected void execMethod(HashMap<String, Object> inputs) {

		String methodName = (String)inputs.get("method");
		if (methodName == null || methodName.length() == 0) return;
		
		for (int i = 0; i < methods.length; i++) {
			String name = methods[i].getName();
			if (!name.equalsIgnoreCase(methodName)) continue;
			
			execute(methods[i],inputs);			
			break;
		}
	}
	private void execute(Method method,HashMap<String, Object> inputs) {
		Object[] arguments = new Object[] {inputs};
//		Object retObj = null;
		try {
			method.invoke(this,arguments);
//			retObj = method.invoke(this,arguments);
		} catch (Exception e) {
			String msg = "ASocketClient::execute invoked method failed "+method.getName()+" "+e;
			LOGGER.warning(msg);
		} 
	}
	@SuppressWarnings("unchecked")
	public void register(HashMap<String, Object> inputs) {
		try {
			if (!openSocket(inputs)) return;
			sendReq(inputs);
			String jsonStr = recvResp();
			JSONObject jsonObj = bldJson( jsonStr);
			JSONArray services = (JSONArray)jsonObj.get("service");
			services.forEach(service -> UtilsDatabaseStatics.SERVICES.put((String)service, (String)service) );
		} finally {
			if (socket != null) try { socket.close(); } catch (IOException e) {e.printStackTrace();};
		}
	}
	public void serviceRequest(HashMap<String, Object> inputs) {
		try {
			if (!openSocket(inputs)) return;
			sendReq(inputs);
			String jsonStr = recvResp();
			jsonObj = bldJson( jsonStr);
		} finally {
			if (socket != null) try { socket.close(); } catch (IOException e) {e.printStackTrace();};
		}
	}
	@SuppressWarnings("unchecked")
	private boolean openSocket(HashMap<String, Object> inputs) {		
		int port = (int) inputs.get("SERVICEPORT");
		String serviceHost = (String) inputs.get("SERVICEHOST");
		try {
			socket = new Socket(serviceHost,port);
			LOGGER.info(() -> "openSocket successful.!!");			
		}
		catch (IOException e) {
			LOGGER.info(() -> e.getMessage());
			jsonObj.put("infomsg", e.getMessage());
			long status = HttpStatus.GONE.value();
			jsonObj.put("status",status);
			return false;
		} 
		return true;
	}
	private void sendReq(HashMap<String, Object> inputs) {

		JSONObject json = (JSONObject) inputs.get("reqJson");
		Integer len = json.toString().length();
		String slen = len.toString() + System.lineSeparator();
		try {
			OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
			out.write(slen); out.flush();
			out.write(json.toString()); out.flush();			
			LOGGER.info(() -> "ClientMain "+json.toString());			
		}
		catch (IOException e) {
			LOGGER.info(() -> e.getMessage());
		}
	}
	private String recvResp() {
		StringBuffer sb = new StringBuffer();
		try {
			int len = 0;
			char[] chars = new char[4096];
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (( len = in.read(chars)) != -1) {
				sb.append(chars, 0, len);
			}
		}
		catch (IOException e) {
			LOGGER.info(() -> "recvResp " +e.getMessage());
		}
		return sb.toString();
	}
	protected JSONObject bldJson(String jsonString) {
		if (jsonString == null || jsonString.length() == 0) return null;
		JSONObject json = null;
		try {
			JSONParser parser = new JSONParser();
			json = (JSONObject) parser.parse(jsonString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return json;
	}
	/*
	 * getters and setters 
	 */
	public JSONObject getJsonObj() {
		return jsonObj;
	}
	public void setJsonObj(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
}
