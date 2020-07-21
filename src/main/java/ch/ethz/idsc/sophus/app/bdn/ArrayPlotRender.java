// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class ArrayPlotRender implements RenderInterface {
  /** @param tensor
   * @param colorDataGradient
   * @param magnify
   * @return */
  public static ArrayPlotRender rescale(Tensor tensor, ColorDataGradient colorDataGradient, int magnify) {
    Rescale rescale = new Rescale(tensor);
    ScalarSummaryStatistics scalarSummaryStatistics = rescale.scalarSummaryStatistics();
    Clip clip = 0 < scalarSummaryStatistics.getCount() //
        ? Clips.interval(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax())
        : null;
    return new ArrayPlotRender(rescale.result(), clip, colorDataGradient, magnify);
  }

  /***************************************************/
  private final BufferedImage bufferedImage;
  private final int width;
  private final int height;
  private final BufferedImage legend;

  private ArrayPlotRender(Tensor tensor, Clip clip, ColorDataGradient colorDataGradient, int magnify) {
    bufferedImage = ImageFormat.of(tensor.map(colorDataGradient));
    width = bufferedImage.getWidth() * magnify;
    height = bufferedImage.getHeight() * magnify;
    legend = LegendImage.of(colorDataGradient, height, clip);
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
        null);
  }

  public int height() {
    return height;
  }

  public BufferedImage export() {
    BufferedImage bi = new BufferedImage( //
        width + 10 + legend.getWidth(), // magic constant corresponds to width of legend
        height, //
        BufferedImage.TYPE_INT_ARGB);
    render(null, bi.createGraphics());
    return bi;
  }

  public Dimension getDimension() {
    return new Dimension(width, height);
  }
}
