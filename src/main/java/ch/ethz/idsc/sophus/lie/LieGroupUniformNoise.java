// code by ob
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** Rayleigh Noise calculated from uniform[0, 1] distributed random with inverse transform sampling
 * 
 * This function creates a Gaussian noise vector on the Lie algebra and maps it to the Lie Group.
 * The noise can be added by either left-/ or right-multiplication
 * 
 * Noise on Lie Groups: https://hal-mines-paristech.archives-ouvertes.fr/hal-01826025v2/document
 * https://en.wikipedia.org/wiki/Uniform_distribution_(continuous) */
public class LieGroupUniformNoise extends LieGroupAbstractNoise {
  private final Tensor lowerLimits;
  private final Tensor upperLimits;

  private LieGroupUniformNoise(LieGroup lieGroup, LieExponential lieExponential, Tensor lowerLimits, Tensor upperLimits) {
    super(lieGroup, lieExponential);
    this.lowerLimits = lowerLimits;
    this.upperLimits = upperLimits;
  }

  public Tensor getMean() {
    return lowerLimits.add(upperLimits).multiply(RationalScalar.HALF);
  }

  public Tensor getStandardDeviation() {
    return upperLimits.subtract(lowerLimits).divide(RealScalar.of(Math.sqrt(12)));
  }

  @Override
  protected Tensor noise() {
    return Array.of(l -> RealScalar.of(Math.random())).pmul(lowerLimits.subtract(upperLimits)).add(lowerLimits);
  }
}
