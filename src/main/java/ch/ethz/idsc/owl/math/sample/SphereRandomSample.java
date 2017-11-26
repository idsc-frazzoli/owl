// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;

public class SphereRandomSample implements RandomSampleInterface {
  private static final Distribution UNIFORM = UniformDistribution.of(-1, 1);
  // ---
  private final Tensor center;
  private final Scalar radius;

  public SphereRandomSample(Tensor center, Scalar radius) {
    GlobalAssert.that(VectorQ.of(center));
    GlobalAssert.that(Sign.isPositiveOrZero(radius));
    this.center = center;
    this.radius = radius;
  }

  @Override
  public Tensor randomSample() {
    while (true) {
      Tensor vector = RandomVariate.of(UNIFORM, center.length());
      if (Scalars.lessEquals(Norm._2.ofVector(vector), RealScalar.ONE))
        return vector.multiply(radius).add(center);
    }
  }
}
