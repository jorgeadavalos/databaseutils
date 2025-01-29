package com.assoc.jad.database.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.sql.DataSource;

import com.assoc.jad.database.tools.DatabaseUtilsStatics;

/**
 * this class implements the database access. The name of the 'schema' variable is fixed because
 * there will be only one DB resource per VM but there will be more than one VM housing the same DB resource name.
 * the constructor will be invoked using two parameters: the class of the instance and the instance itself.
 * reflection will be used to build and invoke the methods from the meta data of the sql request.
 * In case of a failure due to connection timeout it will try to reconnect 4-times before throwing and exception.
 * It implements image insertion (Blob in Mysql jargon) by accessing from a file and if the file size is larger than 64k
 * it will enter multiple rows per one image.
 * Errors will be report in the TomCat server log.
 */

public class DataBaseAccess<T> {
	private static final Logger LOGGER = Logger.getLogger( DataBaseAccess.class.getName() );

	private String schema;
	private ResultSet rst=null;
	private Connection conn = null;
	private Statement stmt = null;
	private String sqlColName = "";
	private String sqlColValue = "";
	private Object retObj = null;
	private Object retType = null;
	//private T updater = null;

	private Class<?> caller = null;
	private T instance = null;
	private String currvalFormat = null;
	private HashMap<String,String> dataBaseNames = new HashMap<String,String>(10);
	private String errorMsg;
	
    //private DataSource dataSource;
	
