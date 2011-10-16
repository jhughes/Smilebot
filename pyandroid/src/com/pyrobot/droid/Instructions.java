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
		string.append(" {");
		for (Entry<String, String> entry : instructions.entrySet()) {
			string.append(String.format("'%s':%s,", entry.getKey(), entry.getValue().toString()));
		}
		int lastComma = string.lastIndexOf(",");
		if(lastComma > 0){
			string.deleteCharAt(lastComma);
		}
		string.append("}\n");
		return string.toString();
	}

	public void setValue(String key, String value) {
		instructions.put(key, value);
	}

	public void setCommand(String command) {
		instructions.put("command", command);
	}
	
	public void setVelocity(int velocity) {
		instructions.put("velocity", Integer.toString(velocity));
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
}
