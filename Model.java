package ballz;

import java.lang.Math;
import java.util.HashMap;

/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */
class Model {

	double areaWidth, areaHeight;

	double energy;

	static final double GRAVITY = -9.82;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1, 0, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.9, -0.5, 0, 0.3);
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
				double distance = b.distanceToOtherBall(other);

				if (distance <=0) {
					double angle = b.angleToOtherBall(other);

					// rotate the speed of the balls
					double[] new_speed = rotate(b.vx, b.vy, angle);
					double[] new_speed_other = rotate(other.vx, other.vy, angle);

					// swap the x speed of the balls
					b.vx = new_speed_other[0];
					other.vx = new_speed[0];

					b.vy = new_speed[1];
					other.vy = new_speed_other[1];

					double[] new_speed2 = rotate(b.vx, b.vy, -angle);
					double[] new_speed_other2 = rotate(other.vx, other.vy, -angle);

					b.vx = new_speed2[0];
					other.vx = new_speed_other2[0];

					b.vy = new_speed2[1];
					other.vy = new_speed_other2[1];

					// move the balls so they don't overlap
				}
			}


			b.vy += deltaT * GRAVITY;
			
			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;
		
			energy += (b.vx * b.vx + b.vy * b.vy)/2 - GRAVITY * b.y;
		}
		System.out.println(energy);
	}

	double[] rectToPolar(double x, double y) {
		double r = Math.sqrt(x * x + y * y);
		double theta = Math.atan2(y, x);
		return new double[]{r, theta};
	}

	double[] polarToRect(double r, double theta) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		return new double[]{x, y};
	}

	double[] rotate(double x, double y, double angle) {
		double new_x = x * Math.cos(angle) - y * Math.sin(angle);
		double new_y = x * Math.sin(angle) + y * Math.cos(angle);
		return new double[]{new_x, new_y};
	}
	
	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius;

		double angleToOtherBall(Ball other) {
			double dx = other.x - x;
			double dy = other.y - y;
			return Math.atan(dy / dx);
		}

		double distanceToOtherBall(Ball other) {
			double dx = other.x - x;
			double dy = other.y - y;
			return Math.sqrt(dx * dx + dy * dy) - radius - other.radius;
		}
	}
}
