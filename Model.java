package ballz;

import java.lang.Math;
import java.util.HashMap;
import java.util.ArrayList;

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

	final double GRAVITY = -0.982;

	double EXPECTED_ENERGY;
	
	Ball[] balls;

	ArrayList<Double> energyList = new ArrayList<Double>();

	int physicsCounter = 0;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		
		// Initialize the model with a few balls
		balls = new Ball[3];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
		balls[2] = new Ball(width / 3, height * 0.4, -1, -1.0, 0.5);

		EXPECTED_ENERGY = 0.5 * (balls[0].vx * balls[0].vx + balls[0].vy * balls[0].vy +
				balls[1].vx * balls[1].vx + balls[1].vy * balls[1].vy +
				balls[2].vx * balls[2].vx + balls[2].vy * balls[2].vy) - GRAVITY * (balls[0].y + balls[1].y + balls[2].y);
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
					double[] b_rotated_v = rotate(b.vx, b.vy, -angle);
					double[] other_rotated_v = rotate(other.vx, other.vy, -angle);

					double[] b_unrotated_v = rotateInverse(other_rotated_v[0], b_rotated_v[1], -angle);
					double[] other_unrotated_v = rotateInverse(b_rotated_v[0], other_rotated_v[1], -angle);

					b.vx = b_unrotated_v[0];
					b.vy = b_unrotated_v[1];

					other.vx = other_unrotated_v[0];
					other.vy = other_unrotated_v[1];

					// move the balls so they don't overlap
				}
			}


			b.vy += deltaT * GRAVITY;
			
			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;
		
			energy += (b.vx * b.vx + b.vy * b.vy)/2 - GRAVITY * b.y;
		}

		// Adjust for energy loss by floating point inaccuracy
		// double energy_factor = Math.sqrt(EXPECTED_ENERGY / energy);
		// for (Ball b : balls) {
		// 	b.vx *= energy_factor;
		// 	b.vy *= energy_factor;
		// }
		// System.out.println("Energy loss/gain: " + (energy - EXPECTED_ENERGY));

		energyList.add(energy);
		if (physicsCounter % 1000 == 0) {
			double minEnergy = energyList.stream().min(Double::compare).get();
			double maxEnergy = energyList.stream().max(Double::compare).get();
			double avgEnergy = energyList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
			double percentileLow = energyList.stream().sorted().skip((int)(energyList.size() * 0.01)).findFirst().get();
			double percentileHigh = energyList.stream().sorted().skip((int)(energyList.size() * 0.99)).findFirst().get();
			System.out.println("Energy: " + energy + " Min: " + minEnergy + " Max: " + maxEnergy + " Avg: " + avgEnergy + " 1% low: " + percentileLow + " 1% high: " + percentileHigh);
		}

		physicsCounter++;
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

	double[] rotateInverse(double x, double y, double angle) {
		double new_x = x * Math.cos(angle) + y * Math.sin(angle);
		double new_y = -x * Math.sin(angle) + y * Math.cos(angle);
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
			return Math.atan2(dy, dx);
		}

		double distanceToOtherBall(Ball other) {
			double dx = other.x - x;
			double dy = other.y - y;
			return Math.sqrt(dx * dx + dy * dy) - radius - other.radius;
		}
	}
}
