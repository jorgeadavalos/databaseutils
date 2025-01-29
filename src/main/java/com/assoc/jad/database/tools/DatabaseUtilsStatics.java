package com.assoc.jad.database.tools;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

public class DatabaseUtilsStatics {
	public static Socket SOCKETLBDISCOVERY = null;
	public static ExecutorService SERVICETHREADS = Executors.newFixedThreadPool(10);
	public static HashMap<String,String> SERVICES = new HashMap<>();
	public static HashMap<String,DataSource> dynDataSources = new HashMap<>();
	public static HashMap<String,HashMap<String,String>> DBMGRCMDS = sqlDatabases();
	
	
    public static String specialChars(String parm) {
    	String outParm = parm.replaceAll("'", "&#39;").replaceAll(",", "&#44;"); //.replaceAll("@", "&#64;");
    	return outParm;
    }
    public static synchronized String undoSpecialChars(String parm) {
    	if (parm == null) return "";
    	String outParm = parm.replaceAll("&#39;","'").replaceAll("&#44;", ","); //.replaceAll("&#64;", "@");
    	return outParm;
    }
    private static HashMap<String,HashMap<String,String>> sqlDatabases() {
    	HashMap<String,HashMap<String,String>> wrkSQLs = new HashMap<>();
    	HashMap<String,String> managerCmds = new HashMap<>();
    	managerCmds.put("show", " show databases");
    	managerCmds.put("drop", " drop database %s");
    	managerCmds.put("createdb", " create database %s");
    	managerCmds.put("driver","com.mysql.jdbc.Driver");
    	managerCmds.put("defaultdb", "");
    	managerCmds.put("setSize", "true");
    	wrkSQLs.put("mysql",managerCmds);
    	
    	managerCmds = new HashMap<>();
    	managerCmds.put("show", " SELECT datname FROM pg_database");
    	managerCmds.put("drop", " drop database \"%s\"");
    	managerCmds.put("createdb", " create database \"%s\"");
    	managerCmds.put("driver", "org.postgresql.Driver");
    	managerCmds.put("defaultdb", "postgres");
    	managerCmds.put("setSize", "false");
    	wrkSQLs.put("postgresql",managerCmds);
    	
    	managerCmds = new HashMap<>();
    	managerCmds.put("show", " show databases");
    	managerCmds.put("drop", " drop database %s");
    	managerCmds.put("createdb", " create database %s");
    	managerCmds.put("driver","NEED TO UPDATE");
    	managerCmds.put("defaultdb", "");
    	managerCmds.put("setSize", "false");
    	wrkSQLs.put("default",managerCmds);
    	
        return wrkSQLs;
    }
}
