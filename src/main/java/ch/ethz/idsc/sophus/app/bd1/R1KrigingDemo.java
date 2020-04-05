// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class R1KrigingDemo extends A1KrigingDemo {
  public R1KrigingDemo() {
    super(R2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 1, 0}, {2, 2, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  private static final Scalar MARGIN = RealScalar.of(2);

  static Tensor domain(Tensor support) {
    return Subdivide.of( //
        support.stream().reduce(Min::of).get().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).get().add(MARGIN), 128).map(N.DOUBLE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    // ---
    Tensor control = Sort.of(getGeodesicControlPoints());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor domain = domain(support);
      Tensor sequence = support.map(Tensors::of);
      ScalarUnaryOperator variogram = PowerVariogram.fit(sequence, funceva, spinnerBeta.getValue());
      Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), support.length()));
      Kriging kriging = spinnerKriging.getValue().regression( //
          geodesicDisplay.flattenLogManifold(), variogram, sequence, funceva, covariance);
      Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(kriging::estimate));
      new PathRender(Color.BLUE, 1.25f) //
          .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
          .render(geometricLayer, graphics);
      Tensor errors = Tensor.of(domain.stream().map(Tensors::of).map(kriging::variance));
      // ---
      new PathRender(Color.RED, STROKE) //
          .setCurve(Transpose.of(Tensors.of(domain, result.add(errors))), false) //
          .render(geometricLayer, graphics);
      new PathRender(Color.GREEN, STROKE) //
          .setCurve(Transpose.of(Tensors.of(domain, result.subtract(errors))), false) //
          .render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new R1KrigingDemo().setVisible(1000, 800);
  }
}
