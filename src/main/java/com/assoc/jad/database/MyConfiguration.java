package com.assoc.jad.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

import com.assoc.jad.database.tools.DatabaseUtilsStatics;
import com.assoc.jad.database.tools.SocketClient;

import jakarta.servlet.ServletContext;

@Configuration
public class MyConfiguration extends SpringBootServletInitializer implements ServletContextAware {
	@Value("${my.SERVERDISCOVERYPORT}") 		private int PORT;
	@Value("${my.SERVERDISCOVERYNAME}") 		private String LBNAME;
	@Value("${my.REGISTERASSERVICE}")			private boolean REGISTERASSERVICE;
	@Value("${spring.application.name}")		private String SERVICENAME;
	@Value("${my.DYNSERVERPORT}") 				private int DYNSERVERPORT;	//if server.port == 0 then actual port is not available yet.
	@Value("${server.port}")					private int PORTBOOTAPP;
	@Value("${server.servlet.context-path}")	private String CONTEXTPATH;
	@Value("${spring.mvc.view.suffix}")			private String SUFFIX;
	@Value("${my.DNS}")							private String DNS;
	
//	@Autowired
//    private ServletContext servletContext;
	@Override
	public void setServletContext(ServletContext servletContext) {
		String filename = this.getClass().getSimpleName();
		int ndx = filename.indexOf('$');
		if (ndx != -1) {
			filename = filename.substring(0, ndx);
		}
		readConfigFile("myApplication.config");
		registerService();
	}
	private void registerService() {
		if (!REGISTERASSERVICE) return;
		HashMap<String,Object> inputs = bldInputs();

		DatabaseUtilsStatics.SERVICETHREADS.submit( new SocketClient(inputs));
	}
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> bldInputs() {
		HashMap<String, Object> inputs = new HashMap<>();

		String method = "register";
		if (CONTEXTPATH != null || CONTEXTPATH.length() > 0) CONTEXTPATH = CONTEXTPATH.substring(1);
		if (CONTEXTPATH == null || CONTEXTPATH.length() == 0) CONTEXTPATH = SERVICENAME;
		String welcome = "/index.xhtml";
		if (SUFFIX != null) welcome = "/index"+SUFFIX;
		String instanceURL = "http://" ;
		DNS = DNS.trim();
		
		try {
			if (DNS.length() > 0) instanceURL += DNS;
			else instanceURL += InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		instanceURL += ":"+PORTBOOTAPP+"/"+CONTEXTPATH+welcome;

		JSONObject json = new JSONObject();
		json.put("application",SERVICENAME);
		json.put("instance",instanceURL);
		json.put("welcome",welcome);
		json.put("ctr","0");
		json.put("method",method);
		json.put("dns",DNS);
//		json.put("jsonlbobj",bldLoadBalancerObj());

		inputs.put("reqJson", json);
		inputs.put("SERVICEPORT",PORT);
		inputs.put("SERVICEHOST",LBNAME);
		inputs.put("method",method);		//TODO use reflection to create list of method available.
		
		return inputs;
	}
	private void readConfigFile(String filename) {
		if (filename == null || filename.length() == 0 ) return;
		BufferedReader inpf = null;		
		try {
			InputStream is = getClass().getResourceAsStream("/" + filename);
			if (is == null) return;
			
			inpf = new BufferedReader(new InputStreamReader(is));
			
			inpf.lines().parallel()
				.filter(str -> (!str.startsWith("#") && str.length() > 0 && str.trim().startsWith("-D"))) 
			    .map(b -> b.trim().split("="))
//			    .forEach(b->System.out.println(b[0].trim()+" "+ b[1].trim()))
			    .forEach(b->System.setProperty(b[0].substring(2).trim(), b[1].trim()))
			    ;
		}finally {
			try {if (inpf != null) inpf.close();} catch (IOException e) {}
		}
	}

}