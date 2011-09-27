package com.pyrobot.droid;

import java.util.Hashtable;
import java.util.Map.Entry;

public class Instructions {
	Hashtable<String, String> instructions;
	
	public Instructions() {
		instructions = new Hashtable<String, String>();
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('{');
		for (Entry<String, String> entry : instructions.entrySet()) {
			string.append(String.format("'%s':%s,", entry.getKey(), entry.getValue().toString()));
		}
		int lastComma = string.lastIndexOf(",");
		if(lastComma > 0){
			string.deleteCharAt(lastComma);
		}
		string.append('}');
		return string.toString();
	}

	public void setValue(String key, String value) {
		instructions.put(key, value);
	}

	public void setVelocity(int velocity) {
		instructions.put("velocity", Integer.toString(velocity));
	}

	public void setRadius(int radius) {
		instructions.put("radius", Integer.toString(radius));
	}

	public void setDistance(int distance) {
		instructions.put("distance", Integer.toString(distance));
	}

	public void setAngle(int angle) {
		instructions.put("angle", Integer.toString(angle));
	}

	public void setSonar(int sonar) {
		instructions.put("sonar", Integer.toString(sonar));
	}

	public void setIgnoreBump(boolean ignoreBump) {
		instructions.put("ignore_bump", ignoreBump ? "True" : "False");
	}

	public void setIgnoreWheelDrop(boolean ignoreWheelDrop) {
		instructions.put("ignore_wheel_drop", ignoreWheelDrop ? "True" : "False");
	}

	public void setIgnoreCliff(boolean ignoreCliff) {
		instructions.put("ignore_cliff", ignoreCliff ? "True" : "False");
	}
}
