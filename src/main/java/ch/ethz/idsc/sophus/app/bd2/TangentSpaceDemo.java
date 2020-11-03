// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.sca.Exp;

/* package */ class TangentSpaceDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{1, 2, 5, 10}");
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public TangentSpaceDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerRefine.setList(Arrays.asList(5, 10, 20, 50, 100, 200));
      spinnerRefine.setValue(10);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(1));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(70, 28), "beta");
      // spinnerBeta.addSpinnerListener(v -> recompute());
    }
    Tensor sequence = Tensor.of(CirclePoints.of(13).multiply(RealScalar.of(2)).stream().skip(5).map(PadRight.zeros(3)));
    setControlPointsSe2(sequence);
  }

  public static Deque<Tensor> static_origin(Genesis genesis, Tensor levers, int k, Scalar accel) {
    Deque<Tensor> deque = new ArrayDeque<>(k);
    int n = levers.length();
    Tensor average = ConstantArray.of(RationalScalar.of(1, n), n);
    Tensor factor = ConstantArray.of(RealScalar.ONE, n);
    Tensor current = levers;
    for (int depth = 0; depth < k; ++depth) {
      Tensor scaling = genesis.origin(current).subtract(average).map(accel::multiply).map(Exp.FUNCTION);
      factor = factor.pmul(scaling);
      current = factor.pmul(levers);
      deque.add(current);
    }
    return deque;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Genesis genesis = MetricCoordinate.of(InversePowerVariogram.of(2));
    genesis = AffineCoordinate.INSTANCE;
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor sequence = getGeodesicControlPoints();
    {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), sequence, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderWeights(genesis.origin(sequence));
    }
    // ---
    Deque<Tensor> deque = static_origin(AffineCoordinate.INSTANCE, sequence, spinnerRefine.getValue(), spinnerBeta.getValue());
    for (Tensor levers : deque) {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      // Path2D path2d = geometricLayer.toPath2D(levers);
      // graphics.setColor(new Color(215, 215, 215, 128));
      // graphics.draw(path2d);
    }
    {
      Tensor levers = deque.peekLast();
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      Tensor weights = genesis.origin(levers);
      leversRender.renderWeights(weights);
    }
  }

  public static void main(String[] args) {
    new TangentSpaceDemo().setVisible(1300, 900);
  }
}
