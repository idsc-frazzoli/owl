// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.Color;

import ch.ethz.idsc.owl.math.Hsluv;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.sca.Mod;

public class ColorLookup implements ColorDataIndexed {
  private static final int RESOLUTION = 127;
  private static final Tensor TRANSPARENT = Tensors.vectorDouble(0, 0, 0, 0).unmodifiable();

  /** precompute lookup table of hsluv colors for given lightness and alpha
   * 
   * @param lightness
   * @return */
  public static ColorLookup hsluv_lightness(double lightness) {
    Tensor tensor = Tensors.empty();
    for (Tensor hue : Subdivide.of(0, 1, RESOLUTION))
      tensor.append(ColorFormat.toVector( //
          Hsluv.of(hue.Get().number().doubleValue(), 1, lightness, 1)));
    return new ColorLookup(tensor);
  }
  // ---

  private final Tensor tensor;
  private final Mod mod;
  private final Color[] colors;

  private ColorLookup(Tensor tensor) {
    this.tensor = tensor;
    mod = Mod.function(tensor.length());
    colors = tensor.stream().map(ColorFormat::toColor).toArray(Color[]::new);
  }

  /** @param value in unit interval [0, 1]
   * @return */
  public Color get(double value) {
    return colors[(int) (value * RESOLUTION)]; // value == 1.0 maps to RESOLUTION
  }

  @Override // from ColorDataIndexed
  public Tensor apply(Scalar scalar) {
    return NumberQ.of(scalar) //
        ? tensor.get(mod.apply(scalar).number().intValue())
        : TRANSPARENT.copy();
  }

  @Override // from ColorDataIndexed
  public Color getColor(int index) {
    return colors[index];
  }

  @Override // from ColorDataIndexed
  public ColorLookup deriveWithAlpha(int alpha) {
    Tensor tensor = this.tensor.copy();
    Scalar scalar = RealScalar.of(alpha);
    tensor.set(entry -> scalar, Tensor.ALL, 3);
    return new ColorLookup(tensor);
  }
}
