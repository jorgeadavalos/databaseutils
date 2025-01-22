package com.assoc.jad.database.tools;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

public class SocketClient extends ASocketComm implements Callable<JSONObject> {
	private static final Log LOGGER = LogFactory.getLog(SocketClient.class);
    
    private HashMap<String,Object> inputs;
    
    public SocketClient(HashMap<String,Object> inputs) {
    	this.inputs = inputs;
    }
	private JSONObject callMethod() {
		execMethod(inputs);
		LOGGER.debug("finished "+(String)inputs.get("method") + " ..");
		return getJsonObj();
	}
	@Override
	public JSONObject call() throws Exception {
		return callMethod();
	}
}
