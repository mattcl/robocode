package rampancy.util;

import java.util.HashMap;
import java.util.Iterator;

public class REnemyManager {
	
	protected HashMap<String, REnemyRobot> enemies;

	public REnemyManager() {
		this.enemies = new HashMap<String, REnemyRobot>();
	}
	
	public void add(String name) {
		this.enemies.put(name, new REnemyRobot(name));
	}
	
	public REnemyRobot get(String name) {
		return enemies.get(name);
	}
	
	public boolean contains(String name) {
		return enemies.containsKey(name);
	}
	
	public Iterator<REnemyRobot> iterator() {
		return enemies.values().iterator();
	}
}
