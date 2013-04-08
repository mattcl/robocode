package rampancy.micro;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Swingline extends AdvancedRobot {

    private static double lastHeading;
    private static double lastEnergy;
    private static double moveDist;

    public void run() {
        setColors(new Color(0xC92E2E), new Color(0xF54747), new Color(0x7A7676), Color.white, Color.white);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while(true) {
            if (getRadarTurnRemaining() == 0) {
                setTurnRadarLeftRadians(Double.POSITIVE_INFINITY);
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

        double absB = Utils.normalAbsoluteAngle(e.getBearingRadians() + getHeadingRadians());

        Point2D.Double myLocation = new Point2D.Double(getX(), getY());
        Point2D.Double target = project(myLocation, absB, e.getDistance());

        double deltaH = e.getHeadingRadians() - lastHeading;
        double heading = e.getHeadingRadians();
        lastHeading = heading;
        double bestPower = 0.1;
        double gunAngle = 100;

        for (int i = 0; i < 200; i++) {
            heading += deltaH;
            target = project(target, heading, e.getVelocity());
            double power = computeBulletPower(target.distance(myLocation) / (i + 1));
            if (power > 2.0) {
                break;
            }
            if (power > bestPower) {
                bestPower = power;
                if (target.x < 18 || target.y < 18 || target.x + 18 > getBattleFieldWidth() || target.y + 18 > getBattleFieldHeight()) {
                    break;
                }
                gunAngle = Utils.normalRelativeAngle(computeAbsoluteBearing(myLocation, target) - getGunHeadingRadians());
            }

        }

        if (gunAngle != 100) {
            setTurnGunRightRadians(gunAngle);
            setFire(bestPower);
        }

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

    public Point2D.Double project(Point2D.Double point, double angle, double dist) {
        return new Point2D.Double(point.x + Math.sin(angle) * dist, point.y + Math.cos(angle) * dist);
    }

    public double computeBulletPower(double velocity) {
        return Math.max(0.1, (20.0 - velocity) / 3.0);
    }

    public double computeAbsoluteBearing(Point2D.Double source, Point2D.Double target) {
        return Utils.normalAbsoluteAngle(Math.atan2(target.x - source.x, target.y - source.y));
    }
}
