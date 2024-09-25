package ballz;

import java.util.List;
import java.util.ArrayList;

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

    double GRAVITY = -9.81;

    double energy;

    List<Ball> balls = new ArrayList<Ball>();

    double EXPECTED_ENERGY;

    int stepCount = 0;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls.add(new Ball(width / 2, height - 0.5, -1.6, 0.6, 0.5, 10));
        balls.add(new Ball(2 * width / 3, height / 4, 0.6, 1.7, 0.2, 1));

        EXPECTED_ENERGY = calculateExpectedEnergy(balls);
    }

    void step(double deltaT) {
        stepCount++;
        energy = 0;
        for (int i = 0; i < balls.size(); i++) {
            Ball a = balls.get(i);

            if (a.x <= a.radius || a.x >= areaWidth - a.radius) {
                a.vx *= -1; // change direction of ball
            }
            if (a.y <= a.radius || a.y >= areaHeight - a.radius) {
                a.vy *= -1;
            }
            for (int j = i + 1; j < balls.size() && i < balls.size() - 1; j++) {
                Ball b = balls.get(j);
                // detect collision with the border
                if (a.distanceToOtherBall(b) <= 0) {
                    if (sanityCheck(a, b)) {
                        collide(a, b);
                    }
                    // rotate the speed of the balls
                }
            }

            // compute new position according to the speed of the ball
            a.x += deltaT * a.vx;
            a.y += deltaT * a.vy + 0.5 * GRAVITY * Math.pow(deltaT, 2);

            a.vy += GRAVITY * deltaT; // apply gravity

            // prevent out of bounds
            // a.x = Math.clamp(a.x, a.radius, areaWidth - a.radius);
            // a.y = Math.clamp(a.y, a.radius, areaHeight - a.radius);

            energy += a.getKineticEnergy() - a.getPotentialEnergy(GRAVITY);
        }

        if (stepCount == 100) {
            System.out.println("Energy: " + energy);
            System.out.println("Expected Energy: " + EXPECTED_ENERGY);
            System.out.println("Energy Difference: " + (energy / EXPECTED_ENERGY));
            stepCount = 0;
        }

        // Adjust for energy loss by floating point inaccuracy
        double energy_factor = Math.sqrt(EXPECTED_ENERGY / energy);
        for (Ball b : balls) {
            b.vx *= (energy_factor);
            b.vy *= (energy_factor);
        }
    }

    void collide(Ball a, Ball b) {
        double angle = a.angleToOtherBall(b);
        double[] a_rotated = rotate(a.vx, a.vy, angle);
        double[] b_rotated = rotate(b.vx, b.vy, angle);

        double a_vx = (a.mass - b.mass) / (a.mass + b.mass) * a_rotated[0]
                + 2 * b.mass / (a.mass + b.mass) * b_rotated[0];

        double b_vx = (b.mass - a.mass) / (a.mass + b.mass) * b_rotated[0]
                + 2 * a.mass / (a.mass + b.mass) * a_rotated[0];

        double[] a_unrotated = rotateInverse(a_vx, a_rotated[1], angle);
        double[] b_unrotated = rotateInverse(b_vx, b_rotated[1], angle);

        a.vx = a_unrotated[0];
        a.vy = a_unrotated[1];

        b.vx = b_unrotated[0];
        b.vy = b_unrotated[1];
    }

    double[] rotate(double x, double y, double angle) {
        double new_x = x * Math.cos(angle) - y * Math.sin(angle);
        double new_y = x * Math.sin(angle) + y * Math.cos(angle);
        return new double[] { new_x, new_y };
    }

    double[] rotateInverse(double x, double y, double angle) {
        return rotate(x, y, -angle);
    }

    boolean sanityCheck(Ball a, Ball b) {
        return (sanityCheckX(a, b) || sanityCheckY(a, b));
    }

    boolean sanityCheckX(Ball a, Ball b) {
        if (a.x > b.x && b.vx > a.vx)
            return true;
        if (a.x < b.x && a.vx > b.vx)
            return true;
        return false;
    }

    boolean sanityCheckY(Ball a, Ball b) {
        if (a.y > b.y && b.vy > a.vy)
            return true;
        if (a.y < b.y && a.vy > b.vy)
            return true;
        return false;
    }

    double calculateExpectedEnergy(List<Ball> balls) {
        double energy = 0;
        for (Ball b : balls) {
            energy += b.getKineticEnergy() - b.getPotentialEnergy(GRAVITY);
        }
        return energy;
    }
}
