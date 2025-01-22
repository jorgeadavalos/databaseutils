package com.assoc.jad.database.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assoc.jad.database.dao.UsersDao;
import com.assoc.jad.database.repository.DataBaseUtils;
import com.assoc.jad.filetransfer.tools.CreateTempFiles;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class DatabaseService {
	private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
	private String infomsg;
//	private final String crlf = "\r\n";

	@Autowired
	UsersDao usersDao;
	@Autowired
	DataBaseUtils dataBaseUtils;

	public JSONObject verifyTables(HttpServletRequest req) {
		JSONObject jsonOut = null;
		jsonOut = dataBaseUtils.verifyTables(req);
		return jsonOut;
	}
	public JSONObject createTable(HttpServletRequest req) {
		JSONObject jsonOut = null;
		jsonOut = dataBaseUtils.createTable(req);
		return jsonOut;
	}
	public JSONObject bldConnections(JSONObject reqBodyJson) {
		JSONObject jsonOut = null;
		jsonOut = dataBaseUtils.bldConnections(reqBodyJson);
		return jsonOut;
	}
	public List<String> uploadFile(HttpServletRequest req) {
		LOGGER.warning("file of files uploaded and created java tem-file(s)");
		CreateTempFiles tempFiles = new CreateTempFiles();
		return tempFiles.bldTempFile(req);
	}
	public JSONObject uploadJarFile(HttpServletRequest req) {
		JSONObject jsonOut = new JSONObject();
		List<String> fullFilenames = uploadFile(req);
		String filename = fullFilenames.get(0);
		if (!verifyJarsignature(filename,jsonOut)) ;

		addToClassPath(filename);
		return jsonOut;
	}
	private void addToClassPath(String filename) {
		URL[] urls;
		File file = new File(filename);
		URLClassLoader classLoader = null;
		try {
			URL jarUrl = file.toURI().toURL();
			urls = new URL[]{jarUrl};
		     classLoader = new URLClassLoader(urls);
		     classLoader.loadClass("com.mysql.jdbc.Driver");
		} catch (MalformedURLException | ClassNotFoundException | IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		} finally {
			if (classLoader != null)
				try {classLoader.close();} catch (IOException e) {}
		}

	}
	
	@SuppressWarnings("unchecked")
	private boolean verifyJarsignature(String fullfilename,JSONObject jsonOut) {
		byte[] jar		= { (byte)0x50, (byte)0x4b, (byte)0x03, (byte)0x04};
		byte[] bytes = new byte[1024];
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(fullfilename);
			fis.read(bytes);
		} catch (IOException e) {
			jsonOut.put("infomsg", e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			if (fis != null) try { fis.close(); } catch (IOException e) {}
		}
		for (int i=0;i<jar.length;i++) {
			if (bytes[i] != jar[i]) return false;
		}
		jsonOut.put("infomsg", "jar signature magic numbere matched...");
		return true;
	}
	/*
	 * getters and setters 
	 */
	public String getInfomsg() {
		return infomsg;
	}
}