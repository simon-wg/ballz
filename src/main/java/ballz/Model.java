package ballz;

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

    double GRAVITY = -9.82;

    double energy;

    Ball[] balls;

    double EXPECTED_ENERGY;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[3];
        balls[0] = new Ball(width / 5, height * 0.7, 1, 0.6, 0.2, 1);
        balls[1] = new Ball(2 * width / 3, height * 0.8, -1, -1.2, 0.3, 1);
        balls[2] = new Ball(width / 3, height * 0.4, -1, -1.0, 0.5, 50);

        EXPECTED_ENERGY = calculateExpectedEnergy(balls);
    }

    void step(double deltaT) {
        energy = 0;
        for (int i = 0; i < balls.length; i++) {
            Ball a = balls[i];

            if (a.x <= a.radius || a.x >= areaWidth - a.radius) {
                a.vx *= -1; // change direction of ball
            }
            if (a.y <= a.radius || a.y >= areaHeight - a.radius) {
                a.vy *= -1;
            }
            for (int j = i + 1; j < balls.length && i < balls.length - 1; j++) {
                Ball b = balls[j];
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

            a.x = Math.clamp(a.x, -a.radius, areaWidth); // make sure the ball is within the area
            a.y = Math.clamp(a.y, -a.radius, areaHeight);

            energy += a.mass * Math.pow(a.getVelocity(), 2) / 2 - a.mass * GRAVITY * a.y;
        }

        System.out.println(energy);

        // Adjust for energy loss by floating point inaccuracy
        // double energy_factor = Math.sqrt(EXPECTED_ENERGY / energy);
        // for (Ball b : balls) {
        // b.vx *= Math.sqrt(energy_factor);
        // b.vy *= Math.sqrt(energy_factor);
        // }
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
        return (sanityCheckAx(a, b) || sanityCheckAy(a, b) || sanityCheckBx(a, b) || sanityCheckBy(a, b));
    }

    boolean sanityCheckAx(Ball a, Ball b) {
        if (a.x > b.x && b.vx > a.vx)
            return true;
        if (a.x < b.x && a.vx > b.vx)
            return true;
        return false;
    }

    boolean sanityCheckAy(Ball a, Ball b) {
        if (a.y > b.y && b.vy > a.vy)
            return true;
        if (a.y < b.y && a.vy > b.vy)
            return true;
        return false;
    }

    boolean sanityCheckBx(Ball a, Ball b) {
        if (b.x > a.x && a.vx > b.vx)
            return true;
        if (b.x < a.x && b.vx > a.vx)
            return true;
        return false;
    }

    boolean sanityCheckBy(Ball a, Ball b) {
        if (b.y > a.y && a.vx > b.vy)
            return true;
        if (b.y < a.y && b.vx > a.vy)
            return true;
        return false;
    }

    double calculateExpectedEnergy(Ball[] balls) {
        double energy = 0;
        for (Ball b : balls) {
            energy += b.mass * Math.pow(b.getVelocity(), 2) / 2 - b.mass * GRAVITY * b.y;
        }
        return energy;
    }
}
