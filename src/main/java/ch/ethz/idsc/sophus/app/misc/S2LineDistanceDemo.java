// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.AppendOne;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.sophus.opt.SnLineDistances;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2LineDistanceDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Tensor GEODESIC_DOMAIN = Subdivide.of(0.0, 1.0, 11);
  private static final Tensor INITIAL = Tensors.fromString("{{-0.5, 0, 0}, {0.5, 0, 0}}").unmodifiable();
  // ---
  private final SpinnerLabel<SnLineDistances> spinnerLineDistances = SpinnerLabel.of(SnLineDistances.values());
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();

  public S2LineDistanceDemo() {
    super(false, GeodesicDisplays.S2_ONLY);
    spinnerLineDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "line distance");
    {
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(30);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
    }
    // ---
    setControlPointsSe2(INITIAL);
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // ---
    timerFrame.geometricComponent.setOffset(400, 400);
    setMidpointIndicated(false);
  }

  TensorNorm tensorNorm() {
    Tensor cp = getGeodesicControlPoints();
    return 1 < cp.length() //
        ? spinnerLineDistances.getValue().lineDistance().tensorNorm(cp.get(0), cp.get(1))
        : t -> RealScalar.ZERO;
  }

  private Tensor pixel2model(BufferedImage bufferedImage) {
    double rad = rad();
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.TWO); // model
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        AppendOne.FUNCTION.apply(scale.map(Scalar::reciprocal)) // pixel 2 model
            .pmul(Se2Matrix.flipY(bufferedImage.getHeight())));
  }

  private BufferedImage bufferedImage(int resolution, VectorLogManifold vectorLogManifold) {
    Tensor matrix = Tensors.matrix(array(resolution, tensorNorm()::norm));
    // ---
    Tensor colorData = matrix.map(spinnerColorData.getValue());
    return ImageFormat.of(colorData);
  }

  Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction) {
    double rad = rad();
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        Scalar z2 = RealScalar.ONE.subtract(Vector2NormSquared.of(point));
        if (Sign.isPositive(z2)) {
          Scalar z = Sqrt.FUNCTION.apply(z2);
          Tensor xyz = point.append(z);
          array[cy][cx] = clip.apply(tensorScalarFunction.apply(xyz));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    return array;
  }

  double rad() {
    return 1;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    RenderQuality.setDefault(graphics);
    BufferedImage bufferedImage = bufferedImage(spinnerRes.getValue(), geodesicDisplay.hsManifold());
    ImageRender.of(bufferedImage, pixel2model(bufferedImage)) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    // ---
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor cp = getGeodesicControlPoints();
    ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(cp.get(0), cp.get(1));
    graphics.setStroke(STROKE);
    Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
    graphics.setColor(new Color(192, 192, 192));
    graphics.draw(geometricLayer.toPath2D(ms));
    graphics.setStroke(new BasicStroke());
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S2LineDistanceDemo().setVisible(1200, 600);
  }
}
