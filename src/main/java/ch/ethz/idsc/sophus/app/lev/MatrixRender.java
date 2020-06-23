// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class MatrixRender {
  public static MatrixRender absoluteOne(Graphics2D graphics, Color color_text, ColorDataGradient colorDataGradient) {
    return new MatrixRender(graphics, color_text, //
        value -> ColorFormat.toColor(colorDataGradient.apply(Clips.absoluteOne().rescale(value))));
  }

  public static MatrixRender arcTan(Graphics2D graphics, Color color_text, ColorDataGradient colorDataGradient) {
    Clip clip = Clips.absolute(Pi.HALF);
    return new MatrixRender(graphics, color_text, //
        value -> ColorFormat.toColor(colorDataGradient.apply(clip.rescale(ArcTan.FUNCTION.apply(value)))));
  }

  public static MatrixRender of(Graphics2D graphics, Color color_text, Color color) {
    return new MatrixRender(graphics, color_text, value -> color);
  }

  /***************************************************/
  private final Graphics2D graphics;
  private final Color color_text;
  private final Function<Scalar, Color> function;

  private MatrixRender(Graphics2D graphics, Color color_text, Function<Scalar, Color> function) {
    this.graphics = graphics;
    this.color_text = color_text;
    this.function = function;
  }

  public void renderMatrix(Tensor matrix, ScalarUnaryOperator round, int pix, int piy) {
    Tensor rounded = matrix.map(round);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int max = rounded.flatten(-1) //
        .map(Object::toString) //
        .mapToInt(fontMetrics::stringWidth) //
        .max() //
        .getAsInt();
    int width = max + 3;
    for (int inx = 0; inx < rounded.length(); ++inx) {
      Tensor row = matrix.get(inx);
      for (int iny = 0; iny < row.length(); ++iny) {
        graphics.setColor(function.apply(row.Get(iny)));
        int tpx = pix + width * inx;
        int tpy = piy + fheight * iny;
        graphics.fillRect(tpx, tpy, width, fheight);
        graphics.setColor(color_text);
        String string = rounded.Get(inx, iny).toString();
        int sw = fontMetrics.stringWidth(string);
        graphics.drawString(string, tpx + width - sw, tpy + fheight - 1);
      }
    }
  }
}
