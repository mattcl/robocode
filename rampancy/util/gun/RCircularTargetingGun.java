package rampancy.util.gun;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import rampancy.RampantRobot;
import rampancy.util.RDrawable;
import rampancy.util.REnemyRobot;
import rampancy.util.RPoint;
import rampancy.util.RRobotState;
import rampancy.util.RUtil;

public class RCircularTargetingGun extends RGun {
	
	public static final String NAME = "Circular targeting gun";
	public static final int MAX_PROJECTED_TURNS = 100;

	public RCircularTargetingGun() {
		super(NAME);
	}

	@Override
	public RFiringSolution getFiringSolution(RampantRobot reference, REnemyRobot enemy) {
		RRobotState currentState = enemy.getCurrentState();
		double heading = currentState.heading;
		double velocity = currentState.velocity;
		double deltaH = currentState.deltaH;
		RPoint location = currentState.location.getCopy();
		RPoint referenceLocation = reference.getLocation();
		
		// TODO: compute this somehow
		double maxBulletPower = 2.5;
		double minBulletPower = 0.1;
		double bestPower = 0;
		double bestAngle = 0;
		ArrayList<Option> options = new ArrayList<Option>();
		for (int i = 0; i < MAX_PROJECTED_TURNS; i++) {
			heading += deltaH;
			location = RUtil.project(location, heading, velocity);
			double requiredBulletPower = RUtil.computeBulletPower(location.distance(referenceLocation) / (i + 1.0));
			if (requiredBulletPower > maxBulletPower || !RampantRobot.getGlobalBattlefield().contains(location)) {
				break;
			}
			if (requiredBulletPower > minBulletPower && requiredBulletPower > bestPower) {
				bestPower = requiredBulletPower;
				bestAngle = referenceLocation.computeAbsoluteBearingTo(location);
				options.add(new Option(location, requiredBulletPower, bestAngle));
			}
		}
		if (!options.isEmpty()) {
			return new Solution(enemy, bestPower, bestAngle, options);
		}
		return null;
	}
	
	class Solution extends RFiringSolution {
		ArrayList<Option> options;

		public Solution(REnemyRobot target, double power, double firingAngle, ArrayList<Option> options) {
			super(target, power, firingAngle);
			this.options = options;
		}

		@Override
		public void draw(Graphics2D g) {
			Color lastColor = g.getColor();
			g.setColor(Color.yellow);
			for (Option option : options) {
				option.draw(g);
			}
			RUtil.drawOval(options.get(options.size() - 1).target, 5, g);
			g.setColor(lastColor);
		}
	}
	
	class Option implements RDrawable {
		double power;
		double angle;
		RPoint target;
		
		public Option(RPoint target, double power, double angle) {
			this.power = power;
			this.angle = angle;
			this.target = target.getCopy();
		}
		
		@Override
		public void draw(Graphics2D g) {
			RUtil.drawOval(target, 2, g);
		}
	}
}
