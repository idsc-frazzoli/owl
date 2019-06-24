// code by ob
package ch.ethz.idsc.sophus.lie;

import java.util.Random;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** Gaussian Noise applied on Lie Groups:
 * 
 * This function creates a Gaussian noise vector on the Lie algebra and maps it to the Lie Group.
 * The noise can be added by either left-/ or right-multiplication
 * 
 * Noise on Lie Groups: https://hal-mines-paristech.archives-ouvertes.fr/hal-01826025v2/document
 * Gaussian Noise: https://de.wikipedia.org/wiki/Normalverteilung (20.6.19) */
public class LieGroupGaussianNoise extends LieGroupAbstractNoise {
  private final Tensor mean;
  private final Tensor stdDeviation;

  private LieGroupGaussianNoise(LieGroup lieGroup, LieExponential lieExponential, Tensor mean, Tensor stdDeviation) {
    super(lieGroup, lieExponential);
    this.mean = mean;
    this.stdDeviation = stdDeviation;
  }

  public Tensor getMean() {
    return mean;
  }

  public Tensor getStandardDeviation() {
    return stdDeviation;
  }

  @Override
  protected Tensor noise() {
    Random random = new Random();
    return Array.of(l -> RealScalar.of(random.nextGaussian()), mean.length()).pmul(stdDeviation).add(mean);
  }
}
