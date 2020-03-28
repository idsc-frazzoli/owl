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
import ch.ethz.idsc.sophus.hs.BarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
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
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
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
  // ---
  private final GeodesicDisplay geodesicDisplay;
  private final Tensor weights;
  private final Tensor sequence;
  private final Tensor origin;
  private final Tensor shape;
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  public LeverRender(GeodesicDisplay geodesicDisplay, Tensor sequence, Tensor origin, GeometricLayer geometricLayer, Graphics2D graphics) {
    this.geodesicDisplay = geodesicDisplay;
    this.sequence = sequence;
    this.origin = origin;
    shape = geodesicDisplay.shape();
    if (geodesicDisplay.dimensions() < sequence.length()) {
      BarycentricCoordinate barycentricCoordinate = HsBarycentricCoordinate.smooth(geodesicDisplay.flattenLogManifold());
      weights = barycentricCoordinate.weights(sequence, origin);
    } else
      weights = Array.zeros(sequence.length());
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
      graphics.setColor(Color.BLACK);
      Rectangle rectangle = path2d.getBounds();
      Scalar rounded = Round._2.apply(getWeights().Get(index));
      String string = " " + rounded.toString();
      graphics.drawString(string, //
          rectangle.x + rectangle.width, //
          rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2);
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderSequence() {
    POINTS_RENDER_0.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
  }

  public void renderOrigin() {
    ORIGIN_RENDER_0.show(geodesicDisplay::matrixLift, shape.multiply(RealScalar.of(1.2)), Tensors.of(origin)) //
        .render(geometricLayer, graphics);
  }

  public Tensor getWeights() {
    return weights;
  }

  public Tensor getSequence() {
    return sequence;
  }

  public Tensor getOrigin() {
    return origin;
  }
}