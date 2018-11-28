// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;

/** random samples from the interior of a n-dimensional sphere
 * 
 * implementation supports the use of Quantity */
public class SphereRandomSample implements RandomSampleInterface {
  private static final Distribution UNIFORM = UniformDistribution.of(-1, 1);

  /** @param center vector
   * @param radius non-negative
   * @return
   * @throws Exception if center is not a vector
   * @throws Exception if radius is negative */
  public static RandomSampleInterface of(Tensor center, Scalar radius) {
    switch (center.length()) {
    case 0:
      throw TensorRuntimeException.of(center, radius);
    case 1:
      Distribution distribution = UniformDistribution.of( //
          center.Get(0).subtract(radius), //
          center.Get(0).add(radius));
      return UniformRandomSample.of(distribution, 1);
    case 2:
      return new CircleRandomSample(center, radius);
    }
    return Scalars.isZero(radius) //
        ? new ConstantRandomSample(VectorQ.require(center))
        : new SphereRandomSample(center, radius);
  }

  // ---
  private final Tensor center;
  private final Scalar radius;

  private SphereRandomSample(Tensor center, Scalar radius) {
    this.center = VectorQ.require(center);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample() {
    while (true) {
      Tensor vector = RandomVariate.of(UNIFORM, center.length());
      if (Scalars.lessEquals(Norm._2.ofVector(vector), RealScalar.ONE))
        return vector.multiply(radius).add(center);
    }
  }
}
