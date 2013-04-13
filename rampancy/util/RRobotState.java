package rampancy.util;

import rampancy.RampantRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class RRobotState {

    public RPoint location;
    public double absoluteBearing;
    public double velocity;
    public double lateralVelocity;
    public double advancingVelocity;
    public double deltaV;
    public double heading;
    public double deltaH;
    public double distance;
    public double distanceFromWall;
    public int distanceFromWallCategory;
    public double timeSinceVelocityChange;
    public double timeSinceDirectionChange;
    public double timeSinceStop;
    public double energy;
    public double gunHeat;
    public boolean accelerating;
    public boolean braking;
    public int directionTraveling;
    
    public RRobotState() {}
    
    public RRobotState(RampantRobot robot, RBattlefield battlefield, ScannedRobotEvent e) {
        RRobotState lastState = robot.getCurrentState();
        double enemyAbsoluteBearing = e == null ? 0 : Utils.normalAbsoluteAngle(robot.getHeadingRadians() + e.getBearingRadians());
        double enemyDistance = e == null ? 100 : e.getDistance();
        
        this.location                 = new RPoint(robot.getX(), robot.getY());
        RPoint enemyLocation = RUtil.project(location, enemyAbsoluteBearing, enemyDistance);
        
        this.absoluteBearing          = RUtil.computeAbsoluteBearing(enemyLocation, location);
        this.heading                  = robot.getHeadingRadians();
        this.velocity                 = robot.getVelocity();
        this.lateralVelocity          = velocity * Math.sin(heading - absoluteBearing);
        this.advancingVelocity        = 0;
        this.deltaV                   = lastState == null ? 0 : velocity - lastState.velocity;
        this.accelerating             = deltaV > 0;
        this.braking                 = !accelerating;
        this.deltaH                   = lastState == null ? 0 : heading - lastState.heading;
        this.distance                 = 0;
        this.distanceFromWall         = RUtil.getDistanceFromWall(battlefield, location);
        this.distanceFromWallCategory = battlefield.distanceFromWallCategory(location);
        this.timeSinceVelocityChange  = deltaV != 0 || lastState == null ? 0 : lastState.timeSinceVelocityChange + 1;
        this.directionTraveling       = lateralVelocity >= 0 ? 1 : -1;
        this.timeSinceDirectionChange = lastState == null || lastState.directionTraveling != directionTraveling ? 0 : 
                                        lastState.timeSinceDirectionChange + 1;
        this.timeSinceStop            = velocity == 0 || lastState == null ? 0 : lastState.timeSinceStop + 1;
        this.energy                   = robot.getEnergy();
    }
    
    /**
     * Constructor for enemy states
     * @param enemy
     * @param e
     */
    public RRobotState(RampantRobot reference, REnemyRobot robot, RBattlefield battlefield, ScannedRobotEvent e) {
        if(reference == null) {
            return;
        } else {
            RRobotState lastState = robot.getCurrentState();
            
            this.heading                  = e.getHeadingRadians();
            this.absoluteBearing          = Utils.normalAbsoluteAngle(reference.getHeadingRadians() + e.getBearingRadians());
            this.location                 = RUtil.project(reference.getLocation(), absoluteBearing, e.getDistance());
            this.velocity                 = e.getVelocity();
            this.lateralVelocity          = velocity * Math.sin(heading - absoluteBearing);
            this.advancingVelocity        = velocity * -1 * Math.cos(heading - absoluteBearing);
            this.deltaV                   = lastState == null ? 0 : velocity - lastState.velocity;
            this.accelerating             = deltaV > 0;
            this.braking                 = !accelerating;
            this.deltaH                   = lastState == null ? 0 : heading - lastState.heading;
            this.distance                 = e.getDistance();
            this.distanceFromWall         = RUtil.getDistanceFromWall(battlefield, location);
            this.distanceFromWallCategory = battlefield.distanceFromWallCategory(location);
            this.timeSinceVelocityChange  = deltaV != 0 || lastState == null ? 0 : lastState.timeSinceVelocityChange + 1;
            this.directionTraveling       = lateralVelocity >= 0 ? 1 : -1;
            this.timeSinceDirectionChange = lastState == null || lastState.directionTraveling != directionTraveling ? 0 : 
                                            lastState.timeSinceDirectionChange + 1;
            this.timeSinceStop            = velocity == 0 || lastState == null ? 0 : lastState.timeSinceStop + 1;
            this.energy                   = e.getEnergy();
        }
    }
    
    public RRobotState(RPoint location, double absoluteBearing, 
            double velocity, double lateralVelocity, double advancingVelocity,
            double deltaV, double heading, double deltaH, double distance, 
            double distanceFromWall, int distanceFromWallCategory, 
            double timeSinceVelocityChange, double timeSinceDirectionChange, 
            double timeSinceStop, double energy, int directionTraveling) {
        this.location                 = location.getCopy();
        this.absoluteBearing          = absoluteBearing;
        this.velocity                 = velocity;
        this.lateralVelocity          = lateralVelocity;
        this.advancingVelocity        = advancingVelocity;
        this.deltaV                   = deltaV;
        this.heading                  = heading;
        this.deltaH                   = deltaH;
        this.distance                 = distance;
        this.distanceFromWall         = distanceFromWall;
        this.distanceFromWallCategory = distanceFromWallCategory;
        this.timeSinceVelocityChange  = timeSinceVelocityChange;
        this.timeSinceDirectionChange = timeSinceDirectionChange;
        this.timeSinceStop            = timeSinceStop;
        this.energy                   = energy;
        this.directionTraveling       = directionTraveling;
        this.accelerating             = deltaV > 0;
        this.braking                 = deltaV < 0;
    }
    
    public RRobotState(RRobotState state) {
        this(state.location,
             state.absoluteBearing,
             state.velocity,
             state.lateralVelocity,
             state.advancingVelocity,
             state.deltaV,
             state.heading,
             state.deltaH,
             state.distance,
             state.distanceFromWall,
             state.distanceFromWallCategory,
             state.timeSinceVelocityChange,
             state.timeSinceDirectionChange,
             state.timeSinceStop,
             state.energy,
             state.directionTraveling);
    }
    
    public RRobotState getCopy() {
        return new RRobotState(this);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(absoluteBearing);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (accelerating ? 1231 : 1237);
        temp = Double.doubleToLongBits(advancingVelocity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (braking ? 1231 : 1237);
        temp = Double.doubleToLongBits(deltaH);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(deltaV);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + directionTraveling;
        temp = Double.doubleToLongBits(distance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(distanceFromWall);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + distanceFromWallCategory;
        temp = Double.doubleToLongBits(energy);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(gunHeat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(heading);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lateralVelocity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        temp = Double.doubleToLongBits(timeSinceDirectionChange);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(timeSinceStop);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(timeSinceVelocityChange);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(velocity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RRobotState other = (RRobotState) obj;
        if (Double.doubleToLongBits(absoluteBearing) != Double
                .doubleToLongBits(other.absoluteBearing))
            return false;
        if (accelerating != other.accelerating)
            return false;
        if (Double.doubleToLongBits(advancingVelocity) != Double
                .doubleToLongBits(other.advancingVelocity))
            return false;
        if (braking != other.braking)
            return false;
        if (Double.doubleToLongBits(deltaH) != Double
                .doubleToLongBits(other.deltaH))
            return false;
        if (Double.doubleToLongBits(deltaV) != Double
                .doubleToLongBits(other.deltaV))
            return false;
        if (directionTraveling != other.directionTraveling)
            return false;
        if (Double.doubleToLongBits(distance) != Double
                .doubleToLongBits(other.distance))
            return false;
        if (Double.doubleToLongBits(distanceFromWall) != Double
                .doubleToLongBits(other.distanceFromWall))
            return false;
        if (distanceFromWallCategory != other.distanceFromWallCategory)
            return false;
        if (Double.doubleToLongBits(energy) != Double
                .doubleToLongBits(other.energy))
            return false;
        if (Double.doubleToLongBits(gunHeat) != Double
                .doubleToLongBits(other.gunHeat))
            return false;
        if (Double.doubleToLongBits(heading) != Double
                .doubleToLongBits(other.heading))
            return false;
        if (Double.doubleToLongBits(lateralVelocity) != Double
                .doubleToLongBits(other.lateralVelocity))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (Double.doubleToLongBits(timeSinceDirectionChange) != Double
                .doubleToLongBits(other.timeSinceDirectionChange))
            return false;
        if (Double.doubleToLongBits(timeSinceStop) != Double
                .doubleToLongBits(other.timeSinceStop))
            return false;
        if (Double.doubleToLongBits(timeSinceVelocityChange) != Double
                .doubleToLongBits(other.timeSinceVelocityChange))
            return false;
        if (Double.doubleToLongBits(velocity) != Double
                .doubleToLongBits(other.velocity))
            return false;
        return true;
    }
}
