// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Color;

import ch.ethz.idsc.owl.math.Hsluv;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

public class ColorLookup {
  private static final int RESOLUTION = 127;

  /** precompute lookup table of hsluv colors for given lightness and alpha
   * 
   * @param lightness
   * @param alpha
   * @return */
  public static ColorLookup hsluv_lightness(double lightness, double alpha) {
    ColorLookup paletteLookup = new ColorLookup();
    int index = -1;
    for (Tensor hue : Subdivide.of(0, 1, RESOLUTION))
      paletteLookup.colors[++index] = //
          Hsluv.of(hue.Get().number().doubleValue(), 1, lightness, alpha);
    return paletteLookup;
  }
  // ---

  private final Color[] colors = new Color[RESOLUTION + 1];

  private ColorLookup() {
  }

  /** @param value in unit interval [0, 1]
   * @return */
  public Color get(double value) {
    int rnd = (int) (value * RESOLUTION); // value == 1.0 maps to RESOLUTION
    return colors[rnd];
  }
}
