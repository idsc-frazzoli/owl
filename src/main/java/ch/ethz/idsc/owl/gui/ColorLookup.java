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
  private static final int RESOLUTION = 127;

  /** precompute lookup table of hsluv colors for given lightness and alpha
   * 
   * @param lightness
   * @return */
  public static ColorDataIndexed hsluv_lightness(double lightness) {
    Tensor tensor = Tensor.of(Subdivide.of(0, 1, RESOLUTION).stream() //
        .map(hue -> Hsluv.of(hue.Get().number().doubleValue(), 1, lightness, 1)) //
        .map(ColorFormat::toVector));
    return StrictColorDataIndexed.create(tensor);
  }
}
