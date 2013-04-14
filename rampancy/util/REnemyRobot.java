package rampancy.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import rampancy.RampantRobot;
import robocode.ScannedRobotEvent;

public class REnemyRobot {
    public static final int MAX_HISTORY_SIZE = 1500;
    public static final int BOT_RADIUS = 18;
    
    public static final Stroke absoluteDangerStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 10 }, 0);
    public static final Stroke desiredMinStroke     = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 20 }, 0);
    public static final Stroke desiredMaxStroke     = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 20 }, 0);

    protected String name;
    protected int shotsFired;
    protected int shotsHit;
    protected double minSafeDistance;
    protected double preferredSafeDistance;
    
    protected ArrayList<REnemyListener> listeners;
    
    protected ArrayList<RRobotState> states;
    protected ArrayList<Double> trackedBearings;
    protected ArrayList<Integer> trackedDirections;
    
    protected Ellipse2D.Double absoluteDangerZone;
    protected Ellipse2D.Double desiredMinDistance;
    protected Ellipse2D.Double desiredMaxDistance;
    
    public REnemyRobot(String name) {
        this.name = name;
        listeners = new ArrayList<REnemyListener>();
        states = new ArrayList<RRobotState>();
        resetState();
        shotsFired = 0;
        shotsHit = 0;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean shotFired() {
        RRobotState curr = getCurrentState();
        RRobotState last = getLastState();
        if(last != null) {
            double diff = last.energy - curr.energy;
            return diff >= 0.1 && diff <= 3.0 ;
        }
        
        return false;
    }
    
    public double getShotPower() {
        if(!shotFired())  {
            return 0;
        }
        return getLastState().energy - getCurrentState().energy;
    }
    
    public int getShotsFired() {
        return shotsFired;
    }
    
    public int getShotsHit() {
        return shotsHit;
    }
    
    public void noteShotHit() {
        shotsHit++;
    }
    
    public double getMinSafeDistance() {
        return minSafeDistance;
    }
    
    public double getPreferredSafeDistance() {
        return preferredSafeDistance;
    }
    
    public void addState(RampantRobot reference, RBattlefield battlefield, ScannedRobotEvent e) {
        states.add(0, new RRobotState(reference, this, battlefield, e));
        if(states.size() >= MAX_HISTORY_SIZE) {
            states.remove(states.size() - 1); // remove the last state
        }
    }
    
    public RRobotState getCurrentState() {
        if(states.size() > 0) {
            return states.get(0);
        }
        return null;
    }
    
    public RRobotState getLastState() {
        if(states.size() > 1) {
            return states.get(1);
        }
        return null;
    }
    
    public ArrayList<RRobotState> getLastNStates(int n) {
        if(states.isEmpty()) {
            return null;
        }
        return new ArrayList<RRobotState>(states.subList(0, Math.min(n-1, states.size())));
    }
    
    public ArrayList<RRobotState> getStates() {
        return states;
    }
    
    public void clearStates() {
        states.clear();
    }
    
    public int getLastUsableSurfDirection() {
        if(trackedDirections.size() > 2) {
            return trackedDirections.get(2);
        }
        return 0;
    }
    
    public double getLastUsableBearing() {
        if(trackedBearings.size() > 2) {
            return trackedBearings.get(2);
        }
        return 0;
    }
    
    public void resetState() {
        //clearStates();
        trackedBearings = new ArrayList<Double>();
        trackedDirections = new ArrayList<Integer>();
    }
    
    public void update(RampantRobot reference, RBattlefield battlefield, ScannedRobotEvent e) {
        addState(reference, battlefield, e);
        updateTracking(reference, e);
        if(shotFired()) {
            notifyShotFired();
            shotsFired++;
        }
        updateZones();
        notifyListeners();
    }
    
    public void draw(Graphics2D g) {        
        RRobotState state = getCurrentState();
        if(state == null) {
            return;
        }
        
        g.setColor(Color.white);
        RPoint location = state.location;
        g.draw(new RRectangle(location));
    }

    // ---------- Private ---------- //
    private void updateZones() {
        RRobotState state = getCurrentState();
        if(state == null) {
            return;
        }
        
        RPoint location = state.location;
        double maxEscapeAngle = RUtil.computeMaxEscapeAngle(RUtil.computeBulletVelocity(0.1));

        minSafeDistance = 30 / Math.sin(maxEscapeAngle);
        absoluteDangerZone = new Ellipse2D.Double(location.x - minSafeDistance, 
                                                  location.y - minSafeDistance, 
                                                  minSafeDistance * 2, 
                                                  minSafeDistance * 2);
    }
    
    private void updateTracking(RampantRobot reference, ScannedRobotEvent e) {
        double lateralVelocity = reference.getVelocity() * Math.sin(e.getBearingRadians());
        int direction = lateralVelocity >= 0 ? 1 : -1;
        trackedDirections.add(0, direction);
        trackedBearings.add(0, getCurrentState().absoluteBearing + Math.PI);
    }
    
    // ---------- Listener Code ----------- //
    
    public boolean addListener(REnemyListener listener) {
        if(listeners.contains(listener))
            return false;
        
        return listeners.add(listener);
    }
    
    public boolean removeListener(REnemyListener listener) {
        return listeners.remove(listener);
    }
    
    public void removeAllListeners() {
        listeners.clear();
    }
    
    public void notifyListeners() {
        for(REnemyListener listener : listeners) {
            listener.enemyUpdated(this);
        }
    }
    
    public void notifyShotFired() {
        for(REnemyListener listener : listeners) {
            listener.shotFired(this);
        }
    }
}
