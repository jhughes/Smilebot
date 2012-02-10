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
	
	public void setInstructions(double x, double y){
		double angle = Math.atan2(y, x);
		if( angle < 0 )
			angle += 2 * Math.PI;
		int radius;
		if(angle < 0.196349541)
		{
			radius = -20;
			instructions.put("command", "'right'");
		}
		else if(angle < 0.589048623){
			radius = -300;
			instructions.put("command", "'forward'");
		}			
		else if(angle < 0.981747704){
			radius = -1000;
			instructions.put("command", "'forward'");
		}
		else if(angle < 1.37444679){
			radius = -5000;
			instructions.put("command", "'forward'");
		}
		else if(angle < 1.76714587){
			radius = 32768;
			instructions.put("command", "'forward'");
		}
		else if(angle < 2.15984495){
			radius = 5000;
			instructions.put("command", "'forward'");
		}
		else if(angle < 2.55254403){
			radius = 1000;
			instructions.put("command", "'forward'");
		}
		else if(angle < 2.94524311){
			radius = 300;
			instructions.put("command", "'forward'");
		}
		else if(angle < 4.71238898){
			radius = 20;
			instructions.put("command", "'left'");
		}
		else{
			radius = -20;
			instructions.put("command", "'right'");
		}
		instructions.put("radius", Integer.toString(radius));
		
		double distance = Math.sqrt(x*x + y*y);
		int velocity = (int) (minVelocity + (distance * (maxVelocity - minVelocity)));
		instructions.put("velocity", Integer.toString(velocity));	
	}
}
