// code by jph
package ch.ethz.idsc.owl.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.Clip;

public class ListPlot {
  private static final int MARGIN_TOP = 30;
  private static final int MARGIN_BOT = 12;
  private static final int MARGIN_EAST = 30;
  private static final int MARGIN_WEST = 20;
  private static final int DOT = 5;

  /** @param points {{x1, y1}, ..., {xn, yn}}
   * @param dimension
   * @param file
   * @throws IOException */
  public static void of(Tensor points, Dimension dimension, File file) throws IOException {
    SeriesCollection seriesCollection = new SeriesCollection();
    seriesCollection.add(points);
    of(seriesCollection, dimension, file);
  }

  public static void of(SeriesCollection seriesCollection, Dimension dimension, File file) throws IOException {
    BufferedImage bufferedImage = //
        new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    Dimension view = new Dimension(dimension.width - MARGIN_EAST - MARGIN_WEST, dimension.height - MARGIN_TOP - MARGIN_BOT);
    Tensor plotRange = seriesCollection.getPlotRange();
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, dimension.width, dimension.height);
    graphics.setColor(Color.DARK_GRAY);
    // ---
    ClipExpand ce_rx = new ClipExpand(Clip.function(plotRange.Get(0, 0), plotRange.Get(0, 1)), 10);
    ClipExpand ce_ry = new ClipExpand(Clip.function(plotRange.Get(1, 0), plotRange.Get(1, 1)), 10);
    // ---
    Clip rx = ce_rx.clip;
    Clip ry = ce_ry.clip;
    // ---
    // TODO division by zero
    Tensor point2res = DiagonalMatrix.of(rx.width().reciprocal(), ry.width().reciprocal(), RealScalar.ONE);
    point2res.set(rx.min().negate().divide(rx.width()), 0, 2);
    point2res.set(ry.min().negate().divide(ry.width()), 1, 2);
    Scalar fx = RealScalar.of(view.width);
    Scalar fy = RealScalar.of(view.height);
    Tensor model2pixel = DiagonalMatrix.of(fx, fy.negate(), RealScalar.ONE);
    model2pixel.set(RealScalar.of(MARGIN_EAST), 0, 2);
    model2pixel.set(RealScalar.of(view.height + MARGIN_TOP), 1, 2);
    // System.out.println(Pretty.of(model2pixel));
    GeometricLayer geometricLayer = new GeometricLayer(model2pixel, Tensors.vector(1, 2, 3));
    geometricLayer.pushMatrix(point2res);
    // ---
    graphics.drawRect(MARGIN_EAST, MARGIN_TOP, 0, view.height);
    int y_xaxis;
    {
      Point2D ppx = geometricLayer.toPoint2D(Tensors.of(RealScalar.ZERO, RealScalar.ZERO));
      y_xaxis = ry.isInside(RealScalar.ZERO) ? (int) ppx.getY() : view.height + MARGIN_TOP;
    }
    graphics.drawRect(MARGIN_EAST, y_xaxis, view.width, 0); // draw x-axis
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    {
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
      FontMetrics fontMetrics = graphics.getFontMetrics();
      for (Tensor _px : ce_rx.linspace) {
        Scalar px = _px.Get();
        Point2D ppx = geometricLayer.toPoint2D(Tensors.of(px, RealScalar.ZERO));
        int pix = (int) ppx.getX();
        graphics.drawRect(pix, y_xaxis - DOT - 1, 0, DOT);
        String string = px.toString();
        int width = fontMetrics.stringWidth(string);
        int ofs_x = pix - width / 2;
        ofs_x = Math.max(MARGIN_EAST, ofs_x);
        ofs_x = Math.min(ofs_x, MARGIN_EAST + view.width - width + 1);
        graphics.drawString(string, ofs_x, y_xaxis + 10);
      }
      for (Tensor _py : ce_ry.linspace) {
        Scalar py = _py.Get();
        Point2D ppy = geometricLayer.toPoint2D(Tensors.of(RealScalar.ZERO, py));
        int piy = (int) ppy.getY();
        graphics.drawRect(MARGIN_EAST + 1, piy, DOT, 0);
        String string = py.toString();
        int width = fontMetrics.stringWidth(string);
        graphics.drawString(string, MARGIN_EAST - width - 2, piy + 4);
      }
    }
    // ---
    for (SeriesContainer seriesContainer : seriesCollection) {
      graphics.setColor(seriesContainer.color);
      if (seriesContainer.isJoined()) {
        graphics.setStroke(new BasicStroke(1.6f));
        graphics.draw(geometricLayer.toPath2D(seriesContainer.points()));
      } else {
        for (Tensor x : seriesContainer.points()) {
          Point2D pixel = geometricLayer.toPoint2D(x);
          graphics.fill(new Ellipse2D.Double(pixel.getX() - 1, pixel.getY() - 1, 3.5, 3.5));
        }
      }
    }
    graphics.setStroke(new BasicStroke(1));
    // ---
    graphics.setColor(Color.DARK_GRAY);
    graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    graphics.drawString(seriesCollection.getTitle(), 0, 12);
    // ---
    {
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
      FontMetrics fontMetrics = graphics.getFontMetrics();
      OptionalInt optionalInt = seriesCollection.stream() //
          .map(SeriesContainer::getName) //
          .mapToInt(fontMetrics::stringWidth) //
          .reduce(Math::max);
      if (optionalInt.isPresent()) {
        int count = (int) seriesCollection.stream() //
            .map(SeriesContainer::getName) //
            .filter(string -> !string.isEmpty()) //
            .count();
        int width_max = optionalInt.getAsInt();
        graphics.setColor(new Color(255, 255, 255, 192));
        // graphics.setColor(Color.BLACK);
        int ofs = dimension.width - width_max;
        graphics.fillRect(ofs, 0, width_max, count * 12);
        graphics.setColor(Color.DARK_GRAY);
        int piy = 11;
        for (SeriesContainer seriesContainer : seriesCollection) {
          String name = seriesContainer.getName();
          graphics.setColor(seriesContainer.color);
          graphics.drawString(name, ofs, piy);
          piy += 12;
        }
      }
    }
    // graphics.fillRect(x, y, width, height);
    ImageIO.write(bufferedImage, "png", file); // PNG
  }
}
