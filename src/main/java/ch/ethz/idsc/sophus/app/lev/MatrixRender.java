// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class MatrixRender {
  public static MatrixRender absoluteOne(Graphics2D graphics, ColorDataIndexed colorDataIndexed, ColorDataGradient colorDataGradient) {
    return new MatrixRender(graphics, colorDataIndexed, //
        value -> ColorFormat.toColor(colorDataGradient.apply(Clips.absoluteOne().rescale(value))));
  }

  public static MatrixRender arcTan(Graphics2D graphics, ColorDataIndexed colorDataIndexed, ColorDataGradient colorDataGradient) {
    Clip clip = Clips.absolute(Pi.HALF);
    return new MatrixRender(graphics, colorDataIndexed, //
        value -> ColorFormat.toColor(colorDataGradient.apply(clip.rescale(ArcTan.FUNCTION.apply(value)))));
  }

  public static MatrixRender of(Graphics2D graphics, ColorDataIndexed colorDataIndexed, Color color) {
    return new MatrixRender(graphics, colorDataIndexed, value -> color);
  }

  /***************************************************/
  private final Graphics2D graphics;
  private final ColorDataIndexed colorDataIndexed;
  private final Function<Scalar, Color> function;
  private ScalarUnaryOperator round = s -> s;

  private MatrixRender(Graphics2D graphics, ColorDataIndexed colorDataIndexed, Function<Scalar, Color> function) {
    this.graphics = graphics;
    this.colorDataIndexed = colorDataIndexed;
    this.function = function;
  }

  public void renderMatrix(Tensor matrix, int pix, int piy) {
    Tensor rounded = matrix.map(round);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int max = rounded.flatten(-1) //
        .map(Object::toString) //
        .mapToInt(fontMetrics::stringWidth) //
        .max() //
        .getAsInt();
    int width = max + 3;
    for (int row = 0; row < rounded.length(); ++row) {
      Tensor vector = matrix.get(row);
      for (int col = 0; col < vector.length(); ++col) {
        graphics.setColor(function.apply(vector.Get(col)));
        int tpx = pix + width * col;
        int tpy = piy + fheight * row;
        graphics.fillRect(tpx, tpy, width, fheight);
        String string = rounded.Get(row, col).toString();
        int sw = fontMetrics.stringWidth(string);
        String show = string;
        graphics.setColor(new Color(255, 255, 255, 128));
        graphics.drawString(show, tpx + width - sw - 1, tpy + fheight - 2);
        graphics.setColor(colorDataIndexed.getColor(row));
        graphics.drawString(show, tpx + width - sw, tpy + fheight - 1);
      }
    }
  }

  public void setScalarMapper(ScalarUnaryOperator scalarUnaryOperator) {
    round = scalarUnaryOperator;
  }
}
