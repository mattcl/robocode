package rampancy.util.wave;

import java.util.ArrayList;

public class RWaveManager {
	ArrayList<RWave> waves;

	public RWaveManager() {
		this.waves = new ArrayList<RWave>();
	}
	
	public void add(RWave wave) {
		this.waves.add(wave);
	}
	
	public void update(long time) {
		for (RWave wave : waves) {
			wave.update(time);
		}
	}

}
