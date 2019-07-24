// code fluric
package ch.ethz.idsc.subare.demo.airport;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class AirportTest extends TestCase {
  public void testTerminalState() {
    Airport airport = new Airport();
    assertEquals(airport.isTerminal(Tensors.vector(Airport.LASTT, 0, Airport.VEHICLES)), true);
    assertEquals(airport.actions(Tensors.vector(Airport.LASTT, 0, Airport.VEHICLES)), Array.zeros(1, 1));
    assertEquals(airport.expectedReward(Tensors.vector(Airport.LASTT, 0, Airport.VEHICLES), Tensors.of(RealScalar.ZERO)), RealScalar.ZERO);
    assertEquals(
        airport.reward(Tensors.vector(Airport.LASTT, 0, Airport.VEHICLES), Tensors.of(RealScalar.ZERO), Tensors.vector(Airport.LASTT, 0, Airport.VEHICLES)),
        RealScalar.ZERO);
  }

  public void testCustProb() {
    Airport airport = new Airport();
    Tensor state = Tensors.vector(1, 2, 3);
    Tensor actions = airport.actions(state);
    assertEquals(actions.length(), 12);
    int probes = 3000;
    Clip clip = Clips.absolute(2);
    for (Tensor action : actions) {
      Tensor next = airport.move(state, action);
      assertEquals(next.get(0), RealScalar.of(2));
      // System.out.println(state + " " + action + " " + next);
      final Scalar R = airport.expectedReward(state, action);
      Scalar total = IntStream.range(0, probes).mapToObj(i -> airport.reward(state, action, next)).reduce(Scalar::add).get();
      Scalar mean = total.divide(DoubleScalar.of(probes));
      if (!clip.isInside(R.subtract(mean))) {
        System.out.println(state + " " + action);
        System.out.println(R + " " + mean);
        fail();
      }
    }
  }
}
