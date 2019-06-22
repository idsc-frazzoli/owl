// code by ob
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Rayleigh Noise calculated from uniform[0,1] distributed random with inverse transform sampling
 * 
 * This function creates a Gaussian noise vector on the Lie algebra and maps it to the Lie Group.
 * The noise can be added by either left-/ or right-multiplication
 * 
 * Noise on LieGroups: https://hal-mines-paristech.archives-ouvertes.fr/hal-01826025v2/document
 * Gaussian Noise: https://en.wikipedia.org/wiki/Rayleigh_distribution */
public class LieGroupRayleighNoise {
  private final LieExponential lieExponential;
  private final Tensor stdDeviation;
  private final GeodesicDisplay geodesicDisplay;

  private LieGroupRayleighNoise(GeodesicDisplay geodesicDisplay, Tensor stdDeviation) {
    this.geodesicDisplay = geodesicDisplay;
    this.lieExponential = geodesicDisplay.lieExponential();
    this.stdDeviation = stdDeviation;
  }

  // gives the mean of the function given the stdDeviation
  public Tensor getMean() {
    return stdDeviation.multiply(Sqrt.of(RealScalar.of(Math.PI / 2)));
  }

  public Tensor getStandardDeviation() {
    return stdDeviation;
  }

  private Tensor noise() {
    return Array.of(l -> Sqrt.FUNCTION.apply(Log.of(RealScalar.of(-2 * Math.random())))).pmul(stdDeviation);
  }

  public final Tensor leftNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(lieExponential.exp(noise())).combine(tensor);
  }

  public final Tensor rightNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(tensor).combine(lieExponential.exp(noise()));
  }
}