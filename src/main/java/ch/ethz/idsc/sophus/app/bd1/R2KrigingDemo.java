// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class R2KrigingDemo extends A2KrigingDemo {
  public R2KrigingDemo() {
    super(R2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    // specific to geodesic display
    Tensor pointsSe2 = getControlPointsSe2();
    Tensor sequence = Tensor.of(pointsSe2.stream().map(Extract2D.FUNCTION));
    Tensor values = pointsSe2.get(Tensor.ALL, 2);
    // general
    FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
    Kriging kriging = //
        spinnerKriging.getValue().regression(flattenLogManifold, variogram, sequence, values, covariance);
    // ---
    double rad = 10.0;
    int res = spinnerRes.getValue();
    Tensor dx = Subdivide.of(0, rad, res);
    Tensor dy = Subdivide.of(rad, 0, res);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        array[cy][cx] = clip.apply((Scalar) kriging.estimate(point));
      }
    });
    Tensor matrix = Tensors.matrix(array);
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
    // ---
    Tensor colorData = matrix.map(spinnerColorData.getValue());
    BufferedImage bufferedImage = ImageFormat.of(colorData);
    Tensor range = Tensors.vector(rad, rad); // model
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    Tensor pixel2model = Dot.of( //
        // Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
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
    new R2KrigingDemo().setVisible(1000, 800);
  }
}
