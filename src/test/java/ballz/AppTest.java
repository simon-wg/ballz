package ballz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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

        testModel.GRAVITY = 0;

        testModel.balls.add(new Ball(2, 5, 1, 0, 1, 1));
        testModel.balls.add(new Ball(7, 5, -1, 0, 1, 1));

        System.out.println(testModel.balls.get(0).vx);
        System.out.println(testModel.balls.get(1).vx);

        testModel.step(0.0000000001);

        testModel.collide(testModel.balls.get(0), testModel.balls.get(1));

        assertEquals(testModel.balls.get(0).vx, -testModel.balls.get(1).vx);
    }

    @Test
    void conservationOfMomentum() {
        Model testModel = new Model(10, 10);

        testModel.balls.clear();
        testModel.balls.add(new Ball(2, 5, 1, 0, 1, 1));
        testModel.balls.add(new Ball(7, 5, -1, 0, 1, 1));

        double p_original = testModel.balls.get(0).getMomentum()
                + testModel.balls.get(1).getMomentum();

        for (int i = 0; i < 1e6; i++)
            testModel.collide(testModel.balls.get(0), testModel.balls.get(1));

        double p_new = testModel.balls.get(0).getMomentum()
                + testModel.balls.get(1).getMomentum();

        assertEquals(Math.round(p_original * 1e9), Math.round(p_new * 1e9));
    }

    @Test
    void testBallInitialization() {
        Model testModel = new Model(10, 10);

        testModel.balls.clear();

        testModel.balls.add(new Ball(2, 5, 1, 0, 1, 1));

        assertEquals(2, testModel.balls.get(0).x);
        assertEquals(5, testModel.balls.get(0).y);
        assertEquals(1, testModel.balls.get(0).vx);
        assertEquals(0, testModel.balls.get(0).vy);
        assertEquals(1, testModel.balls.get(0).radius);
        assertEquals(1, testModel.balls.get(0).mass);
    }

    @Test
    void testBallMovement() {
        Model testModel = new Model(10, 10);
        testModel.GRAVITY = 0;

        testModel.balls.clear();

        testModel.balls.add(new Ball(2, 5, 1, 1, 1, 1));
        testModel.step(1.0);

        assertEquals(3, testModel.balls.get(0).x);
        assertEquals(6, testModel.balls.get(0).y);
    }

    @Test
    void testBallCollisionWithWall() {
        Model testModel = new Model(10, 10);

        testModel.balls.clear();

        testModel.balls.add(new Ball(9, 5, 1, 0, 1, 1));
        testModel.step(1.0);

        assertTrue(testModel.balls.get(0).x <= testModel.areaWidth - testModel.balls.get(0).radius);
        assertEquals(-1, testModel.balls.get(0).vx);
    }

    @Test
    void testEnergyConservationAfterManySteps() {
        Model testModel = new Model(10, 10);
        testModel.GRAVITY = 0;

        testModel.balls.clear();

        testModel.balls.add(new Ball(2, 5, 1, 1, 1, 1));
        testModel.balls.add(new Ball(7, 5, -1, -1, 1, 1));

        double e_original = testModel.balls.get(0).getKineticEnergy()
                + (testModel.balls.get(0).y * testModel.balls.get(0).mass
                        + testModel.balls.get(1).y * testModel.balls.get(1).mass)
                        * testModel.GRAVITY
                + testModel.balls.get(1).getKineticEnergy();

        for (int i = 0; i < 1e6; i++)
            testModel.step(0.002);

        double e_new = testModel.balls.get(0).getKineticEnergy()
                + (testModel.balls.get(0).y * testModel.balls.get(0).mass
                        + testModel.balls.get(1).y * testModel.balls.get(1).mass)
                        * testModel.GRAVITY
                + testModel.balls.get(1).getKineticEnergy();

        System.out.println(e_original - e_new);

        assertEquals(Math.round(e_original * 1e9), Math.round(e_new * 1e9));
    }
}
