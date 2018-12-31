// code by jph
package ch.ethz.idsc.owl.gui;

import ch.ethz.idsc.owl.math.Hsluv;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.StrictColorDataIndexed;

public enum ColorLookup {
  ;
  /** precompute lookup table of hsluv colors for given lightness and alpha
   * 
   * @param length
   * @param lightness
   * @return strict color data table with given length number of colors */
  public static ColorDataIndexed hsluv_lightness(int length, double lightness) {
    Tensor tensor = Tensor.of(Subdivide.of(0, 1, length - 1).stream() //
        .map(hue -> Hsluv.of(hue.Get().number().doubleValue(), 1, lightness, 1)) //
        .map(ColorFormat::toVector));
    return StrictColorDataIndexed.create(tensor);
  }
}
