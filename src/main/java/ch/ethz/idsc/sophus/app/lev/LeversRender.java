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
import ch.ethz.idsc.sophus.math.Exponential;
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
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.CyclicColorDataIndexed;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

public class LeversRender {
  public static final Font FONT_LABELS = new Font(Font.DIALOG, Font.ITALIC, 18);
  public static final Font FONT_MATRIX = new Font(Font.DIALOG, Font.BOLD, 14);
  private static final Tensor RGBA = Tensors.fromString("{{0, 0, 0, 16}, {0, 0, 0, 255}}");
  private static final ColorDataGradient COLOR_DATA_GRADIENT = LinearColorDataGradient.of(RGBA);
  private static final Scalar NEUTRAL_DEFAULT = RealScalar.of(0.33);
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 128, 64, 128), new Color(64, 128, 64, 255));
  private static final Stroke STROKE_GEODESIC = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  static final Color COLOR_TEXT_DRAW = new Color(128 - 32, 128 - 32, 128 - 32);
  private static final Color COLOR_TEXT_FILL = new Color(255 - 32, 255 - 32, 255 - 32, 128);
  private static final ColorDataIndexed CONSTANT = //
      CyclicColorDataIndexed.of(Tensors.of(ColorFormat.toVector(COLOR_TEXT_DRAW)));
  // ---
  public static boolean DEBUG_FLAG = false;

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

  public void renderIndexP() {
    renderIndexP("p");
  }

  public void renderIndexX() {
    renderIndexX("x");
  }

  public void renderIndexP(String plabel) {
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
        pix -= fontMetrics.stringWidth(plabel);
        graphics.drawString(plabel, pix, piy - fheight / 3);
      }
      // ---
      geometricLayer.popMatrix();
      ++index;
    }
  }

  public void renderIndexX(String xlabel) {
    Tensor shape = geodesicDisplay.shape();
    graphics.setFont(FONT_LABELS);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    graphics.setColor(Color.BLACK);
    if (Objects.nonNull(origin)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
      Rectangle rectangle = geometricLayer.toPath2D(shape, true).getBounds();
      int pix = rectangle.x;
      int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
      {
        String string = xlabel + " ";
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
    Tensor rescale = !isSufficient() || weights.equals(Array.zeros(sequence.length())) //
        ? weights.map(s -> NEUTRAL_DEFAULT)
        : Rescale.of(weights);
    graphics.setStroke(STROKE_GEODESIC);
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

  public void renderLeverLength() {
    TensorMetric tensorMetric = geodesicDisplay.parametricDistance();
    if (Objects.nonNull(tensorMetric)) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
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
  }
  // public void renderWeights() {
  // renderWeights(weights);
  // }

  public void renderWeights(Tensor weights) {
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
  private static final Stroke STROKE_TANGENT = new BasicStroke(1.5f);
  private static final Color COLOR_TANGENT = new Color(0, 0, 255, 192);
  private static final Color COLOR_PLANE = new Color(192, 192, 192, 64);
  private static final Tensor CIRCLE = CirclePoints.of(41).unmodifiable();

  public void renderTangentsPtoX(boolean tangentPlane) {
    HsExponential hsExponential = geodesicDisplay.hsExponential();
    graphics.setStroke(STROKE_TANGENT);
    for (Tensor p : sequence) { // draw tangent at p
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
      Tensor v = hsExponential.exponential(p).log(origin);
      graphics.setColor(COLOR_TANGENT);
      TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(p);
      if (Objects.nonNull(tangentProjection))
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
      // ---
      if (tangentPlane) {
        if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE)) {
          Scalar max = Hypot.ofVector(v);
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
    graphics.setStroke(STROKE_TANGENT);
    graphics.setColor(COLOR_TANGENT);
    TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(origin);
    if (Objects.nonNull(tangentProjection))
      for (Tensor v : vs)
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
    // ---
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
  private void renderMahalanobisMatrix(Tensor p, Tensor form, ColorDataGradient colorDataGradient) {
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.arcTan(graphics, CONSTANT, colorDataGradient);
    Tensor alt = Tensors.of(Eigensystem.ofSymmetric(form).values());
    renderMatrix(p, matrixRender, Transpose.of(alt));
  }

  public static boolean form_shadow = false;

  private void renderMahalanobisForm(Tensor p, Tensor form) {
    Tensor vs = null;
    if (geodesicDisplay.equals(R2GeodesicDisplay.INSTANCE))
      vs = CIRCLE;
    else //
    if (geodesicDisplay.equals(S2GeodesicDisplay.INSTANCE))
      vs = CIRCLE.dot(S2GeodesicDisplay.tangentSpace(p));
    // ---
    if (Objects.nonNull(vs)) {
      vs = Tensor.of(vs.stream().map(form::dot)); //
      if (form_shadow) {
        Exponential exponential = geodesicDisplay.hsExponential().exponential(p);
        Tensor ms = Tensor.of(vs.stream().map(exponential::exp).map(geodesicDisplay::toPoint));
        Path2D path2d = geometricLayer.toPath2D(ms, true);
        graphics.setStroke(new BasicStroke());
        graphics.setColor(new Color(0, 0, 0, 16));
        graphics.fill(path2d);
        graphics.setColor(new Color(0, 0, 0, 32));
        graphics.draw(path2d);
      }
      // ---
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
      Tensor ellipse = Tensor.of(vs.stream().map(geodesicDisplay.tangentProjection(p))); // from 3d to 2d
      Path2D path2d = geometricLayer.toPath2D(ellipse, true);
      graphics.setColor(new Color(64, 192, 64, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(64, 192, 64, 192));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public void renderMahalanobisFormX(boolean matrix, ColorDataGradient colorDataGradient) {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    if (Tensors.nonEmpty(sequence)) {
      Tensor form = mahalanobis.new Form(sequence, origin).sigma_inverse();
      if (matrix)
        renderMahalanobisMatrix(origin, form, colorDataGradient);
      renderMahalanobisForm(origin, form);
    }
  }

  public void renderMahalanobisFormsP(boolean matrix, ColorDataGradient colorDataGradient) {
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    Mahalanobis mahalanobis = new Mahalanobis(vectorLogManifold);
    for (Tensor p : sequence) {
      Tensor form = mahalanobis.new Form(sequence, p).sigma_inverse();
      if (matrix)
        renderMahalanobisMatrix(p, form, colorDataGradient);
      renderMahalanobisForm(p, form);
    }
  }

  /***************************************************/
  public void renderLeverages() {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    renderWeights(Diagonal.of(new HsProjection(vectorLogManifold).new Matrix(sequence, origin).influence()));
  }

  public void renderInfluenceX(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor influence = hsProjection.new Matrix(sequence, origin).influence();
    // ---
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.absoluteOne(graphics, CONSTANT, colorDataGradient);
    matrixRender.setScalarMapper(Round._2);
    renderMatrix(origin, matrixRender, influence);
  }

  public void renderInfluenceP(ColorDataGradient colorDataGradient) {
    if (!isSufficient())
      return;
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    HsProjection hsProjection = new HsProjection(vectorLogManifold);
    Tensor projections = Tensor.of(sequence.stream().map(point -> hsProjection.new Matrix(sequence, point).influence()));
    // ---
    graphics.setFont(FONT_MATRIX);
    int index = 0;
    MatrixRender matrixRender = MatrixRender.absoluteOne(graphics, CONSTANT, colorDataGradient);
    matrixRender.setScalarMapper(Round._2);
    for (Tensor p : sequence) {
      renderMatrix(p, matrixRender, projections.get(index));
      ++index;
    }
  }

  public void renderMatrix(int index, Tensor matrix, ColorDataIndexed colorDataIndexed) {
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.of(graphics, colorDataIndexed, new Color(255, 255, 255, 32));
    matrixRender.setScalarMapper(Round._3);
    renderMatrix(sequence.get(index), matrixRender, matrix);
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
    matrixRender.renderMatrix(matrix, pix, piy);
    geometricLayer.popMatrix();
  }
}