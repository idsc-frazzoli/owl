// code by ob
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** Rayleigh Noise calculated from uniform[0,1] distributed random with inverse transform sampling
 * 
 * This function creates a Gaussian noise vector on the Lie algebra and maps it to the Lie Group.
 * The noise can be added by either left-/ or right-multiplication
 * 
 * Noise on LieGroups: https://hal-mines-paristech.archives-ouvertes.fr/hal-01826025v2/document
 * https://en.wikipedia.org/wiki/Uniform_distribution_(continuous) */
public class LieGroupUniformNoise {
  private final LieExponential lieExponential;
  private final GeodesicDisplay geodesicDisplay;
  private final Tensor lowerLimits;
  private final Tensor upperLimits;

  private LieGroupUniformNoise(GeodesicDisplay geodesicDisplay, Tensor lowerLimits, Tensor upperLimits) {
    this.geodesicDisplay = geodesicDisplay;
    this.lieExponential = geodesicDisplay.lieExponential();
    this.lowerLimits = lowerLimits;
    this.upperLimits = upperLimits;
  }

  public Tensor getMean() {
    return lowerLimits.add(upperLimits).multiply(RationalScalar.HALF);
  }

  public Tensor getStandardDeviation() {
    return upperLimits.subtract(lowerLimits).divide(RealScalar.of(Math.sqrt(12)));
  }

  private Tensor whiteNoise() {
    return Array.of(l -> RealScalar.of(Math.random())).pmul(lowerLimits.subtract(upperLimits)).add(lowerLimits);
  }

  public final Tensor leftNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(lieExponential.exp(whiteNoise())).combine(tensor);
  }

  public final Tensor rightNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(tensor).combine(lieExponential.exp(whiteNoise()));
  }
}
