package com.assoc.jad.database.tools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class DynURLClassLoader extends URLClassLoader {

	public DynURLClassLoader(String name, URL[] urls, ClassLoader parent) {
		super(name, urls, parent);
		// TODO Auto-generated constructor stub
	}
	public void addURL(URL url) {
		super.addURL(url);
	}
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void testclass(String jarfile) {
        URL jarURL;
		try {
			jarURL = new URL("jar:file:" + jarfile + "!/");
	        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});
	        String className = "com.mysql.jdbc.Driver";
	        Class<?> clazz = classLoader.loadClass(className);
	        clazz.getDeclaredConstructor().newInstance();
	        classLoader.close();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

        // 4. Instantiate the class

	}
}
