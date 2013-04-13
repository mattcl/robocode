package rampancy.util.external;

import rampancy.util.RPoint;
import robocode.Rules;
import robocode.util.Utils;

/**
 * A simulator class I wrote to make simulation simple.
 * 
 * @author Chase
 *
 * ZLIB License: This software is provided 'as-is', without any express or
 * implied warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not claim
 * that you wrote the original software. If you use this software in a product,
 * an acknowledgment in the product documentation would be appreciated but is
 * not required.
 * 
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */
public final class MovSim2 { // Change name from Simulate to MovSim2 to avoid conflicts
    public RPoint position; // Replace Point2D.Double with my RPoint
    public double heading;
    public double velocity;
    public double headingDelta;
    public double maxVelocity;
    public double angleToTurn;
    public int    direction;

    /**
     * Create a new Simulate class
     */
    public MovSim2() {
        position = new RPoint();
        maxVelocity = Rules.MAX_VELOCITY;
        direction = 1;
    }
   
    /**
     * Add easier to use constructor
     */
    public MovSim2(RPoint position, double heading, double velocity, double angleToTurn, int direction) {
        this.position = position.getCopy();
        this.heading = heading;
        this.velocity = velocity;
        this.angleToTurn = angleToTurn;
        this.direction = direction;
        this.maxVelocity = Rules.MAX_VELOCITY;
    }

    /**
     * We can easily set the position with this.
     */
    public void setLocation(double x, double y) {
        position.x = x;
        position.y = y;
    }

    /**
     * Here we just make a copy of the simulator.
     */
    public MovSim2 copy() {
        MovSim2 copy = new MovSim2();
        copy.position.setLocation(this.position);
        copy.heading = this.heading;
        copy.velocity = this.velocity;
        copy.headingDelta = this.headingDelta;
        copy.maxVelocity = this.maxVelocity;
        copy.angleToTurn = this.angleToTurn;
        copy.direction = this.direction;
        return copy;
    }

    /**
     * We calculate one step or turn into the future, and update the values
     * accordingly
     */
    public void step() {
        // //////////////
        // Heading
        double lastHeading = heading;
        double turnRate = Rules.getTurnRateRadians(Math.abs(velocity));
        double turn = Math.min(turnRate, Math.max(angleToTurn, -turnRate));
        heading = Utils.normalNearAbsoluteAngle(heading + turn);
        angleToTurn -= turn;

        // //////////////
        // Movement
        if (direction != 0 || velocity != 0.0) {
            // //////////////
            // Acceleration
            double acceleration = 0;
            double speed = Math.abs(velocity);
            maxVelocity = Math.abs(maxVelocity);

            // Determine the current direction
            int velDirection = (velocity > 0 ? (int) 1 : (int) -1);

            // Handles the zero direction, which means stop
            if (direction == 0) {
                maxVelocity = 0;
                direction = velDirection;
            }

            // Handles speedup from zero
            if (speed < 0.000001) {
                velDirection = direction;
            }

            // Check if we are speeding up or slowing down
            if (velDirection == direction) {
                // We are speeding up or maintaining speed
                if (speed <= maxVelocity) {
                    // We are speeding up
                    acceleration = Math.min(Rules.ACCELERATION, maxVelocity
                            - speed);
                } else {
                    // We are slowing down in the same direction
                    if (speed > maxVelocity)
                        acceleration = Math.max(-Rules.DECELERATION,
                                maxVelocity - speed);
                    // else we are maintaining speed (do nothing)
                }
            } else {
                // We are slowing down or stopping
                if (speed < Rules.DECELERATION) {
                    // Limit pass over zero, special rules are here for this
                    double beyondZero = Math.abs(speed - Rules.DECELERATION);
                    acceleration = speed + (beyondZero /= 2.0);

                    // Limit our acceleration so it does not go beyond max when
                    // passing over zero
                    if (beyondZero > maxVelocity)
                        acceleration = speed + maxVelocity;
                } else {
                    // Otherwise
                    acceleration = Rules.DECELERATION;
                }
            }

            // Apply the direction to the acceleration, so we don't have
            // to have a case for both directions
            acceleration *= direction;

            // //////////////
            // Velocity
            velocity += acceleration;

            // //////////////
            // Position
            position.x += Math.sin(heading) * velocity;
            position.y += Math.cos(heading) * velocity;
        }

        headingDelta = Utils.normalRelativeAngle(heading - lastHeading);
    }
}