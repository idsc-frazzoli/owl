// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.HsProjection;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

public class LeverRender {
  public static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 14);
  private static final Tensor RGBA = Tensors.fromString("{{0, 0, 0, 16}, {0, 0, 0, 255}}");
  private static final ColorDataGradient COLOR_DATA_GRADIENT = LinearColorDataGradient.of(RGBA);
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 128, 64, 128), new Color(64, 128, 64, 255));
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public static LeverRender of( //
      GeodesicDisplay geodesicDisplay, TensorUnaryOperator weightingInterface, //
      Tensor sequence, Tensor origin, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor weights = geodesicDisplay.dimensions() < sequence.length() //
        ? weightingInterface.apply(origin)
        : Array.zeros(sequence.length());
    return new LeverRender( //
        geodesicDisplay, sequence, origin, //
        weights, geometricLayer, graphics);
  }

  /***************************************************/
  private final GeodesicDisplay geodesicDisplay;
  private final Tensor weights;
  private final Tensor sequence;
  private final Tensor origin;
  private final Tensor shape;
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  /** @param geodesicDisplay
   * @param sequence
   * @param origin may be null
   * @param weights
   * @param geometricLayer
   * @param graphics */
  public LeverRender(GeodesicDisplay geodesicDisplay, Tensor sequence, Tensor origin, Tensor weights, GeometricLayer geometricLayer, Graphics2D graphics) {
    this.geodesicDisplay = geodesicDisplay;
    this.sequence = sequence;
    this.origin = origin;
    shape = geodesicDisplay.shape();
    this.weights = weights;
    this.geometricLayer = geometricLayer;
    this.graphics = graphics;
  }

  public void renderLevers() {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    if (geodesicDisplay.dimensions() < sequence.length()) {
      int index = 0;
      Tensor rescale = Rescale.of(getWeights());
      for (Tensor q : sequence) {
        ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
        graphics.setStroke(STROKE);
        Tensor domain = Subdivide.of(0, 1, 21);
        Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
        Tensor rgba = COLOR_DATA_GRADIENT.apply(Clips.unit().apply(rescale.Get(index)));
        graphics.setColor(ColorFormat.toColor(rgba));
        graphics.draw(geometricLayer.toPath2D(ms));
        ++index;
      }
      graphics.setStroke(new BasicStroke());
    }
  }

  public void renderWeights() {
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int index = 0;
    for (Tensor q : sequence) {
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      Scalar rounded = Round._2.apply(weights.Get(index));
      String string = " " + rounded.toString();
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      graphics.setColor(Color.WHITE);
      graphics.drawString(string, pix - 1, piy - 1);
      graphics.drawString(string, pix + 1, piy - 1);
      graphics.drawString(string, pix - 1, piy + 1);
      graphics.drawString(string, pix + 1, piy + 1);
      graphics.setColor(Color.BLACK);
      graphics.drawString(string, pix, piy);
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderGrassmannians() {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor grassmann = Tensor.of(sequence.stream().map(point -> hsProjection.projection(sequence, point)));
    // ---
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int index = 0;
    for (Tensor q : sequence) {
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      String lines = Pretty.of(grassmann.get(index).map(Round._2));
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderString(lines, pix, piy);
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderGrassmannianOrigin() {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor grassmann = hsProjection.projection(sequence, origin);
    // ---
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    Tensor q = origin;
    {
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      String lines = Pretty.of(grassmann.map(Round._2));
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderString(lines, pix, piy);
      geometricLayer.popMatrix();
    }
  }

  private void renderString(String lines, int pix, int piy) {
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    String[] splits = lines.split("\\n");
    for (int count = 0; count < splits.length; ++count) {
      String string = splits[count];
      graphics.setColor(Color.WHITE);
      graphics.drawString(string, pix - 1, piy - 1);
      graphics.drawString(string, pix + 1, piy - 1);
      graphics.drawString(string, pix - 1, piy + 1);
      graphics.drawString(string, pix + 1, piy + 1);
      graphics.setColor(Color.BLACK);
      graphics.drawString(string, pix, piy);
      piy += fheight;
    }
  }

  /** render control points */
  public void renderSequence() {
    POINTS_RENDER_0.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
  }

  /** render point of coordinate evaluation */
  public void renderOrigin() {
    ORIGIN_RENDER_0.show(geodesicDisplay::matrixLift, shape.multiply(RealScalar.of(1.2)), Tensors.of(origin)) //
        .render(geometricLayer, graphics);
  }

  public Tensor getWeights() {
    return weights.unmodifiable();
  }

  public Tensor getSequence() {
    return sequence.unmodifiable();
  }

  public Tensor getOrigin() {
    return origin.unmodifiable();
  }
}