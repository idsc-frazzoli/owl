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
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gds.Se2AbstractDisplay;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.CyclicColorDataIndexed;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.InfluenceMatrix;
import ch.ethz.idsc.tensor.mat.Mahalanobis;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;

public class LeversRender {
  public static final Font FONT_LABELS = new Font(Font.DIALOG, Font.ITALIC, 18);
  public static final Font FONT_MATRIX = new Font(Font.DIALOG, Font.BOLD, 14);
  private static final Tensor RGBA = Tensors.fromString("{{0, 0, 0, 16}, {0, 0, 0, 255}}");
  private static final ColorDataGradient COLOR_DATA_GRADIENT = LinearColorDataGradient.of(RGBA);
  private static final Scalar NEUTRAL_DEFAULT = RealScalar.of(0.33);
  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  public static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 128, 64, 128), new Color(64, 128, 64, 255));
  private static final Stroke STROKE_GEODESIC = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  static final Color COLOR_TEXT_DRAW = new Color(128 - 32, 128 - 32, 128 - 32);
  private static final Color COLOR_TEXT_FILL = new Color(255 - 32, 255 - 32, 255 - 32, 128);
  private static final ColorDataIndexed CONSTANT = //
      CyclicColorDataIndexed.of(Tensors.of(ColorFormat.toVector(COLOR_TEXT_DRAW)));
  // ---
  public static boolean DEBUG_FLAG = false;

  /** @param manifoldDisplay non-null
   * @param sequence
   * @param origin
   * @param geometricLayer non-null
   * @param graphics non-null
   * @return */
  public static LeversRender of( //
      ManifoldDisplay manifoldDisplay, Tensor sequence, Tensor origin, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    return new LeversRender( //
        manifoldDisplay, //
        sequence, origin, //
        Objects.requireNonNull(geometricLayer), //
        Objects.requireNonNull(graphics));
  }

  /***************************************************/
  private final ManifoldDisplay geodesicDisplay;
  private final Tensor sequence;
  private final Tensor origin;
  private final Tensor shape;
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  private LeversRender( //
      ManifoldDisplay geodesicDisplay, Tensor sequence, Tensor origin, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    this.geodesicDisplay = geodesicDisplay;
    this.sequence = sequence;
    this.origin = origin;
    shape = geodesicDisplay.shape();
    this.geometricLayer = geometricLayer;
    this.graphics = graphics;
  }

  public void renderIndexP() {
    renderIndexP("p");
  }

  public void renderIndexX() {
    renderIndexX("x");
  }

  public int getSequenceLength() {
    return sequence.length();
  }

  private static final int MAX_INDEX_P = 20;

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
      if (MAX_INDEX_P <= index)
        break;
    }
  }

  public void renderIndexX(String xlabel) {
    if (Objects.isNull(origin))
      return;
    Tensor shape = geodesicDisplay.shape();
    graphics.setFont(FONT_LABELS);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    graphics.setColor(Color.BLACK);
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
    Rectangle rectangle = geometricLayer.toPath2D(shape, true).getBounds();
    int pix = rectangle.x;
    int piy = rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2;
    {
      String string = xlabel + " ";
      pix -= fontMetrics.stringWidth(string);
      graphics.drawString(string, pix, piy - fheight / 3);
    }
    geometricLayer.popMatrix();
  }

  public void renderLevers() {
    renderLeversRescaled(Tensors.vector(i -> NEUTRAL_DEFAULT, sequence.length()));
  }

  public void renderLevers(Tensor weights) {
    renderLeversRescaled(Rescale.of(weights));
  }

  private void renderLeversRescaled(Tensor rescale) {
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    int index = 0;
    graphics.setStroke(STROKE_GEODESIC);
    for (Tensor p : sequence) {
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, p);
      Tensor domain = Subdivide.of(0, 1, 21);
      Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
      Tensor rgba = COLOR_DATA_GRADIENT.apply(rescale.Get(index));
      graphics.setColor(ColorFormat.toColor(rgba));
      graphics.draw(geometricLayer.toPath2D(ms));
      ++index;
    }
    graphics.setStroke(new BasicStroke());
  }

  public void renderLeverLength() {
    TensorMetric tensorMetric = geodesicDisplay.parametricDistance();
    if (Objects.nonNull(tensorMetric)) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      graphics.setFont(FONT_MATRIX);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      int fheight = fontMetrics.getAscent();
      for (Tensor point : sequence) {
        Scalar d = tensorMetric.distance(origin, point);
        ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, point);
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

  /***************************************************/
  public void renderWeightsLength() {
    TensorMetric tensorMetric = geodesicDisplay.parametricDistance();
    if (Objects.nonNull(tensorMetric)) {
      Tensor weights = Tensor.of(sequence.stream().map(point -> tensorMetric.distance(origin, point)));
      renderWeights(weights);
    }
  }

  public void renderWeightsLeveragesSqrt() {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
      Tensor w1 = new Mahalanobis(matrix).leverages_sqrt();
      Tensor w2 = InfluenceMatrix.of(matrix).leverages_sqrt();
      Chop._05.requireClose(w1, w2);
      renderWeights(w1);
    }
  }

  public void renderWeightsGarden() {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      Tensor weights = Biinvariants.GARDEN.distances(vectorLogManifold, sequence).apply(origin);
      renderWeights(weights);
    }
  }

  public void renderWeights(Tensor weights) {
    graphics.setFont(FONT_MATRIX);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int fheight = fontMetrics.getAscent();
    AtomicInteger atomicInteger = new AtomicInteger();
    for (Tensor point : sequence) {
      Tensor matrix = geodesicDisplay.matrixLift(point);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Rectangle rectangle = path2d.getBounds();
      Scalar rounded = Round._2.apply(weights.Get(atomicInteger.getAndIncrement()));
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
    }
  }

  /***************************************************/
  private static final Stroke STROKE_TANGENT = new BasicStroke(1.5f);
  private static final Color COLOR_TANGENT = new Color(0, 0, 255, 192);
  private static final Color COLOR_POLYGON_DRAW = new Color(0, 255, 255, 192);
  private static final Color COLOR_POLYGON_FILL = new Color(0, 255, 255, 64);
  private static final Color COLOR_PLANE = new Color(0, 0, 0, 32);
  private static final Tensor CIRCLE = CirclePoints.of(41).unmodifiable();

  public void renderTangentsPtoX(boolean tangentPlane) {
    HsManifold hsManifold = geodesicDisplay.hsManifold();
    graphics.setStroke(STROKE_TANGENT);
    for (Tensor p : sequence) { // draw tangent at p
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
      Tensor v = hsManifold.exponential(p).log(origin);
      graphics.setColor(COLOR_TANGENT);
      TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(p);
      if (Objects.nonNull(tangentProjection))
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
      // ---
      if (tangentPlane) {
        if (geodesicDisplay.equals(S2Display.INSTANCE)) {
          Scalar max = Vector2Norm.of(v);
          graphics.setColor(COLOR_PLANE);
          graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(max), true));
        }
      }
      geometricLayer.popMatrix();
    }
    graphics.setStroke(new BasicStroke());
  }

  public void renderTangentsXtoP(boolean tangentPlane) {
    HsManifold hsManifold = geodesicDisplay.hsManifold();
    Tensor vs = Tensor.of(sequence.stream().map(hsManifold.exponential(origin)::log));
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
    graphics.setStroke(STROKE_TANGENT);
    graphics.setColor(COLOR_TANGENT);
    TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(origin);
    if (Objects.nonNull(tangentProjection))
      for (Tensor v : vs)
        graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
    // ---
    if (tangentPlane) {
      if (geodesicDisplay.equals(S2Display.INSTANCE)) {
        Scalar max = vs.stream().map(Vector2Norm::of).reduce(Max::of).orElse(RealScalar.ONE);
        graphics.setColor(COLOR_PLANE);
        graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(max), true));
      }
    }
    geometricLayer.popMatrix();
  }

  public void renderPolygonXtoP() {
    HsManifold hsManifold = geodesicDisplay.hsManifold();
    Tensor vs = Tensor.of(sequence.stream().map(hsManifold.exponential(origin)::log));
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
    graphics.setStroke(STROKE_TANGENT);
    TensorUnaryOperator tangentProjection = geodesicDisplay.tangentProjection(origin);
    if (Objects.nonNull(tangentProjection)) {
      Tensor poly = Tensor.of(vs.stream().map(tangentProjection));
      Path2D path2d = geometricLayer.toPath2D(poly, true);
      graphics.setColor(COLOR_POLYGON_FILL);
      graphics.fill(path2d);
      graphics.setColor(COLOR_POLYGON_DRAW);
      graphics.draw(path2d);
    }
    geometricLayer.popMatrix();
  }

  public void renderLbsS2() {
    if (geodesicDisplay instanceof S2Display) {
      Tensor poly = Tensors.empty();
      graphics.setStroke(STROKE_TANGENT);
      for (Tensor p : sequence) {
        Scalar factor = ((Scalar) origin.dot(p)).reciprocal();
        Tensor point = p.multiply(factor);
        poly.append(point);
        graphics.setColor(new Color(255, 0, 0, 64));
        graphics.draw(geometricLayer.toLine2D(Array.zeros(2), point));
        graphics.setColor(COLOR_TANGENT);
        graphics.draw(geometricLayer.toLine2D(origin, point));
      }
      {
        Path2D path2d = geometricLayer.toPath2D(poly, true);
        graphics.setColor(COLOR_POLYGON_FILL);
        graphics.fill(path2d);
        graphics.setColor(COLOR_POLYGON_DRAW);
        graphics.draw(path2d);
      }
    }
  }

  private static final Tensor DOMAIN = Drop.tail(Subdivide.of(0.0, 1.0, 10), 1);

  /** render points in sequence as polygon with geodesic edges */
  public void renderSurfaceP() {
    Tensor all = Tensors.empty();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor prev = sequence.get(Math.floorMod(index - 1, sequence.length()));
      Tensor next = sequence.get(index);
      DOMAIN.map(geodesicDisplay.geodesicInterface().curve(prev, next)).stream() //
          .map(geodesicDisplay::toPoint) //
          .forEach(all::append);
    }
    Path2D path2d = geometricLayer.toPath2D(all);
    graphics.setColor(new Color(192, 192, 128, 64));
    graphics.fill(path2d);
    graphics.setColor(new Color(192, 192, 128, 192));
    graphics.draw(path2d);
  }

  /***************************************************/
  private void renderMahalanobisMatrix(Tensor p, Mahalanobis mahalanobis, ColorDataGradient colorDataGradient) {
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.arcTan(graphics, CONSTANT, colorDataGradient);
    Tensor alt = Tensors.of(Eigensystem.ofSymmetric(mahalanobis.sigma_n()).values());
    renderMatrix(p, matrixRender, Transpose.of(alt.map(Round._4)));
  }

  public static boolean form_shadow = false;

  private void renderEllipse(Tensor p, Tensor sigma_inverse) {
    Tensor vs = null;
    if (geodesicDisplay.equals(R2Display.INSTANCE))
      vs = CIRCLE;
    else //
    if (geodesicDisplay.equals(S2Display.INSTANCE))
      vs = CIRCLE; // .dot(TSnProjection.of(p));
    else //
    if (geodesicDisplay instanceof Se2AbstractDisplay) {
      vs = Tensor.of(CIRCLE.stream().map(PadRight.zeros(3)));
    }
    // ---
    if (Objects.nonNull(vs)) {
      vs = Tensor.of(vs.stream().map(sigma_inverse::dot));
      if (form_shadow) {
        Exponential exponential = geodesicDisplay.hsManifold().exponential(p);
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
      Tensor ellipse = vs; // Tensor.of(vs.stream().map(geodesicDisplay.tangentProjection(p))); // from 3d to 2d
      Path2D path2d = geometricLayer.toPath2D(ellipse, true);
      graphics.setColor(new Color(64, 192, 64, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(64, 192, 64, 192));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public void renderEllipseMahalanobis() {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      Mahalanobis mahalanobis = new Mahalanobis(new HsDesign(vectorLogManifold).matrix(sequence, origin));
      renderEllipse(origin, mahalanobis.sigma_inverse());
    }
  }

  private static final Scalar FACTOR = RealScalar.of(0.3);

  public void renderEllipseIdentity() {
    renderEllipse(origin, DiagonalMatrix.of(geodesicDisplay.dimensions(), FACTOR));
  }

  public void renderEllipseIdentityP() {
    Tensor matrix = DiagonalMatrix.of(geodesicDisplay.dimensions(), FACTOR);
    for (Tensor point : sequence)
      renderEllipse(point, matrix);
  }

  public void renderMahalanobisFormXEV(ColorDataGradient colorDataGradient) {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      Mahalanobis mahalanobis = new Mahalanobis(new HsDesign(vectorLogManifold).matrix(sequence, origin));
      renderMahalanobisMatrix(origin, mahalanobis, colorDataGradient);
    }
  }

  public void renderEllipseMahalanobisP() {
    VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
    for (Tensor point : sequence) {
      Mahalanobis mahalanobis = new Mahalanobis(new HsDesign(vectorLogManifold).matrix(sequence, point));
      renderEllipse(point, mahalanobis.sigma_inverse());
    }
  }

  /***************************************************/
  /** @param colorDataGradient */
  public void renderInfluenceX(ColorDataGradient colorDataGradient) {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      Tensor design = new HsDesign(vectorLogManifold).matrix(sequence, origin);
      Tensor matrix = InfluenceMatrix.of(design).matrix();
      // ---
      graphics.setFont(FONT_MATRIX);
      MatrixRender matrixRender = MatrixRender.absoluteOne(graphics, CONSTANT, colorDataGradient);
      matrixRender.setScalarMapper(Round._2);
      renderMatrix(origin, matrixRender, matrix);
    }
  }

  public void renderInfluenceP(ColorDataGradient colorDataGradient) {
    if (Tensors.nonEmpty(sequence)) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      // HsProjection hsProjection = ;
      // Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
      Tensor projections = Tensor.of(sequence.stream() //
          .map(point -> InfluenceMatrix.of(new HsDesign(vectorLogManifold).matrix(sequence, point)).matrix()));
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
  }

  /***************************************************/
  public void renderMatrix(Tensor pos, Tensor matrix, ColorDataIndexed colorDataIndexed) {
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.of(graphics, colorDataIndexed, new Color(255, 255, 255, 32));
    matrixRender.setScalarMapper(Round._3);
    renderMatrix(pos, matrixRender, matrix);
  }

  public void renderMatrix2(Tensor p, Tensor matrix) {
    ColorDataIndexed colorDataIndexed = new ColorDataIndexed() {
      @Override
      public Tensor apply(Scalar t) {
        return Tensors.vector(64, 64, 64, 128);
      }

      @Override
      public int length() {
        return 1;
      }

      @Override
      public Color getColor(int index) {
        return new Color(64, 64, 64, 128);
      }

      @Override
      public ColorDataIndexed deriveWithAlpha(int alpha) {
        return this;
      }
    };
    graphics.setFont(FONT_MATRIX);
    MatrixRender matrixRender = MatrixRender.of(graphics, colorDataIndexed, new Color(255, 255, 255, 32));
    matrixRender.setScalarMapper(Round._3);
    renderMatrix(p, matrixRender, matrix);
  }

  /** render control points */
  public void renderSequence() {
    renderSequence(POINTS_RENDER_0);
  }

  public void renderSequence(PointsRender pointsRender) {
    pointsRender.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
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