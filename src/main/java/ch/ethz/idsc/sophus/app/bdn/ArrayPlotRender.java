// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class ArrayPlotRender implements RenderInterface {
  public static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 14);
  // ---
  private final BufferedImage bufferedImage;
  private final ScalarSummaryStatistics scalarSummaryStatistics;
  private final int pix;
  private final int piy;
  private final int width;
  private final int height;
  private final BufferedImage legend;

  public ArrayPlotRender(Tensor tensor, ColorDataGradient colorDataGradient, int pix, int piy, int magnify) {
    Rescale rescale = new Rescale(tensor);
    bufferedImage = ImageFormat.of(rescale.result().map(colorDataGradient));
    scalarSummaryStatistics = rescale.scalarSummaryStatistics();
    this.pix = pix;
    this.piy = piy;
    width = bufferedImage.getWidth() * magnify;
    height = bufferedImage.getHeight() * magnify;
    legend = ImageFormat.of(Subdivide.decreasing(Clips.unit(), height - 1).map(Tensors::of).map(colorDataGradient));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, //
        pix, //
        piy, //
        width, //
        height, null);
    graphics.drawImage(legend, //
        pix + width + 10, //
        piy, //
        10, //
        height, null);
    if (0 < scalarSummaryStatistics.getCount()) {
      graphics.setColor(Color.BLACK);
      graphics.setFont(FONT);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String smax = "" + scalarSummaryStatistics.getMax().map(Round._3);
      int wmax = fontMetrics.stringWidth(smax);
      String smin = "" + scalarSummaryStatistics.getMin().map(Round._3);
      int wmin = fontMetrics.stringWidth(smin);
      int ofx = pix + width + 22 + Math.max(wmin, wmax);
      graphics.drawString(smax, ofx - wmax, piy + fontMetrics.getAscent());
      graphics.drawString(smin, ofx - wmin, piy + height);
    }
  }

  public int height() {
    return height;
  }

  public BufferedImage export() {
    BufferedImage bi = new BufferedImage( //
        bufferedImage.getWidth() + 100, //
        bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    render(null, bi.createGraphics());
    return bi;
  }
}
