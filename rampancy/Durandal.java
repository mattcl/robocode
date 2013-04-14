package rampancy;

import java.awt.Color;

public class Durandal extends RampantRobot {
	
	public Durandal() {
		super();
	}
	
	public void run() {
		super.run();
	}

    @Override
    public void initialSetup() {
		setColors(Color.black, new Color(0x0D5E10), new Color(0x0D5E10), Color.white, Color.blue);
    }
}
