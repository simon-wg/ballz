package ballz;

import java.lang.Math;
import java.util.HashMap;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish,
 * you can improve the design.
 *
 * @author Simon Robillard
 *
 */
class Model {

    double areaWidth, areaHeight;

    final double GRAVITY = -0;

    double energy;

    Ball[] balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[3];
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 1);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 1);
        balls[2] = new Ball(width / 3, height * 0.4, -1, -1.0, 0.5, 10);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        energy = 0;
        HashMap<Ball, Ball> collisions = new HashMap<>();
        for (Ball b : balls) {
            // detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {
                b.vx *= -1; // change direction of ball
                b.x = Math.clamp(b.x, b.radius, areaWidth - b.radius); // make sure the ball is within the area
            }
            if (b.y < b.radius || b.y > areaHeight - b.radius) {
                b.vy *= -1;
                b.y = Math.clamp(b.y, b.radius, areaHeight - b.radius);
            }

            // detect collision with other balls
            for (Ball other : balls) {
                if (b == other || (collisions.containsKey(b) && collisions.get(b) == other)) {
                    continue;
                }
                collisions.put(b, other);

                if (b.distanceToOtherBall(other) <= 0) {
                    // rotate the speed of the balls
                    collide(b, other);
                }
            }

            b.vy += deltaT * GRAVITY;

            // compute new position according to the speed of the ball
            b.x += deltaT * b.vx;
            b.y += deltaT * b.vy;

            energy += b.mass * Math.pow(b.getVelocity(), 2) / 2 - b.mass * GRAVITY * b.y;
        }

        System.out.println(energy);

        // Adjust for energy loss by floating point inaccuracy
        // double energy_factor = Math.sqrt(EXPECTED_ENERGY / energy);
        // for (Ball b : balls) {
        // b.vx *= energy_factor;
        // b.vy *= energy_factor;
        // }
    }

    void collide(Ball b, Ball other) {
        double distance = b.distanceToOtherBall(other);

        double angle = b.angleToOtherBall(other);
        double[] b_rotated = rotate(b.vx, b.vy, -angle);
        double[] other_rotated = rotate(other.vx, other.vy, -angle);

        double b_vx = (b.mass - other.mass) / (b.mass + other.mass) * b_rotated[0]
                + 2 * other.mass / (b.mass + other.mass) * other_rotated[0];

        double other_vx = 2 * b.mass / (b.mass + other.mass) * b_rotated[0];

        double[] b_unrotated = rotateInverse(b_vx, b_rotated[1], -angle);
        double[] other_unrotated = rotateInverse(other_vx, other_rotated[1], -angle);

        b.vx = b_unrotated[0];
        b.vy = b_unrotated[1];

        other.vx = other_unrotated[0];
        other.vy = other_unrotated[1];

        b.x += distance / 2 * Math.cos(angle);
        b.y += distance / 2 * Math.sin(angle);

        other.x -= distance / 2 * Math.cos(angle);
        other.y -= distance / 2 * Math.sin(angle);

    }

    double[] rectToPolar(double x, double y) {
        double r = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(y, x);
        return new double[] { r, theta };
    }

    double[] polarToRect(double r, double theta) {
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);
        return new double[] { x, y };
    }

    double[] rotate(double x, double y, double angle) {
        double new_x = x * Math.cos(angle) - y * Math.sin(angle);
        double new_y = x * Math.sin(angle) + y * Math.cos(angle);
        return new double[] { new_x, new_y };
    }

    double[] rotateInverse(double x, double y, double angle) {
        return rotate(x, y, -angle);
    }

    /**
     * Simple inner class describing balls.
     */
    class Ball {

        Ball(double x, double y, double vx, double vy, double r, double m) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
            this.mass = m;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other
         * attributes.
         */
        double x, y, vx, vy, radius, mass;

        double angleToOtherBall(Ball other) {
            double dx = other.x - x;
            double dy = other.y - y;
            return Math.atan2(dy, dx);
        }

        double distanceToOtherBall(Ball other) {
            double dx = other.x - x;
            double dy = other.y - y;
            return Math.sqrt(dx * dx + dy * dy) - radius - other.radius;
        }

        double getVelocity() {
            return Math.sqrt(vx * vx + vy * vy);
        }
    }
}