	public DataBaseAccess( T instance,Connection conn,String schema) {
		this.schema = schema;
		this.caller = instance.getClass();
		this.instance = instance;
		this.conn = conn;
		dataBaseNames.put("POSTGRESQL", "select currval('%s_%s_seq')");
		dataBaseNames.put("MYSQL", "SELECT currval(pg_get_serial_sequence('%s','%s')");
	}
	public void wrapup() {
//		 try {
//				if(rst != null)  rst.close();
//				if(stmt != null) stmt.close();
//				if(conn != null) conn.close();
//				rst = null;
//				stmt = null;
//				conn = null;
//			}catch (SQLException e) {
//				e.printStackTrace();
//			}
//		
	}
	private synchronized boolean reConnect(String schema, String sql) {
		
		try {
			wait(3000);
//			Context initContext = new InitialContext();
//			Context envContext  = (Context)initContext.lookup("java:/comp/env");
//			DataSource ds = (DataSource)envContext.lookup("jdbc/"+schema);
//			conn = ds.getConnection();
	        stmt = conn.createStatement();
	        stmt.setMaxRows(0); //no limit
	      	if (stmt.execute(sql,Statement.RETURN_GENERATED_KEYS)) rst = stmt.getResultSet();
	      	if (rst != null ) return true;
	      	return false;
		} catch (SQLException | InterruptedException e) {
			errorMsg = "DataBaseAccess::reConnect "+" "+sql+"\n"+e;
			LOGGER.warning(errorMsg)	;
			return false;
		}
	}
	private void setDataBaseAttributes(Connection conn) {
		DatabaseMetaData databaseMetaData;
		try {
			databaseMetaData = conn.getMetaData();
			String keydatabase = databaseMetaData.getDatabaseProductName();
			currvalFormat = dataBaseNames.get(keydatabase.toUpperCase());
		} catch (SQLException e) {
			e.printStackTrace();
			LOGGER.warning(e.getMessage())	;
		}
	}
	private boolean SQLExec(String schema, String sql) {
		errorMsg = "";
		try {
//			if (conn == null) {
//				Context initContext = new InitialContext();
//				Context envContext  = (Context)initContext.lookup("java:/comp/env");
//				DataSource ds = (DataSource)envContext.lookup("jdbc/"+schema);
//				conn = ds.getConnection();
				
//				conn = dataSource.getConnection();
				setDataBaseAttributes(conn);
				stmt = conn.createStatement();
				stmt.setMaxRows(0); //no limit
//			}
//	      	if (stmt.execute(sql,Statement.RETURN_GENERATED_KEYS)) rst = stmt.getResultSet();
	      	if (stmt.execute(sql)) rst = stmt.getResultSet();
	      	return true;
		} catch (SQLException e) {	//TODO no connection available.
            String sqlState = e.getSQLState();

            // staled connection or deadlock. retry it.
            if ("08S01".equals(sqlState) || "41000".equals(sqlState)) {
            	for (int i=0;i<4;i++) {
                	if (reConnect( schema,  sql) ) return true;
            	}
            }
            errorMsg = "DataBaseAccess::SQLExec sqlState="+sqlState+" "+sql+"\n"+e;
			LOGGER.warning(errorMsg)	;
			return false;
		} catch (Exception e) {
			errorMsg = "DataBaseAccess::SQLExec "+" "+sql+"\n"+e;
			LOGGER.warning(errorMsg)	;
			e.printStackTrace();
			return false;
		}
	}
	private String bldAModelName(String prefix,String colName) {
		StringBuilder name = new StringBuilder(colName);
		byte cap = (byte) name.charAt(0);
		cap = (byte)(0xdf & cap);
		name.setCharAt(0, (char) cap);
		return prefix+name;
	}
	private List<T> bldSetterName() throws Exception {
        if (rst == null || caller == null) return null;

    	List<T> callerInstances = new ArrayList<>();
		ResultSetMetaData meta = rst.getMetaData();
     	int col = meta.getColumnCount();
     	while (rst.next()) { 
    		@SuppressWarnings("unchecked")
			T instance = (T) caller.getDeclaredConstructor().newInstance();
        	for (int i=1;i <=col;i++) {
        		Object value = rst.getObject(i);
        		if (value != null && value.getClass().getName().equals("java.lang.String"))
        			value = DatabaseUtilsStatics.undoSpecialChars(value.toString());

        		if (value != null)
        			executeMethods(bldAModelName("set",meta.getColumnName(i)),value,instance);
        	}
        	callerInstances.add(instance);
     	}
     	return callerInstances;
	}
	private void bldInsertNameValue() throws Exception {
        if (rst == null || caller == null) return;

		//Object instance = this.instance;
		ResultSetMetaData meta = rst.getMetaData();
     	int col = meta.getColumnCount();
    	for (int i=1;i <=col;i++) {
			if (!meta.isAutoIncrement(i)) sqlColName += meta.getColumnName(i)+",";
    		executeMethods(bldAModelName("get",meta.getColumnName(i)),null,instance);
    		if (retObj == null ) {
    			if (retType.toString().equals("class [B")) sqlColValue += "?,";
    			else if (retType.toString().indexOf("java.sql.Blob") != -1) sqlColValue += "?,";
    			else sqlColValue += "' ',";
    		}
    		else {
    			String type = retObj.getClass().getName();
    			if (type.equals("[B")) sqlColValue += "?,";
    			else if (type.equals("java.lang.String")||type.equals("java.sql.Timestamp")) {
    				 if (retObj.equals("java.sql.Blob"))
        	    			sqlColValue += "?,";
    				 else sqlColValue += "'"+DatabaseUtilsStatics.specialChars(retObj.toString())+"',";  //html special chars representation
    			} else if (!meta.isAutoIncrement(i)) sqlColValue += retObj.toString()+",";
    		}
    	}
    	int len = sqlColValue.length()-1;
    	if (sqlColValue.endsWith(",")) sqlColValue = sqlColValue.substring(0, len);
    	len = sqlColName.length()-1;
    	if (sqlColName.endsWith(",")) sqlColName = sqlColName.substring(0, len);
	}
	private void collectNameValue(T wrkInstance) throws Exception {

		ResultSetMetaData meta = rst.getMetaData();
     	int col = meta.getColumnCount();
    	for (int i=1;i <=col;i++) {
			if (!meta.isAutoIncrement(i)) sqlColName += meta.getColumnName(i)+",";
    		executeMethods(bldAModelName("get",meta.getColumnName(i)),null,wrkInstance);
    		if (retObj == null ) {
    			if (retType.toString().equals("class [B")) sqlColValue += "?,";
    			else if (retType.toString().indexOf("java.sql.Blob") != -1) sqlColValue += "?,";
    			else sqlColValue += "' ',";
    		}
    		else {
    			String type = retObj.getClass().getName();
    			if (type.equals("[B")) sqlColValue += "?,";
    			else if (type.equals("java.lang.String")||type.equals("java.sql.Timestamp")) {
    				 if (retObj.equals("java.sql.Blob"))
        	    			sqlColValue += "?,";
    				 else sqlColValue += "'"+DatabaseUtilsStatics.specialChars(retObj.toString())+"',";  //html special chars representation
    			} else if (!meta.isAutoIncrement(i)) 
    				sqlColValue += retObj.toString()+",";
    		}
    	}
    	int len = sqlColValue.length()-1;
    	if (sqlColValue.endsWith(",")) sqlColValue = sqlColValue.substring(0, len);
    	len = sqlColName.length()-1;
    	if (sqlColName.endsWith(",")) sqlColName = sqlColName.substring(0, len);
	}
	private void executeMethods(String methnam,Object obj,Object instance) throws Exception {
		Object[] arguments = new Object[] {obj};
		if (obj == null && methnam.startsWith("get")) arguments = null;

		Method method = findMethod(methnam,obj);
		if (method == null) {
			System.out.println("DataBaseAccess::executeMethods: method not found "+methnam+" class="+caller.getName());
			return;
		}
		try {
			retType = method.getReturnType();
			retObj = method.invoke(instance,arguments);
		} catch (Exception e) {
			errorMsg = "DataBaseAccess::executeMethods:"+methnam+" "+e;
			LOGGER.warning(errorMsg)	;
			e.printStackTrace();
		} 
	}
	/* 
	 * return methods for setters/getters only. Only one parameter or null
	 */
	private Method findMethod(String methnam,Object obj) {

		Method[] methods = caller.getMethods();
		for (int i=0;i<methods.length;i++) {
			if (methods[i].getName().equals(methnam)) {
				if (methods[i].getParameterCount() > 1) continue;
				
				if (obj != null) {
					Parameter[] parameter = methods[i].getParameters();
					if (!parameter[0].getType().equals(obj.getClass())) continue;
				}
				return methods[i];
			}
		}
		return null;
	}

