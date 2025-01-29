package com.assoc.jad.database.repository;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.assoc.jad.database.tools.DatabaseUtilsStatics;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DataBaseUtils {
	private static final Logger LOGGER = Logger.getLogger(DataBaseUtils.class.getName());
	private String infomsg;

	@SuppressWarnings("unchecked")
	public JSONObject verifyTables(HttpServletRequest req) {

		Connection fromDBCconnection = null;
		Connection toDBCconnection = null;
		JSONObject jsonOut = new JSONObject();
		try {
			fromDBCconnection = DatabaseUtilsStatics.dynDataSources.get("fromDBConn").getConnection();
			DataSource toDataSource = DatabaseUtilsStatics.dynDataSources.get("toDBConn");
			if (toDataSource != null) toDBCconnection = toDataSource.getConnection();
			else toDBCconnection = fromDBCconnection;
			
			fromDBCconnection.setAutoCommit(false);
			toDBCconnection.setAutoCommit(false);
			
			HashMap<String, String> inputs = collectInputs(req);
			String toTable = inputs.get("toTable");
			String fromTable = inputs.get("fromTable");
			String toDatabase = inputs.get("toDatabase"); // TODO might need to define a new database

			jsonOut.put("status", "");
			jsonOut.put("infomsg", "Missing value for input parameter 'fromTable'");
			if (fromTable == null || fromTable.length() == 0) return jsonOut;
			
			String fromDatabase = inputs.get("fromDatabase"); // TODO might need to define a new database
			if (toDatabase.equalsIgnoreCase(fromDatabase)) {
				jsonOut.put("infomsg", "From and to dabases are the same. Therefore; 'fromTable' must be different to 'toTable'");
				if (fromTable.equals(toTable)) return jsonOut;
			}
			DataBaseAccess<Object> fromDBA = new DataBaseAccess<>(new Object(), fromDBCconnection,fromDBCconnection.getCatalog());
			DataBaseAccess<Object> toDBA = new DataBaseAccess<>(new Object(), toDBCconnection,toDBCconnection.getCatalog());
			if (!isTableInDB(toTable, toDBA)) {
				ResultSetMetaData fromFieldMeta = getResultSetMeta(fromTable, fromDBA);
				String toDBManager = getDBmanagerId(toDBCconnection,jsonOut);
				jsonOut.put("table", bldPrimaryKeyFields(fromFieldMeta,toDBManager));
				jsonOut.put("status", Integer.toString(HttpStatus.NOT_FOUND.value()));
				jsonOut.put("infomsg", "A copy from table='"+fromTable+"' will be stored in database='"+ toDatabase + "' in table=" + toTable );
			} else {
				jsonOut.put("status", Integer.toString(HttpStatus.FOUND.value()));
				jsonOut.put("infomsg", "table=" + toTable + " exist in database=" + toDatabase
						+ ". It needs confirmation from user");
			}
			fromDBCconnection.commit();
			toDBCconnection.commit();
		} catch (SQLException e) {
			rollback(fromDBCconnection,toDBCconnection);
			e.printStackTrace();
		} finally {
			closeConnections(fromDBCconnection,toDBCconnection);
		}
		return jsonOut;
	}
	@SuppressWarnings("unchecked")
	private String getDBmanagerId(Connection toDBCconnection,JSONObject jsonOut) {
		try {
			String url = toDBCconnection.getMetaData().getURL();
			if (url == null || url.length() == 0) return null;
			
			url = url.toLowerCase();
			Iterator<String> iter = DatabaseUtilsStatics.DBMGRCMDS.keySet().iterator();
			while (iter.hasNext() ) {
				String key = iter.next();
				String wrkkey = key.toLowerCase();
				if ( url.indexOf(wrkkey) != -1 ) return key;
			}
		} catch (SQLException e) {
			jsonOut.put("status", Integer.toString(HttpStatus.CONFLICT.value()));
			jsonOut.put("infomsg", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public JSONObject createTable(HttpServletRequest req) {
		JSONObject jsonOut = new JSONObject();
		Connection fromDBCconnection = null;
		Connection toDBCconnection = null;
		HashMap<String, String> inputs = collectInputs(req);
		String fromTable = inputs.get("fromTable");
		String toTable = inputs.get("toTable");
		String toDatabase = inputs.get("toDatabase");

		try {
			toDBCconnection = DatabaseUtilsStatics.dynDataSources.get("toDBConn").getConnection();
			fromDBCconnection = DatabaseUtilsStatics.dynDataSources.get("fromDBConn").getConnection();
			fromDBCconnection.setAutoCommit(false);
			toDBCconnection.setAutoCommit(false);

			DataBaseAccess<Object> toDBA = new DataBaseAccess<>(new Object(), toDBCconnection,toDBCconnection.getCatalog());
			DataBaseAccess<Object> fromDBA = new DataBaseAccess<>(new Object(), fromDBCconnection, fromDBCconnection.getCatalog());
			ResultSetMetaData fromResultSet = getResultSetMeta(fromTable, fromDBA);
			HashMap<String, String> hashToFields = bldHashFieldToType(fromResultSet);
			String sql = bldSQLCreateTable(inputs, hashToFields);
			if (!toDBA.bldTable(sql)) {
				jsonOut.put("status", Integer.toString(HttpStatus.NOT_FOUND.value()));
				jsonOut.put("infomsg", toDBA.getErrorMsg());
				return jsonOut;
			}
			sql = "select * from " + fromTable;

			List<StringBuffer> records = fromDBA.readFullTable(sql);
			toDBA.fullTableCopy(toTable, records);
			fromDBCconnection.commit();
			toDBCconnection.commit();
		} catch (Exception e) {
			LOGGER.warning(e.toString());
			rollback(fromDBCconnection,toDBCconnection);
		} finally {
			closeConnections(fromDBCconnection,toDBCconnection);
		}
		jsonOut.put("status", Integer.toString(HttpStatus.OK.value()));
		jsonOut.put("infomsg", "sucessfully copied and stored table '"+toTable+"' in database='"+ toDatabase+"'" );
		return jsonOut;
	}
	@SuppressWarnings("unchecked")
	public JSONObject bldConnections(JSONObject reqBodyJson) {
		JSONObject jsonOut = new JSONObject();
		jsonOut.put("status", Integer.toString(HttpStatus.CONFLICT.value()));
		jsonOut.put("infomsg", "invalid inputs recieved");
		if (reqBodyJson.size() == 0) return jsonOut;
		
		JSONObject fieldsJson = new JSONObject();
		if (fromConnectionFields(reqBodyJson,fieldsJson)) bldConnection(fieldsJson,"fromDBConn",jsonOut);
		fieldsJson = new JSONObject();
		if (toConnectionFields(reqBodyJson,fieldsJson)) bldConnection(fieldsJson,"toDBConn",jsonOut);
		
		String tourl = (String)reqBodyJson.get("tourl");
		if (tourl == null || tourl.length() == 0) {
			jsonOut.put ("tourl",reqBodyJson.get("fromurl"));
			jsonOut.put("toDatabase",jsonOut.get("fromDatabase"));
			jsonOut.put("status", Integer.toString(HttpStatus.OK.value()));
			DatabaseUtilsStatics.dynDataSources.put("toDBConn",DatabaseUtilsStatics.dynDataSources.get("fromDBConn"));
		}
		return jsonOut;
	}
	@SuppressWarnings("unchecked")
	private void bldConnection(JSONObject wrkJson,String connKey,JSONObject jsonOut) {
		
		String url = (String) wrkJson.get("url");
		String dBManagementName = url.split(":")[1];
		HashMap<String,String> dBManagement = DatabaseUtilsStatics.DBMGRCMDS.get(dBManagementName);	//mysql, postgresql....

		jsonOut.put("status", Integer.toString(HttpStatus.CONFLICT.value()));
		jsonOut.put("infomsg", "The URL is not correctl url="+url);
		int ndx = url.lastIndexOf("/");
		if (ndx == -1) return;
		
		String dynDBName = url.substring(++ndx);
		setJsonOutDatabaseName(dynDBName,connKey,jsonOut);

		Connection dynConn = null;
    	DataSource ds = bldDataSource(dBManagement,wrkJson,dynDBName,jsonOut);
    	if (ds == null) return;
    	
    	DatabaseUtilsStatics.dynDataSources.put(connKey,ds);
    	if (connKey.startsWith("fromDBConn")) return;
    	
    	DataSource dsRoot = bldDataSource(dBManagement,wrkJson,dBManagement.get("defaultdb"),jsonOut);
        try {
        	dynConn = dsRoot.getConnection();
    		dynConn.setAutoCommit(true);
    		if (!DatabaseExist(dBManagement, dynConn, dynDBName,jsonOut)) {
    			createDatabase(wrkJson,jsonOut,dBManagement,dynDBName);
    		}
 
		} catch (SQLException e) {
			jsonOut.put("status", Integer.toString(HttpStatus.CONFLICT.value()));
			jsonOut.put("infomsg", "Error in bldConnection="+e.getMessage());
			LOGGER.warning(e.toString());
			e.printStackTrace();
			rollback(dynConn);
		} finally {
			closeConnections(dynConn);
		}
	}
	@SuppressWarnings("unchecked")
	private void setJsonOutDatabaseName(String dynDBName, String connKey, JSONObject jsonOut) {
		if (connKey.equals("fromDBConn")) 
			jsonOut.put("fromDatabase", dynDBName);
		else
			jsonOut.put("toDatabase", dynDBName);
	}

	@SuppressWarnings("unchecked")
	private boolean fromConnectionFields(JSONObject reqBodyJson,JSONObject fieldsJson) {
		String url = ((String) reqBodyJson.get("fromurl"));
		if (url == null || url.trim().length() == 0) return false;
		url = url.trim();
		
		fieldsJson.put("url", url);
		fieldsJson.put("userid", (String) reqBodyJson.get("fromuserid"));
		fieldsJson.put("psw",(String) reqBodyJson.get("frompsw"));
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean toConnectionFields(JSONObject reqBodyJson,JSONObject fieldsJson) {
		String url = ((String) reqBodyJson.get("tourl"));
		if (url == null || url.trim().length() == 0) return false;
		url = url.trim();
		
		fieldsJson.put("url", url);
		fieldsJson.put("userid", (String) reqBodyJson.get("touserid"));
		fieldsJson.put("psw",(String) reqBodyJson.get("topsw"));
		return true;
	}

	@SuppressWarnings("unchecked")
	private DataSource bldDataSource(HashMap<String,String> dBManagement,JSONObject reqJson, String dynDBName,JSONObject jsonOut) {
		String url = (String) reqJson.get("url");
		String userid = (String) reqJson.get("userid");
		String psw = (String) reqJson.get("psw");

		String classname = dBManagement.get("driver");
		try {
			Class.forName(classname);
		} catch (Exception e) {
			jsonOut.put("unsolvedclassname", classname);
			jsonOut.put("status", Integer.toString(HttpStatus.SERVICE_UNAVAILABLE.value()));
			jsonOut.put("infomsg", "class name="+classname+" not found...Need to upload.");
			return null;
		}

		HikariConfig config = new HikariConfig();
    	config.setJdbcUrl(url);
    	config.setUsername(userid);
    	config.setPassword(psw);
    	config.setDriverClassName(classname);

    	try {
    		return new HikariDataSource(config);
    	} catch (Exception e) {
    		int ndx = url.lastIndexOf('/');
    		String wrkurl = url.substring(0, ++ndx);		//strip database name if it does not exist in DB
        	config.setJdbcUrl(wrkurl);
        	return new HikariDataSource(config);
    	}
	}
	@SuppressWarnings("unchecked")
	private boolean DatabaseExist(HashMap<String,String> dBManagement,Connection dynConn, String dynDBName,JSONObject jsonOut) {
		
		String sql = dBManagement.get("show");
		DataBaseAccess<Object> dynDBA = new DataBaseAccess<>(new Object(), dynConn,dBManagement.get("defaultdb"));
		List<String> databases = dynDBA.getDefinedDatabases(sql);
		jsonOut.put("status", Integer.toString(HttpStatus.FOUND.value()));
		jsonOut.put("infomsg", "database "+dynDBName+" exist..");
		
		for (String name : databases) {
			if (name.equals(dynDBName)) return true;
		}
		jsonOut.put("status", Integer.toString(HttpStatus.NOT_FOUND.value()));
		return false;
	}
	@SuppressWarnings("unchecked")
	private void createDatabase(JSONObject reqJson, JSONObject jsonOut, HashMap<String, String> dBManagement, String dynDBName) {
		String url = (String) reqJson.get("url");
		String userid = (String) reqJson.get("userid");
		String psw = (String) reqJson.get("psw");
		String sql = String.format(dBManagement.get("createdb"),dynDBName);
		Connection conn = null;
		String wrkURL = url.replaceAll(dynDBName, dBManagement.get("defaultdb"));
		try {
			conn = DriverManager.getConnection(wrkURL, userid, psw);
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			jsonOut.put("status", Integer.toString(HttpStatus.CONFLICT.value()));
			jsonOut.put("infomsg", "could not create database "+dynDBName+" "+e.getMessage());
			e.printStackTrace();
			return;
		} finally {
			if (conn != null) try { conn.close();} catch(SQLException e) {}
		}
		jsonOut.put("status", Integer.toString(HttpStatus.OK.value()));
		jsonOut.put("infomsg", "success... created database "+dynDBName+" ");
	}
	private void rollback(Connection...connections) {
		try {
			for (Connection connection : connections) {
				if (connection != null) connection.rollback();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	private void closeConnections(Connection...connections) {
		try {
			for (Connection connection : connections) {
				if (connection != null) connection.close();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	private HashMap<String, String> collectInputs(HttpServletRequest req) {
		HashMap<String, String> inputs = new HashMap<>();
		Iterator<String> iter = req.getParameterNames().asIterator();
		while (iter.hasNext()) {
			String name = iter.next();
			inputs.put(name, req.getParameter(name));
		}
		String toTable = inputs.get("toTable");
		String fromTable = inputs.get("fromTable");
		if (toTable == null || toTable.length() == 0) toTable = fromTable;
		inputs.put("toTable",toTable);
		return inputs;
	}

	private String bldSQLCreateTable(HashMap<String, String> inputs, HashMap<String, String> hashToFields) {
		String toTable = inputs.get("toTable");
		String primaryKey = inputs.get("keyfields");
		int len = primaryKey.length();
		if (primaryKey.endsWith(","))
			primaryKey = primaryKey.substring(0, len - 1);
		Iterator<String> keys = hashToFields.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (keys.hasNext()) {
			String key = keys.next();
			sb.append(key).append(" ").append(hashToFields.get(key)).append(',').append(System.lineSeparator());
		}
		sb.append("CONSTRAINT " + toTable + "_pkey PRIMARY KEY (" + primaryKey + "));");
		String sql = "CREATE TABLE " + toTable + " (" + sb.toString();
		return sql;
	}

	private <T> Boolean isTableInDB(String toTable, DataBaseAccess<T> database) {
		List<String> tables = new ArrayList<>();
		String databaseName = null;
		Connection connection = null;
		try {
			connection = database.getConn();
			databaseName = connection.getCatalog();
			tables = database.getAllTablesInDB(databaseName);
		} catch (SQLException e) {
			LOGGER.warning(e.getMessage() + "database=" + databaseName + "");
			e.printStackTrace();
		}
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).equalsIgnoreCase(toTable))
				return true;
		}
		return false;
	}

	private HashMap<String, String> bldHashFieldToType(ResultSetMetaData fromFieldMeta) {
		HashMap<String, String> wrkMap = new HashMap<>();

		try {
			int col = fromFieldMeta.getColumnCount();
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= col; i++) {
				String type = fromFieldMeta.getColumnTypeName(i);
				if (type.equalsIgnoreCase("varchar")) type += "("+fromFieldMeta.getColumnDisplaySize(i)+")";
				wrkMap.put(fromFieldMeta.getColumnName(i), type);
				sb.setLength(0);
			}
		} catch (SQLException e) {
			infomsg = e.toString();
			LOGGER.warning(infomsg);
			e.printStackTrace();
		}
		return wrkMap;
	}

	private String bldPrimaryKeyFields(ResultSetMetaData fromFieldMeta,String toDBManager) {
		HashMap<String,String> manager = DatabaseUtilsStatics.DBMGRCMDS.get(toDBManager);
		String varCntrl = " <input type='hidden' id='setSize' value='"+manager.get("setSize")+"'> ";
		String template = "<input type='checkbox' value='%s ' onclick='collectcheckedValues(this)'>";
		StringBuilder sb = new StringBuilder(varCntrl);
		sb.append("<table><tr><th>field name</th><th>type</th><th>check box</th></tr>");
		try {
			sb.append(System.lineSeparator());
			int col = fromFieldMeta.getColumnCount();
			for (int i = 1; i <= col; i++) {
				String name = fromFieldMeta.getColumnName(i);
				String type = fromFieldMeta.getColumnTypeName(i);
				if (type.equalsIgnoreCase("varchar")) type += "("+fromFieldMeta.getColumnDisplaySize(i)+")";
				sb.append("<tr><td>").append(name).append("</td><td>").
				append(type).append("</td><td>").
				append(String.format(template, name)).append("</td></tr>").append(System.lineSeparator());
			}
			sb.append("</table>");
		} catch (SQLException e) {
			infomsg = e.toString();
			LOGGER.warning(infomsg);
			e.printStackTrace();
		}

		return sb.toString();
	}

	private ResultSetMetaData getResultSetMeta(String tablename, DataBaseAccess<Object> fromDB) {
		String sql = "select * from " + tablename + " limit 1";
		ResultSetMetaData meta = fromDB.getFieldnames(sql);
		LOGGER.warning(fromDB.getErrorMsg());

		return meta;
	}

	/*
	 * getters and setters
	 */
	public String getInfomsg() {
		return infomsg;
	}

	public void setInfomsg(String infomsg) {
		this.infomsg = infomsg;
	}
}
//private Object bldInstance(String classname) {
//Object obj = null;
//try {
//	Class<?> clazz = Class.forName(classname);
//	Constructor<?> cons = clazz.getConstructor();
//	obj = cons.newInstance();
//} catch (Exception e) {
//	e.printStackTrace();
//}
//return obj;
//}
//String jarPath = "/path/to/your/jar/file.jar";
//
//  // Create a URLClassLoader with the JAR file
//  URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file:" + jarPath) });
//
//  // Load a class from the JAR
//  Class<?> myClass = classLoader.loadClass("com.example.MyClass"); 
//
//  // Use the loaded class
////  Object instance = myClass.getDeclaredConstructor().newInstance();
//  // ... do something with the instance
//@SuppressWarnings("unchecked")
//private JSONObject mergeJson(JSONObject fromJsonOut, JSONObject toJsonOut) {
//	
//	JSONObject jsonOut = new JSONObject();
//	HashSet<String> hashSet = new HashSet<>(fromJsonOut.keySet());
//	hashSet.addAll(toJsonOut.keySet());
//	for ( String key : hashSet) {
//		String fromValue = (String) fromJsonOut.get(key);
//		String toValue = (String) toJsonOut.get(key);
//		if (fromValue == null) fromValue = "";
//		if (toValue == null) toValue = "";
//		jsonOut.put(key, fromValue+"   "+toValue);
//	}
//	return jsonOut;
//}
