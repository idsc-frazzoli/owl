// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** random sample on n-dimensional sphere
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sphere.html">Sphere</a> */
public class SnRandomSample implements RandomSampleInterface, Serializable {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public static RandomSampleInterface of(int dimension) {
    switch (dimension) {
    case 2:
      return S2RandomSample.INSTANCE;
    default:
      return new SnRandomSample(dimension);
    }
  }

  // ---
  private final RandomSampleInterface randomSampleInterface;

  private SnRandomSample(int dimension) {
    this.randomSampleInterface = BallRandomSample.of(Array.zeros(dimension), RealScalar.ONE);
  }

  @Override
  public Tensor randomSample(Random random) {
    return NORMALIZE.apply(randomSampleInterface.randomSample(random));
  }
}
