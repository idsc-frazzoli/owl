// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class S2KrigingDemo extends A2KrigingDemo {
  public S2KrigingDemo() {
    super(S2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {0.2, 0, 0}, {0.2, 0.2, 0}, {0, 0.2, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    // specific to geodesic display
    {
      Tensor pointsSe2 = getControlPointsSe2().copy();
      pointsSe2.set(Max.function(RealScalar.ZERO), Tensor.ALL, 2);
      setControlPointsSe2(pointsSe2);
    }
    // Tensor pointsSe2 = ;
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    // general
    FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
    Kriging kriging = //
        spinnerKriging.getValue().regression(flattenLogManifold, variogram, sequence, values, covariance);
    // ---
    double rad = 1.0;
    int res = spinnerRes.getValue();
    Tensor dx = Subdivide.of(-rad, +rad, res);
    Tensor dy = Subdivide.of(+rad, -rad, res);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        Scalar z2 = RealScalar.ONE.subtract(Norm2Squared.ofVector(point));
        if (Sign.isPositive(z2)) {
          Scalar z = Sqrt.FUNCTION.apply(z2);
          array[cy][cx] = clip.apply((Scalar) kriging.estimate(point.append(z)));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    Tensor matrix = Tensors.matrix(array);
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
    // ---
    Tensor colorData = matrix.map(spinnerColorData.getValue());
    BufferedImage bufferedImage = ImageFormat.of(colorData);
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.of(2)); // model
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    Tensor pixel2model = Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), // pixel 2 model
        Se2Matrix.flipY(bufferedImage.getHeight()));
    ImageRender.of(bufferedImage, pixel2model) //
        .render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    LeverRender leverRender = //
        new LeverRender(geodesicDisplay(), sequence, null, values, geometricLayer, graphics);
    leverRender.renderWeights();
  }

  public static void main(String[] args) {
    new S2KrigingDemo().setVisible(1000, 800);
  }
}
