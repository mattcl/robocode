package rampancy.micro;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Epiphron extends AdvancedRobot {
    
    private static StringBuffer history;
    private static double previousHeading;
    private static double moveDist;
    private static double lastEnergy;
    
    public void run() {
        setColors(new Color(0x701A91), new Color(0x4D0869), new Color(0x4D0869), Color.white, Color.red);
        this.setAdjustGunForRobotTurn(true);
        this.setAdjustRadarForGunTurn(true);
        this.setAdjustRadarForRobotTurn(true);
        
        if(history == null)
            history = new StringBuffer();
        
        while(true) {
        	if (getRadarTurnRemaining() == 0) {
	            setTurnRadarLeftRadians(Math.PI * 5);
        	}
        	execute();
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        double radarBearingOffset =  Utils.normalRelativeAngle(getRadarHeadingRadians() - (e.getBearingRadians() + getHeadingRadians()));  
        double sign = Math.PI / 6;
        if(radarBearingOffset < 0)
            sign = -Math.PI / 6;
        setTurnRadarLeftRadians(radarBearingOffset + sign);
        
        // record state in history
        double heading = e.getHeadingRadians();
        char dh = toChar(Utils.normalRelativeAngle(heading - previousHeading));
        previousHeading = heading;
        char vel = toChar(e.getVelocity());
        history.append("A" + dh + "B" + vel);
        
        double absB = Utils.normalAbsoluteAngle(e.getBearingRadians() + getHeadingRadians());
        double x = getX();
        double y = getY();
        
        double targetX = projectX(x, absB, e.getDistance());
        double targetY = projectY(y, absB, e.getDistance());
        
        double power = e.getEnergy() == 0 ? 0.1 : e.getDistance() < 30 ? 3.0 : -1;
        double maxPower = getEnergy() < 30 ? 0.5 : 2.0;
        // gun code
        if(history.length() > 320) {
            int ind = history.indexOf(history.substring(history.length() - 32, history.length()));
            if(e.getEnergy() > 0 && power != 3.0 && ind != -1 && ind < (history.length() - 112)) {
                String match = history.substring(ind, history.length() - 32);
                for(int i = 0; i < match.length(); i = i + 4) {
                    heading += toDouble(match.charAt(i+1));
                    double velocity = toDouble(match.charAt(i+3));
                    targetX = projectX(targetX, heading, velocity);
                    targetY = projectY(targetY, heading, velocity);
                    double pow = (20.0 - distance(x, y, targetX, targetY)/(i/4+1)) / 3.0;
                    if(pow > power) {
                        power = pow;
                    }
                    
                    if(pow > maxPower)
                        break;
                }
            }
            double angle = Utils.normalRelativeAngle(Utils.normalAbsoluteAngle(Math.atan2(targetX - x, targetY - y)) - getGunHeadingRadians());
            setTurnGunRightRadians(angle + Math.PI);
            if(power >= 0.1) {
                setFire(power);
            }
        }
        
        // movement
        if(e.getEnergy() < lastEnergy) {
            moveDist = 100 + Math.random() * 100;
            if(Math.random() > 0.5) {
                moveDist = -moveDist;
            }
            setAhead(moveDist);
        }
        lastEnergy = e.getEnergy();
        setTurnRightRadians(Utils.normalRelativeAngle(e.getBearingRadians()+ Math.PI / 2));
    }
    
    public void onHitWall(HitWallEvent e) {
        moveDist = -moveDist;
        setAhead(moveDist);
    }
    
    public char toChar(double value) {
        return (char) (value * 1000 + 32767);
    }
    
    public double toDouble(char ch) {
        return (double) ((int) ch - 32767) / (1000.0);
    }
    
    public double projectX(double sx, double angle, double dist) {
        return sx + Math.sin(angle) * dist;
    }
    
    public double projectY(double sy, double angle, double dist) {
        return sy + Math.cos(angle) * dist;
    }

    public double distance(double x1, double y1, double x2, double y2) {
        x1 -= x2;
        y1 -= y2;
        return Math.sqrt(x1 * x1 + y1 * y1);
    }
}