// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

public class ArrayPlotRender implements RenderInterface {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
  private static final Color FONT_COLOR = Color.BLACK;
  // new Color(32, 32, 32);

  public static ArrayPlotRender rescale(Tensor tensor, ColorDataGradient colorDataGradient, int magnify) {
    Rescale rescale = new Rescale(tensor);
    ScalarSummaryStatistics scalarSummaryStatistics = rescale.scalarSummaryStatistics();
    Clip clip = 0 < scalarSummaryStatistics.getCount() //
        ? Clips.interval(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax())
        : null;
    return new ArrayPlotRender(rescale.result(), clip, colorDataGradient, magnify);
  }

  public static ArrayPlotRender uniform(Tensor tensor, ColorDataGradient colorDataGradient, int magnify) {
    return new ArrayPlotRender(tensor, Clips.unit(), colorDataGradient, magnify);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Clip clip;
  private final int width;
  private final int height;
  private final BufferedImage legend;

  public ArrayPlotRender(Tensor tensor, Clip clip, ColorDataGradient colorDataGradient, int magnify) {
    bufferedImage = ImageFormat.of(tensor.map(colorDataGradient));
    this.clip = clip;
    width = bufferedImage.getWidth() * magnify;
    height = bufferedImage.getHeight() * magnify;
    legend = ImageFormat.of(Subdivide.decreasing(Clips.unit(), height - 1).map(Tensors::of).map(colorDataGradient));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, //
        0, //
        0, //
        width, //
        height, null);
    graphics.drawImage(legend, //
        width + 10, //
        0, //
        10, //
        height, null);
    if (Objects.nonNull(clip)) {
      graphics.setFont(FONT);
      graphics.setColor(FONT_COLOR);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String smax = "" + clip.max().map(Round._3);
      int wmax = fontMetrics.stringWidth(smax);
      String smin = "" + clip.min().map(Round._3);
      int wmin = fontMetrics.stringWidth(smin);
      int ofx = width + 22 + Math.max(wmin, wmax);
      RenderQuality.setQuality(graphics);
      graphics.drawString(smax, ofx - wmax, fontMetrics.getAscent());
      graphics.drawString(smin, ofx - wmin, height);
      RenderQuality.setDefault(graphics);
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

  public Dimension getDimension() {
    return new Dimension(width, height);
  }
}
