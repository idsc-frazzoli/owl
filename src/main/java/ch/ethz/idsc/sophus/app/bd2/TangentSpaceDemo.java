// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Deque;
import java.util.stream.Collectors;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.lie.r2.ConvexHull;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/* package */ class TangentSpaceDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{1, 2, 5, 10}");
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public TangentSpaceDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 5, 10, 20, 50, 100, 200));
      spinnerRefine.setValue(10);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(1));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(70, 28), "beta");
    }
    Tensor sequence = Tensor.of(CirclePoints.of(13).multiply(RealScalar.of(2)).stream().skip(5).map(PadRight.zeros(3)));
    setControlPointsSe2(sequence);
  }

  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 96));

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Genesis genesis = AffineCoordinate.INSTANCE;
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor levers = getGeodesicControlPoints();
    {
      Tensor hull = ConvexHull.of(levers);
      PathRender pathRender = new PathRender(new Color(0, 0, 255, 128));
      pathRender.setCurve(hull, true);
      pathRender.render(geometricLayer, graphics);
    }
    {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderWeights(genesis.origin(levers));
    }
    // ---
    IterativeAffineCoordinate itAfCoordinate = new IterativeAffineCoordinate(spinnerRefine.getValue(), spinnerBeta.getValue());
    Deque<Tensor> deque = itAfCoordinate.factors(levers);
    for (Tensor factor : deque)
      POINTS_RENDER.show(geodesicDisplay()::matrixLift, geodesicDisplay().shape(), factor.pmul(levers)) //
          .render(geometricLayer, graphics);
    {
      Tensor leversVirtual = deque.peekLast().pmul(levers);
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), leversVirtual, Array.zeros(2), geometricLayer, graphics);
      Tensor weights = genesis.origin(leversVirtual);
      leversRender.renderWeights(weights);
      VisualSet visualSet = new VisualSet();
      Tensor domain = Range.of(0, levers.length());
      // visualSet.add(domain, weights);
      for (Tensor factor : deque)
        visualSet.add(domain, factor);
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 300, 200));
    }
  }

  public static void main(String[] args) {
    new TangentSpaceDemo().setVisible(1300, 900);
  }
}
