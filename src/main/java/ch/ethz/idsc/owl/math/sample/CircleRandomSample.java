// code by jph
// formula adapted from users "sigfpe" and "finnw" on stack-overflow
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** produces bi-variate random samples uniformly draw from a circle with
 * given center and radius */
public class CircleRandomSample implements RandomSampleInterface {
  private static final Distribution THETA = UniformDistribution.of(-Math.PI, Math.PI);
  // ---
  private final Tensor center;
  private final Scalar radius;

  /** @param center vector of length 2
   * @param radius non-negative */
  public CircleRandomSample(Tensor center, Scalar radius) {
    this.center = VectorQ.requireLength(center, 2);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample() {
    Scalar theta = RandomVariate.of(THETA);
    Scalar residue = Sqrt.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit()));
    return center.add(Tensors.of(Cos.FUNCTION.apply(theta), Sin.FUNCTION.apply(theta)) //
        .multiply(radius.multiply(residue)));
  }
}
