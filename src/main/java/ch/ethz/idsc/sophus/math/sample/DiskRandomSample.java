// code by jph
// formula adapted from users "sigfpe" and "finnw" on stack-overflow
package ch.ethz.idsc.sophus.math.sample;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.hs.sn.S2RandomSample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** produces bivariate random samples uniformly draw from a circle with
 * given center and radius
 * 
 * implementation supports the use of Quantity
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Disk.html">Disk</a> */
/* package */ class DiskRandomSample implements RandomSampleInterface, Serializable {
  private final Tensor center;
  private final Scalar radius;

  /** @param center vector of length 2
   * @param radius non-negative */
  DiskRandomSample(Tensor center, Scalar radius) {
    this.center = center;
    this.radius = radius;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor circle = S2RandomSample.INSTANCE.randomSample(random);
    Scalar residue = Sqrt.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit()));
    return center.add(circle.multiply(radius.multiply(residue)));
  }
}
