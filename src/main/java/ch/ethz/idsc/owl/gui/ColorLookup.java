// code by jph
package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.tensor.Tensor;
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
   * @param lightness
   * @return strict color data table with given length number of colors */
  public static ColorDataIndexed hsluv_lightness(int length, double lightness) {
    Tensor tensor = Tensor.of(Subdivide.increasing(Clips.unit(), length - 1).stream() //
        .map(hue -> Hsluv.of(hue.Get().number().doubleValue(), 1, lightness, 1)) //
        .map(ColorFormat::toVector));
    return StrictColorDataIndexed.create(tensor);
  }

  /** @param length
   * @param colorDataGradient
   * @return
   * @see ColorDataGradient */
  public static ColorDataIndexed increasing(int length, ScalarTensorFunction colorDataGradient) {
    return StrictColorDataIndexed.create(Subdivide.increasing(Clips.unit(), length - 1).map(colorDataGradient));
  }

  /** @param length
   * @param colorDataGradient
   * @return
   * @see ColorDataGradient */
  public static ColorDataIndexed decreasing(int length, ScalarTensorFunction colorDataGradient) {
    return StrictColorDataIndexed.create(Subdivide.decreasing(Clips.unit(), length - 1).map(colorDataGradient));
  }
}
