package com.i2r.dm.samz;

public class VisualizationParams {
	
	public int x;
	public int y;
	public int rotation;
	public int major;
	public int minor;

	public VisualizationParams(){
		x=0;
		y=0;
		rotation=0;
		major=0;
		minor=0;
	}
	
	public void updateVisParameters(int x, int y, int minor, int major, int rotation){
		this.x=x;
		this.y=y;
		this.rotation=rotation;
		this.major=major;
		this.minor=minor;
	}
	
	public void resetVisParameters(){
		this.x=0;
		this.y=0;
		this.rotation=0;
		this.major=0;
		this.minor=0;
	}

	
}
