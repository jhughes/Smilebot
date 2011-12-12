package com.pyrobot.droid;

import java.util.Hashtable;
import java.util.Map.Entry;

public class Instructions {
	Hashtable<String, String> instructions;
	
	public Instructions() {
		instructions = new Hashtable<String, String>();
	}
	
	public Instructions(String command) {
		instructions = new Hashtable<String, String>();
		setCommand(command);
	}
	
	public Instructions(double left, double right) {
		instructions = new Hashtable<String, String>();
		setInstructions(left, right);
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
	
	int maxVelocity = 300;
	int minVelocity = 50;
	int maxRadius = 32768;
	int minRadius = 15;
	int turnThreshold = 5;
	
	public void setInstructions(double left, double right){
		left = 1 - left;
		right = 1 - right;
		double difference = (Math.abs(left-right));
//		int radius; = minRadius + (int) (maxRadius - (difference) * maxRadius);
		int radius = (int) (minRadius + maxRadius * Math.pow(difference - 1, 4));
		if(right > left)
			radius = -radius;
		int velocity = minVelocity + (int)(Math.max(left, right) * (maxVelocity - minVelocity));
		if(radius < turnThreshold && radius > 0) {
			instructions.put("command", "\"left\"");
		}
		else if(radius > -turnThreshold && radius < 0) {
			instructions.put("command", "\"right\"");
		}
		else {
			instructions.put("command", "\"forward\"");
		}
		instructions.put("radius", Integer.toString(radius));
		instructions.put("velocity", Integer.toString(velocity));
	}
}
