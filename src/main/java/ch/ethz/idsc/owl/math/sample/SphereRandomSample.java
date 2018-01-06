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

/** random samples from the interior of a n-dimensional sphere */
public class SphereRandomSample implements RandomSampleInterface {
  private static final Distribution UNIFORM = UniformDistribution.of(-1, 1);

  /** @param center
   * @param radius
   * @return */
  public static RandomSampleInterface of(Tensor center, Scalar radius) {
    VectorQ.elseThrow(center);
    if (Sign.isNegative(radius))
      throw TensorRuntimeException.of(radius);
    if (Scalars.isZero(radius))
      return new ConstantRandomSample(center);
    return new SphereRandomSample(center, radius);
  }

  // ---
  private final Tensor center;
  private final Scalar radius;

  private SphereRandomSample(Tensor center, Scalar radius) {
    this.center = center;
    this.radius = radius;
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
