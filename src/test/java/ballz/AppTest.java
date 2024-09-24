package ballz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
class ModelTest {
    /**
     * Test to ensure the collision works well on a horizontal plane with equal
     * mass.
     */
    @Test
    void collisionTest() {
        Model testModel = new Model(10, 10);

        testModel.balls[0] = new Ball(2, 5, 1, 0, 1, 1);
        testModel.balls[1] = new Ball(7, 5, -1, 0, 1, 1);

        testModel.collide(testModel.balls[0], testModel.balls[1]);

        assertEquals(testModel.balls[0].vx, -1);
        assertEquals(testModel.balls[1].vx, 1);
    }

    @Test
    void outOfBoundsNeg() {
        Model testModel = new Model(10, 10);

        testModel.balls[0] = new Ball(-100, -100, 1, 1, 1, 1);
        testModel.step(0.1);

        System.out.println(testModel.balls[0].x);
        System.out.println(testModel.balls[0].y);

        assertTrue(testModel.balls[0].x >= testModel.balls[0].radius);
        assertTrue(testModel.balls[0].y >= testModel.balls[0].radius);
    }

    @Test
    void outOfBoundsPos() {
        Model testModel = new Model(10, 10);

        testModel.balls[0] = new Ball(100, 100, 1, 1, 1, 1);
        testModel.step(0.1);

        System.out.println(testModel.balls[0].x);
        System.out.println(testModel.balls[0].y);

        assertTrue(testModel.balls[0].x <= testModel.areaWidth - testModel.balls[0].radius);
        assertTrue(testModel.balls[0].y <= testModel.areaHeight - testModel.balls[0].radius);
    }

    @Test
    void conservationOfMomentum() {
        Model testModel = new Model(10, 10);

        double p_original = testModel.balls[0].mass * testModel.balls[0].getVelocity()
                + testModel.balls[1].mass * testModel.balls[1].getVelocity();

        for (int i = 0; i < 1e6; i++)
            testModel.collide(testModel.balls[0], testModel.balls[1]);

        double p_new = testModel.balls[0].mass * testModel.balls[0].getVelocity()
                + testModel.balls[1].mass * testModel.balls[1].getVelocity();

        assertEquals(Math.round(p_original * 1e9), Math.round(p_new * 1e9));

    }
}
