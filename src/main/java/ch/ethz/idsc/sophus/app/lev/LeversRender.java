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
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.HsProjection;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.sn.SnExponential;
import ch.ethz.idsc.sophus.krg.Mahalanobis;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RationalScalar;
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
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

public class LeversRender {
  public static final Font FONT_LABELS = new Font(Font.DIALOG, Font.ITALIC, 18);
  public static final Font FONT_MATRIX = new Font(Font.DIALOG, Font.BOLD, 14);
  private static final Tensor RGBA = Tensors.fromString("{{0, 0, 0, 16}, {0, 0, 0, 255}}");
  private static final ColorDataGradient COLOR_DATA_GRADIENT = LinearColorDataGradient.of(RGBA);
  private static final Scalar NEUTRAL_DEFAULT = RealScalar.of(0.5);
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 128, 64, 128), new Color(64, 128, 64, 255));
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public static LeversRender of( //
      GeodesicDisplay geodesicDisplay, TensorUnaryOperator tensorUnaryOperator, //
      Tensor sequence, Tensor origin, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor weights = Objects.nonNull(tensorUnaryOperator) //
        && geodesicDisplay.dimensions() < sequence.length() //
        && Objects.nonNull(origin) //
            ? tensorUnaryOperator.apply(origin)
            : Array.zeros(sequence.length());
    return new LeversRender( //
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
  public LeversRender( //
      GeodesicDisplay geodesicDisplay, //
      Tensor sequence, Tensor origin, Tensor weights, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    this.geodesicDisplay = geodesicDisplay;
    this.sequence = sequence;
    this.origin = origin;
    shape = geodesicDisplay.shape();
    this.weights = weights;
    this.geometricLayer = geometricLayer;
    this.graphics = graphics;
  }

  public void renderIndex() {
    int index = 0;
    Tensor shape = geodesicDisplay.shape();
    graphics.setFont(FONT_LABELS);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    graphics.setColor(Color.BLACK);
    for (Tensor q : sequence) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(q));
      Rectangle rectangle = geometricLayer.toPath2D(shape, true).getBounds();
      int pix = rectangle.x;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      {
        String string = (index + 1) + " ";
        pix -= fontMetrics.stringWidth(string);
        graphics.drawString(string, pix, piy);
      }
      {
        String string = "p";
        pix -= fontMetrics.stringWidth(string);
        graphics.drawString(string, pix, piy - fheight / 3);
      }
      // ---
      geometricLayer.popMatrix();
      ++index;
    }
    if (Objects.nonNull(origin)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
      Rectangle rectangle = geometricLayer.toPath2D(shape, true).getBounds();
      int pix = rectangle.x;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      {
        String string = "x ";
        pix -= fontMetrics.stringWidth(string);
        graphics.drawString(string, pix, piy - fheight / 3);
      }
      // ---
      geometricLayer.popMatrix();
    }
  }

  private boolean isSufficient() {
    return geodesicDisplay.dimensions() < sequence.length();
  }

  public void renderLevers() {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    int index = 0;
    Tensor rescale = !isSufficient() || getWeights().equals(Array.zeros(sequence.length())) //
        ? getWeights().map(s -> NEUTRAL_DEFAULT)
        : Rescale.of(getWeights());
    graphics.setStroke(STROKE);
    for (Tensor q : sequence) {
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
      Tensor domain = Subdivide.of(0, 1, 21);
      Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
      Tensor rgba = COLOR_DATA_GRADIENT.apply(Clips.unit().apply(rescale.Get(index)));
      graphics.setColor(ColorFormat.toColor(rgba));
      graphics.draw(geometricLayer.toPath2D(ms));
      ++index;
    }
    graphics.setStroke(new BasicStroke());
  }

  private static final Color COLOR_TEXT_DRAW = Color.DARK_GRAY;
  private static final Color COLOR_TEXT_FILL = new Color(255 - 32, 255 - 32, 255 - 32, 128);
  private static final Tensor RGBA_TEXT_FILL = Tensors.vector(0, 0, 0, 16);

  public void renderLeverLength() {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    TensorMetric tensorMetric = geodesicDisplay.parametricDistance();
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    for (Tensor q : sequence) {
      Scalar d = tensorMetric.distance(origin, q);
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
      Tensor ms = geodesicDisplay.toPoint(scalarTensorFunction.apply(RationalScalar.HALF));
      Point2D point2d = geometricLayer.toPoint2D(ms);
      String string = "" + d.map(Round._3);
      int width = fontMetrics.stringWidth(string);
      int pix = (int) point2d.getX() - width / 2;
      int piy = (int) point2d.getY() + fheight / 2;
      graphics.setColor(COLOR_TEXT_FILL);
      graphics.fillRect(pix, piy - fheight, width, fheight);
      graphics.setColor(COLOR_TEXT_DRAW);
      graphics.drawString(string, pix, piy);
    }
  }

  public void renderWeights() {
    graphics.setFont(FONT_MATRIX);
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

  public void renderTangentsPtoX() {
    if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE))
      for (Tensor p : sequence) { // draw tangent at p
        Tensor vs = new SnExponential(p).log(origin);
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.setColor(new Color(0, 0, 255, 192));
        Tensor ts = S2GeodesicDisplay.tangentSpace(p);
        graphics.draw(geometricLayer.toLine2D(ts.dot(vs)));
        geometricLayer.popMatrix();
      }
  }

  public void renderTangentsXtoP(boolean tangentPlane) {
    if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE)) {
      Tensor vs = Tensor.of(sequence.stream().map(new SnExponential(origin)::log));
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
      graphics.setStroke(new BasicStroke(1.5f));
      graphics.setColor(new Color(0, 0, 255, 192));
      Tensor ts = S2GeodesicDisplay.tangentSpace(origin);
      Scalar max = vs.stream().map(Norm._2::ofVector).reduce(Max::of).orElse(RealScalar.ONE);
      for (Tensor v : vs) // render tangents in tangent space
        graphics.draw(geometricLayer.toLine2D(ts.dot(v)));
      if (tangentPlane) {
        graphics.setColor(new Color(192, 192, 192, 64));
        graphics.fill(geometricLayer.toPath2D(CirclePoints.of(41).multiply(max), true));
      }
      geometricLayer.popMatrix();
    }
  }

  public void renderMahFormsP() {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    Tensor forms = Tensor.of(sequence.stream().map(point -> mahalanobis.new Form(sequence, point).sigma_inverse()));
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int index = 0;
    for (Tensor q : sequence) {
      Tensor form = forms.get(index);
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderMatrix(s -> RGBA_TEXT_FILL, form, pix, piy);
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderMahFormX() {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    Tensor form = mahalanobis.new Form(sequence, origin).sigma_inverse();
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    {
      Tensor matrix = geodesicDisplay.matrixLift(origin);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderMatrix(s -> RGBA_TEXT_FILL, form, pix, piy);
      geometricLayer.popMatrix();
    }
  }

  public void renderGrassmannians(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor grassmann = Tensor.of(sequence.stream().map(point -> hsProjection.projection(sequence, point)));
    // ---
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int index = 0;
    for (Tensor q : sequence) {
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderMatrix(colorDataGradient, grassmann.get(index), pix, piy);
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderGrassmannianX(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor grassmann = hsProjection.projection(sequence, origin);
    // ---
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    Tensor q = origin;
    {
      Tensor matrix = geodesicDisplay.matrixLift(q);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      int pix = rectangle.x + rectangle.width;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      renderMatrix(colorDataGradient, grassmann, pix, piy);
      geometricLayer.popMatrix();
    }
  }

  /** render control points */
  public void renderSequence() {
    POINTS_RENDER_0.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
  }

  /** render point of coordinate evaluation */
  public void renderOrigin() {
    ORIGIN_RENDER_0.show( //
        geodesicDisplay::matrixLift, //
        shape.multiply(RealScalar.of(1.0)), //
        Tensors.of(origin)) //
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

  /***************************************************/
  private void renderMatrix(ScalarTensorFunction colorDataGradient, Tensor matrix, int pix, int piy) {
    Tensor rounded = matrix.map(Round._2);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    int max = rounded.flatten(-1) //
        .map(s -> s.toString()).mapToInt(s -> fontMetrics.stringWidth(s)).max().getAsInt();
    int width = max + 3;
    for (int inx = 0; inx < rounded.length(); ++inx) {
      Tensor row = matrix.get(inx);
      for (int iny = 0; iny < row.length(); ++iny) {
        Color color = ColorFormat.toColor(colorDataGradient.apply(Clips.absoluteOne().rescale(row.Get(iny))));
        graphics.setColor(color);
        int tpx = pix + width * inx;
        int tpy = piy + fheight * iny;
        graphics.fillRect(tpx, tpy, width, fheight);
        graphics.setColor(COLOR_TEXT_DRAW);
        String string = rounded.Get(inx, iny).toString();
        int sw = fontMetrics.stringWidth(string);
        graphics.drawString(string, tpx + width - sw, tpy + fheight - 1);
      }
    }
  }
}