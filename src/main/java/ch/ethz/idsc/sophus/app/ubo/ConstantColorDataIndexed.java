// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;

public class ConstantColorDataIndexed implements ColorDataIndexed {
  /** @param color
   * @return */
  public static final ColorDataIndexed of(Color color) {
    return new ConstantColorDataIndexed(color);
  }

  /***************************************************/
  private final Color color;
  private final Tensor vector;

  private ConstantColorDataIndexed(Color color) {
    this.color = color;
    vector = ColorFormat.toVector(color).unmodifiable();
  }

  @Override
  public Tensor apply(Scalar scalar) {
    return NumberQ.of(scalar) //
        ? vector
        : Array.zeros(4);
  }

  @Override
  public Color getColor(int index) {
    return color;
  }

  @Override
  public int length() {
    return Integer.MAX_VALUE;
  }

  @Override
  public ColorDataIndexed deriveWithAlpha(int alpha) {
    return new ConstantColorDataIndexed(new Color( //
        color.getRed(), //
        color.getGreen(), //
        color.getBlue(), //
        alpha));
  }
}
