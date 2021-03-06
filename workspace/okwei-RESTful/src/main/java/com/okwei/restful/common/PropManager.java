package com.okwei.restful.common;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 属性工厂类
 * 
 * @author xiehz
 *
 */
public class PropManager {

	private PropManager() {
	}

	private static PropManager propManager;

	public synchronized static PropManager getInstance() {
		if (propManager == null) {
			propManager = new PropManager();
		}
		return propManager;
	}

	public static CompositeConfiguration config = new CompositeConfiguration();

	static {
		try {
			config.addConfiguration(new PropertiesConfiguration("restful.properties"));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String key) {
		return config.getString(key);
	}

	public static void main(String[] args) {

		String jdbcUsername = PropManager.getInstance().getProperty("jdbc.username");
		String jdbcPassword = PropManager.getInstance().getProperty("jdbc.password");

//		System.out.println("jdbcUsername:" + jdbcUsername);
//		System.out.println("jdbcPassword:" + jdbcPassword);

//		System.out.println(PropManager.getInstance());
//		System.out.println(PropManager.getInstance());

	}

}
