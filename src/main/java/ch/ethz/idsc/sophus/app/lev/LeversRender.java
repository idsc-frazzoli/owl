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
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsProjection;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
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
import ch.ethz.idsc.tensor.alg.PadRight;
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
    for (Tensor p : sequence) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
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
    for (Tensor p : sequence) {
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, p);
      Tensor domain = Subdivide.of(0, 1, 21);
      Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
      Tensor rgba = COLOR_DATA_GRADIENT.apply(Clips.unit().apply(rescale.Get(index)));
      graphics.setColor(ColorFormat.toColor(rgba));
      graphics.draw(geometricLayer.toPath2D(ms));
      ++index;
    }
    graphics.setStroke(new BasicStroke());
  }

  static final Color COLOR_TEXT_DRAW = Color.GRAY;
  private static final Color COLOR_TEXT_FILL = new Color(255 - 32, 255 - 32, 255 - 32, 128);

  public void renderLeverLength() {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    TensorMetric tensorMetric = geodesicDisplay.parametricDistance();
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    for (Tensor p : sequence) {
      Scalar d = tensorMetric.distance(origin, p);
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, p);
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
    for (Tensor p : sequence) {
      Tensor matrix = geodesicDisplay.matrixLift(p);
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

  /***************************************************/
  private static final Color COLOR_TANGENT = new Color(0, 0, 255, 192);
  private static final Color COLOR_PLANE = new Color(192, 192, 192, 64);
  private static final Tensor CIRCLE = CirclePoints.of(41);

  public void renderTangentsPtoX(boolean tangentPlane) {
    HsExponential hsExponential = geodesicDisplay.hsExponential();
    graphics.setStroke(new BasicStroke(1.5f));
    for (Tensor p : sequence) { // draw tangent at p
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
      Tensor v = hsExponential.exponential(p).log(origin);
      graphics.setColor(COLOR_TANGENT);
      TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(p);
      if (Objects.nonNull(tangentProjection)) {
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
      }
      if (tangentPlane) {
        if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE)) {
          Scalar max = Norm._2.ofVector(v);
          graphics.setColor(COLOR_PLANE);
          graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(max), true));
        }
      }
      geometricLayer.popMatrix();
    }
    graphics.setStroke(new BasicStroke());
  }

  public void renderTangentsXtoP(boolean tangentPlane) {
    HsExponential hsExponential = geodesicDisplay.hsExponential();
    Tensor vs = Tensor.of(sequence.stream().map(hsExponential.exponential(origin)::log));
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
    graphics.setStroke(new BasicStroke(1.5f));
    graphics.setColor(COLOR_TANGENT);
    TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(origin);
    if (Objects.nonNull(tangentProjection)) {
      for (Tensor v : vs)
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
    }
    if (tangentPlane) {
      if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE)) {
        Scalar max = vs.stream().map(Norm._2::ofVector).reduce(Max::of).orElse(RealScalar.ONE);
        graphics.setColor(COLOR_PLANE);
        graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(max), true));
      }
    }
    geometricLayer.popMatrix();
  }

  /***************************************************/
  private static final TensorUnaryOperator PADDING = PadRight.zeros(3, 3);

  public void renderMahFormsP(ColorDataGradient colorDataGradient) {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    Tensor forms = Tensor.of(sequence.stream().map(point -> mahalanobis.new Form(sequence, point).sigma_inverse()));
    { // show matrix
      int index = 0;
      graphics.setFont(FONT_MATRIX);
      MatrixRender matrixRender = MatrixRender.arcTan(graphics, COLOR_TEXT_DRAW, colorDataGradient);
      for (Tensor p : sequence) {
        renderMatrix(p, matrixRender, forms.get(index));
        ++index;
      }
    }
    if (geodesicDisplay.equals(R2GeodesicDisplay.INSTANCE)) {
      int index = 0;
      for (Tensor p : sequence) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p)); // translation
        Tensor matrix = PADDING.apply(forms.get(index));
        matrix.set(RealScalar.ONE, 2, 2);
        geometricLayer.pushMatrix(matrix);
        Path2D path2d = geometricLayer.toPath2D(CIRCLE, true);
        graphics.setColor(new Color(64, 192, 64, 64));
        graphics.fill(path2d);
        graphics.setColor(new Color(64, 192, 64, 192));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
        geometricLayer.popMatrix();
        ++index;
      }
    }
    // else //
    // if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE)) {
    // int index = 0;
    // for (Tensor q : sequence) {
    // Tensor basis = geodesicDisplay.matrixLift(q);
    // geometricLayer.pushMatrix(basis);
    // Tensor form = forms.get(index);
    // graphics.setColor(new Color(192, 64, 64, 64));
    // Tensor v1 = BasisTransform.of(form, 0, basis);
    // Tensor v2 = BasisTransform.of(form, 2, basis);
    // System.out.println("---");
    // System.out.println(Pretty.of(v1.map(Round._3)));
    // System.out.println(Pretty.of(v2.map(Round._3)));
    // geometricLayer.pushMatrix(form);
    // graphics.fill(geometricLayer.toPath2D(CirclePoints.of(41).multiply(RealScalar.of(0.2)), true));
    // geometricLayer.popMatrix();
    // geometricLayer.popMatrix();
    // ++index;
    // }
    // }
  }

  public void renderMahFormX(ColorDataGradient colorDataGradient) {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    Tensor form = mahalanobis.new Form(sequence, origin).sigma_inverse();
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.arcTan(graphics, COLOR_TEXT_DRAW, colorDataGradient);
    renderMatrix(origin, matrixRender, form);
    if (geodesicDisplay.equals(R2GeodesicDisplay.INSTANCE)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin)); // translation
      Tensor matrix = PADDING.apply(form);
      matrix.set(RealScalar.ONE, 2, 2);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(CIRCLE, true);
      graphics.setColor(new Color(64, 192, 64, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(64, 192, 64, 192));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
      geometricLayer.popMatrix();
    }
  }

  public void renderProjectionsP(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor projections = Tensor.of(sequence.stream().map(point -> hsProjection.projection(sequence, point)));
    // ---
    graphics.setFont(FONT_MATRIX);
    int index = 0;
    MatrixRender matrixRender = MatrixRender.absoluteOne(graphics, COLOR_TEXT_DRAW, colorDataGradient);
    for (Tensor p : sequence) {
      renderMatrix(p, matrixRender, projections.get(index));
      ++index;
    }
  }

  /***************************************************/
  public void renderProjectionX(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor projection = hsProjection.projection(sequence, origin);
    // ---
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.absoluteOne(graphics, COLOR_TEXT_DRAW, colorDataGradient);
    renderMatrix(origin, matrixRender, projection);
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

  /***************************************************/
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
  private void renderMatrix(Tensor q, MatrixRender matrixRender, Tensor matrix) {
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(q));
    Path2D path2d = geometricLayer.toPath2D(shape, true);
    Rectangle rectangle = path2d.getBounds();
    int pix = rectangle.x + rectangle.width;
    int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
    matrixRender.renderMatrix(matrix, Round._2, pix, piy);
    geometricLayer.popMatrix();
  }
}