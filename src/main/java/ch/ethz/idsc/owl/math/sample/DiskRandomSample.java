// code by jph
// formula adapted from users "sigfpe" and "finnw" on stack-overflow
package ch.ethz.idsc.owl.math.sample;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** produces bivariate random samples uniformly draw from a circle with
 * given center and radius
 * 
 * implementation supports the use of Quantity */
/* package */ class DiskRandomSample implements RandomSampleInterface, Serializable {
  private final Tensor center;
  private final Scalar radius;

  /** @param center vector of length 2
   * @param radius non-negative
   * @throws Exception if given center is not a vector */
  DiskRandomSample(Tensor center, Scalar radius) {
    this.center = VectorQ.requireLength(center, 2);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor circle = CircleRandomSample.INSTANCE.randomSample(random);
    Scalar residue = Sqrt.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit()));
    return center.add(circle.multiply(radius.multiply(residue)));
  }
}
