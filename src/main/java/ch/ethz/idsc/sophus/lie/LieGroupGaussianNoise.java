// code by ob
package ch.ethz.idsc.sophus.lie;

import java.util.Random;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** Gaussian Noise applied on Lie Groups:
 * 
 * This function creates a Gaussian noise vector on the Lie algebra and maps it to the Lie Group.
 * The noise can be added by either left-/ or right-multiplication
 * 
 * Noise on LieGroups: https://hal-mines-paristech.archives-ouvertes.fr/hal-01826025v2/document
 * Gaussian Noise: https://de.wikipedia.org/wiki/Normalverteilung (20.6.19) */
public class LieGroupGaussianNoise {
  private final LieExponential lieExponential;
  private final Tensor mean;
  private final Tensor stdDeviation;
  private final GeodesicDisplay geodesicDisplay;

  private LieGroupGaussianNoise(GeodesicDisplay geodesicDisplay, Tensor mean, Tensor stdDeviation) {
    this.geodesicDisplay = geodesicDisplay;
    this.lieExponential = geodesicDisplay.lieExponential();
    this.mean = mean;
    this.stdDeviation = stdDeviation;
  }

  private Tensor whiteNoise() {
    Random rand = new Random();
    return Array.of(l -> RealScalar.of(rand.nextGaussian()), mean.length()).pmul(stdDeviation).add(mean);
  }

  public final Tensor leftNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(lieExponential.exp(whiteNoise())).combine(tensor);
  }

  public final Tensor rightNoise(Tensor tensor) {
    return geodesicDisplay.lieGroup().element(tensor).combine(lieExponential.exp(whiteNoise()));
  }
}
