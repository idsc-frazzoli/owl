// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import java.util.Random;
import java.util.function.Predicate;

import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BiasedLaneSampleTest extends TestCase {
  public void testSimple() {
    Chop._03.requireClose(Tensors.vector(.2, .2, .2, .2, .2), run(sample -> false));
    Chop._03.requireClose(Tensors.vector(0, 1, 0, 0, 0), run(sample -> Extract2D.FUNCTION.apply(sample).equals(Array.zeros(2))));
  }

  private Tensor run(Predicate<Tensor> predicate) {
    Tensor ctrl = Tensors.of( //
        Tensors.vector(-1, 0, 0), //
        Tensors.vector(0, 0, 0), //
        Tensors.vector(1, 0, 0), //
        Tensors.vector(2, 0, 0), //
        Tensors.vector(3, 0, 0));
    LaneInterface laneInterface = new LaneInterface() {
      @Override
      public Tensor controlPoints() {
        return ctrl;
      }

      @Override
      public Tensor midLane() {
        return ctrl;
      }

      @Override
      public Tensor leftBoundary() {
        return ctrl;
      }

      @Override
      public Tensor rightBoundary() {
        return ctrl;
      }

      @Override
      public Tensor margins() {
        return Tensor.of(ctrl.stream().map(t -> RealScalar.ZERO));
      }
    };
    RandomSampleInterface biasedLaneSample = BiasedLaneSample.along(laneInterface);
    for (int i = 0; i < 10000; i++) {
      Tensor sample = biasedLaneSample.randomSample(new Random());
      if (predicate.test(sample))
        ((BiasedLaneSample) biasedLaneSample).encourage();
      else
        ((BiasedLaneSample) biasedLaneSample).discourage();
    }
    return ((BiasedLaneSample) biasedLaneSample).weights();
  }
}
