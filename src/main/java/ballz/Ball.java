package ballz;

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
        return -Math.atan(dy / dx);
    }

    double distanceToOtherBall(Ball other) {
        double dx = other.x - x;
        double dy = other.y - y;
        return Math.sqrt(dx * dx + dy * dy) - radius - other.radius;
    }

    double getVelocity() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    double getKineticEnergy() {
        return 0.5 * mass * (vx * vx + vy * vy);
    }

    double getPotentialEnergy(double gravity) {
        return mass * gravity * y;
    }

    double getMomentum() {
        return mass * getVelocity();
    }
}
