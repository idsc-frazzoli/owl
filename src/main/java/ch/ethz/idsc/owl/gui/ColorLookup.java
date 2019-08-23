// code by jph
package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.StrictColorDataIndexed;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clips;

/** @see ColorDataIndexed */
public enum ColorLookup {
  ;
  /** precompute lookup table of hsluv colors for given lightness and alpha
   * 
   * @param length
   * @param lightness in range [0, 1], use 0.5 for greatest variability in hue
   * @return strict color data table with given length number of colors */
  public static ColorDataIndexed hsluv_lightness(int length, double lightness) {
    return StrictColorDataIndexed.of(Subdivide.increasing(Clips.unit(), length - 1) //
        .map(hue -> ColorFormat.toVector(Hsluv.of(hue.number().doubleValue(), 1, lightness, 1))));
  }

  /** @param length
   * @param colorDataGradient
   * @return
   * @see ColorDataGradient */
  public static ColorDataIndexed increasing(int length, ScalarTensorFunction colorDataGradient) {
    return StrictColorDataIndexed.of(Subdivide.increasing(Clips.unit(), length - 1).map(colorDataGradient));
  }

  /** @param length
   * @param colorDataGradient
   * @return
   * @see ColorDataGradient */
  public static ColorDataIndexed decreasing(int length, ScalarTensorFunction colorDataGradient) {
    return StrictColorDataIndexed.of(Subdivide.decreasing(Clips.unit(), length - 1).map(colorDataGradient));
  }
}
