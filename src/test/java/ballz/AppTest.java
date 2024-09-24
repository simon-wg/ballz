package ballz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ballz.Model.Ball;

/**
 * Unit test for simple App.
 */
class ModelTest {
    /**
     * Rigorous Test.
     */
    @Test
    void collisionTest() {

        Model model = new Model(100, 100);
        Ball[] balls = new Ball[2];

        balls[0] = model.new Ball(0, 0, -1, 0, 1, 1);
        balls[1] = model.new Ball(1, 1, 1, 0, 1, 1);

        model.collide(balls[0], balls[1]);

        double distance = balls[0].distanceToOtherBall(balls[1]);
        assertTrue(distance >= 0);
    }
}