	/**
     * executes sql 
     * @param SQL command (String)
     * @return    list of Objects or null
     */
	public List<T> readSql( String sql) {
		try {
			List<T> callerInstances = null;
			
			if (SQLExec(schema,sql))
				callerInstances = bldSetterName();
			return callerInstances ;

		} catch (Exception e) {
			errorMsg = "DataBaseAccess::readSql " +" "+sql+"\n"+ e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return null;
		} finally {
			wrapup();
		}
	}
	public boolean bldTable( String sql) {
		return (SQLExec(schema,sql));
	}
	public ResultSetMetaData getFieldnames(String sql) {
		
		ResultSetMetaData meta = null;
		if (SQLExec(schema,sql)) {
			try {
				meta = rst.getMetaData();
			} catch (SQLException e) {
				errorMsg = e.toString();
				LOGGER.warning(errorMsg);
				e.printStackTrace();
			}
		}
		
		return meta;
	}
	public List<String> getFieldnames(Class<?> clazz) {
		List<String> fieldNames = new ArrayList<>();

		Field[] fields = clazz.getDeclaredFields();
		for (int i=0;i<fields.length;i++) {
			fieldNames.add(fields[i].getName());
		}	
		return fieldNames;
	}
	@Deprecated
	public List<String> getFieldnamesx(String sql) {
		List<String> list = new ArrayList<>();
		if (SQLExec(schema,sql)) {
			ResultSetMetaData meta;
			try {
				meta = rst.getMetaData();
		     	int col = meta.getColumnCount();
        		StringBuilder sb = new StringBuilder();
	        	for (int i=1;i <=col;i++) {	
	        		list.add(meta.getColumnName(i));
	        		sb.setLength(0);
	        	}
			} catch (SQLException e) {
				errorMsg = e.toString();
				LOGGER.warning(errorMsg);
				e.printStackTrace();
			}
		}
		
		return list;
	}
	private List<StringBuffer> bldRows() throws Exception {
        if (rst == null || caller == null) return null;

    	List<StringBuffer> rows = new ArrayList<>();
		ResultSetMetaData meta = rst.getMetaData();
     	int col = meta.getColumnCount();
     	while (rst.next()) { 
        	StringBuffer row = new StringBuffer();
        	StringBuffer names = new StringBuffer("(");
        	StringBuffer values = new StringBuffer("values (");
        	for (int i=1;i <=col;i++) {
        		names.append( meta.getColumnLabel(i)).append(",");
        		Object value = rst.getObject(i);
        		if (value == null) value = "";
        		if (value != null && value.getClass().getName().equals("java.lang.String")) {
        			value = "'"+DatabaseUtilsStatics.undoSpecialChars(value.toString())+"'";
        		}
        		if (value != null) values.append(value).append(",");
        	}
        	names.setLength(names.length() -1);
        	values.setLength(values.length() -1);
        	row.append(names).append(") ").append(values).append(')');
        	rows.add(row);
     	}
     	return rows;
	}
	public List<StringBuffer> readFullTable( String sql) {
		try {
			List<StringBuffer> rows = null;
			
			if (SQLExec(schema,sql))
				rows = bldRows();
			return rows ;

		} catch (Exception e) {
			errorMsg = "DataBaseAccess::readFullTable " +" "+sql+"\n"+ e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return null;
		} finally {
			wrapup();
		}
	}
	public void fullTableCopy(String toTable,List<StringBuffer> toRows) {
     	for (int i=0;i<toRows.size();i++) { 
			StringBuilder bldSQL = new StringBuilder("insert into "+toTable+" ");
			bldSQL.append(toRows.get(i));
     		SQLExec(schema,bldSQL.toString());
		}
		errorMsg = "Added "+ toRows.size()+" to table="+toTable;
		LOGGER.warning(errorMsg);
	}
	public boolean delete( String sql) {
		errorMsg = "success deleting item using sql="+sql;
		return SQLExec(schema,sql);
	}
	/**
	 * insertSql: 
	 * 	execute input sql string to retrieve meta data for the table to update.
	 * 	builds getter to retrieve data from instance.
     * @param SQL command (String)
     * @param table table name(String)
     * @return boolean   true=OK false=failed
	 */
	public boolean insertSql( String sql,String table) {
		sql = "select * from "+table+" limit 1";
		sqlColName = "insert into "+table+" (";
		sqlColValue = "values (";
		try {
			boolean flag = SQLExec(schema,sql);
			if (flag) {
				bldInsertNameValue();
				sql = sqlColName += ") " + sqlColValue + ")";
				flag = SQLExec(schema,sql);
			}
			if (flag) getCurrValue(table, "id");
			wrapup();
			return flag ;

		} catch (Exception e) {
			errorMsg = "DataBaseAccess::readSql " +" "+sql+"\n"+e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return false;
		}
	}
	public boolean insertSql(T instance,String table) {
		this.instance = instance;
		return insertSql("IGNORE", table);
	}
	private void getCurrValue(String table, String colName) throws Exception {
		String modelName = bldAModelName("set",colName);
		if (findMethod(modelName,null) == null) return;
		
		String sql = String.format(currvalFormat, table,colName);
		Boolean gotId = SQLExec(schema,sql);
		if (!gotId) return;
		ResultSetMetaData meta = rst.getMetaData();
		if (meta == null) return;
     	rst.next();
		executeMethods(modelName,rst.getObject(1),instance);
	}
	private String bldUpdateSqlCmd(String table,T tblInstance) throws Exception {
		String sql = "update "+table+" set ";
		boolean changes = false;
		collectNameValue(tblInstance);
		String sqlColValueOld = sqlColValue;
		sqlColName = "";
		sqlColValue = "";
//		instance = updater;
		collectNameValue(instance);
		String[] oldVals  = sqlColValueOld.split(",");
		String[] newVals  = sqlColValue.split(",");
		String[] colNames = sqlColName.split(",");
		if (oldVals.length != colNames.length) throw new Exception("number of vals diff from names "+colNames+""+oldVals);
		
		for (int i=0;i<oldVals.length;i++) {
			if (newVals[i].equals(oldVals[i])) continue;
			sql += colNames[i]+"="+newVals[i]+",";
			changes = true;
		}
		if (!changes) return "";
		int ndx = sql.length()-1;
		sql = sql.substring(0,ndx);
		return sql;
	}
	/**
	 * updateSql: 
	 * 	execute input sql string to retrieve meta data for the table to update.
	 * 	builds setter to update only columns that have changed .
     * @param SQL command (String) where is set to locate a single row.(all key fields should be set.)
     * @param table table name(String)
     * @return boolean   true=OK false=failed
	 */
	public boolean updateSql( String sql,String table) {
		if (this.instance == null) return false;
//		updater = instance;
		sqlColName = "";
		sqlColValue = "";
		try {
			List<T> callerInstances = null;
			if (SQLExec(schema,sql))
				callerInstances = bldSetterName();
			else return false;
			if (callerInstances.size() == 0) return false;
			
//			instance =callerInstances.get(0);
			int ndx = sql.toLowerCase().indexOf("where");
			if (ndx == -1) throw new Exception("no where clause in sql statement");
			String whereClause = sql.substring(ndx);
			sql = bldUpdateSqlCmd(table,callerInstances.get(0));
			if (sql.length() == 0) return true; 					//no changes are necesssary all fields are the same;
			sql += " "+whereClause;
			boolean flag = SQLExec(schema,sql);
			return flag ;

		} catch (Exception e) {
			System.out.println("DataBaseAccess::readSql " +" "+sql+"\n"+e.toString());
			e.printStackTrace();
			return false;
		} finally {
			wrapup();
		}
	}
	public boolean deleteSql( String sql) {
		try {
			SQLExec(schema,sql);
			wrapup();
			return true;
		} catch (Exception e) {
			errorMsg = "DataBaseAccess::deleteSql " + sql+" "+e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * insertSqlBlob: 
	 * 	read image from input file.
	 * 	builds getter to help create a new row with max blob-size. 
	 * 	If file size is larger than max blob size then create multiple rows.
     * @param File command (File)
     * @param sql sql command(String)
     * @param table table name(String)
     * @return boolean   true=OK false=failed
	 */
	public boolean insertSqlBlob(InputStream ins, String sql,String table) {
		sqlColName = "insert into "+table+" (";
		sqlColValue = "values (";
		byte[] bytes = new byte[65535];
		int len = 0;
		try {
			boolean flag = SQLExec(schema,sql);
			if (flag) {
				bldInsertNameValue();
				sql = sqlColName += ") " + sqlColValue + ")";
			    java.sql.PreparedStatement prep = conn.prepareStatement(sql);
				while ((len = ins.read(bytes)) != -1) {
				    ByteArrayInputStream bais = new ByteArrayInputStream(bytes,0,len);
				    prep.setBinaryStream(1, bais, len);
				    prep.execute();
				}
				getCurrValue(table, "id");
				ins.close();
				if (prep != null) {prep.close();}
			}
		} catch (Exception e) {
			errorMsg = "DataBaseAccess::insertSqlBlob " + sql+" "+e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return false;
		} finally {
			wrapup();
		}
		return true;
	}
	public boolean insertSqlBlob(File tmpFile, String sql,String table) {
		try {
			InputStream is = new FileInputStream(tmpFile);
			return insertSqlBlob(is,  sql, table);
		} catch (Exception e) {
			errorMsg = " " + sql+" "+e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return false;
		} finally {
			wrapup();
		}
	}
	public void selectSqlBlob(String sql,File image) {
	    java.sql.Blob blob = null;
		int len = 0;
		try {
			if (!SQLExec(schema,sql)) {
				wrapup();
				return;
			}
			FileOutputStream outs = new FileOutputStream(image);
			 while (rst.next()) {
				blob = rst.getBlob(1);
				len = (int)blob.length();
				outs.write(blob.getBytes(1, len));
			}
			outs.close();
			wrapup();
		} catch (Exception e) {
			errorMsg = " " + sql+" "+e.toString();
			LOGGER.warning(errorMsg);
		}
	}
	public boolean insertByteArray(byte[] bytes, String sql,String table) {
		sqlColName = "insert into "+table+" (";
		sqlColValue = "values (";
		try {
			boolean flag = SQLExec(schema,sql);
			if (!flag) return flag;
			bldInsertNameValue();
			sql = sqlColName += ") " + sqlColValue + ")";
		    java.sql.PreparedStatement prep = conn.prepareStatement(sql);
		    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		    prep.setBinaryStream(1, bais);
		    prep.execute();
			getCurrValue(table, "id");
			if (prep != null) {prep.close();}
		} catch (Exception e) {
			errorMsg = " " + sql+" "+e.toString();
			LOGGER.warning(errorMsg);
			e.printStackTrace();
			return false;
		} finally {
			wrapup();
		}
		return true;
	}
	public List<String> getAllTablesInDB(String databaseName) {
		List<String> tables = new ArrayList<>();
		DatabaseMetaData md;
		try {
			if (conn == null) return tables;
			md = conn.getMetaData();
	    	ResultSet rs = md.getTables(null, null, "%", null);
	    	while (rs.next()) {
	    		if (rs.getString(1) != null)
	    			if (!databaseName.equalsIgnoreCase(rs.getString(1)))  continue;
	    		tables.add(rs.getString(3));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tables;
	}
	public List<String> getDefinedDatabases(String sql) {
		List<String> tables = new ArrayList<>();
		SQLExec(schema,sql);
		try {
	    	while (rst.next()) {
	    		tables.add(rst.getString(1));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tables;
	}
	public void createDatabase(String sql,String url, String user, String psw) {
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, user, psw);
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
/*
 * getters and setters 
 */
	public void setInstance(T instance) {
		this.instance = instance;
	}
	public T getInstance() {
		return this.instance;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public Connection getConn() {
		return conn;
	}
}