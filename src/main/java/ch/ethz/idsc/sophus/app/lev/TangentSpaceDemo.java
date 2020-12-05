// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Amplifiers;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate.Evaluation;
import ch.ethz.idsc.sophus.lie.r2.ConvexHull;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/* package */ class TangentSpaceDemo extends AbstractPlaceDemo {
  private static final Tensor BETAS = Tensors.fromString("{1/8, 1/2, 1, 2, 5, 10, 15, 20}");
  private static final int WIDTH = 300;
  // ---
  private final SpinnerLabel<Amplifiers> spinnerAmps = SpinnerLabel.of(Amplifiers.values());
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();

  public TangentSpaceDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerAmps.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "refinement");
    }
    {
      spinnerRefine.setList(Arrays.asList(1, 5, 10, 20, 50, 100, 200));
      spinnerRefine.setValue(10);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(2));
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
    final Tensor levers = getGeodesicControlPoints();
    {
      Tensor hull = ConvexHull.of(levers);
      PathRender pathRender = new PathRender(new Color(0, 0, 255, 128));
      pathRender.setCurve(hull, true);
      pathRender.render(geometricLayer, graphics);
    }
    if (ConvexHull.isInside(levers)) {
      Amplifiers amplifiers = spinnerAmps.getValue();
      TensorUnaryOperator tensorUnaryOperator = amplifiers.supply(spinnerBeta.getValue());
      IterativeAffineCoordinate iterativeAffineCoordinate = //
          new IterativeAffineCoordinate(tensorUnaryOperator, spinnerRefine.getValue());
      Deque<Evaluation> deque = iterativeAffineCoordinate.factors(levers);
      // for (Evaluation evaluation : deque)
      // POINTS_RENDER.show( //
      // geodesicDisplay()::matrixLift, geodesicDisplay().shape(), evaluation.factor().pmul(levers)) //
      // .render(geometricLayer, graphics);
      {
        Tensor leversVirtual = deque.peekLast().factors().pmul(levers);
        {
          graphics.setColor(Color.GRAY);
          for (int index = 0; index < levers.length(); ++index) {
            Line2D line2d = geometricLayer.toLine2D(levers.get(index), leversVirtual.get(index));
            graphics.draw(line2d);
          }
        }
        LeversRender leversRender = LeversRender.of( //
            geodesicDisplay(), leversVirtual, Array.zeros(2), geometricLayer, graphics);
        leversRender.renderSequence(POINTS_RENDER);
        Tensor weights = genesis.origin(leversVirtual);
        weights = iterativeAffineCoordinate.origin(deque, levers);
        leversRender.renderWeights(weights);
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Weights");
          Tensor domain = Range.of(0, deque.size());
          for (int index = 0; index < levers.length(); ++index) {
            int fi = index;
            visualSet.add(domain, Tensor.of(deque.stream() //
                .map(Evaluation::weights) //
                .map(tensor -> tensor.Get(fi))));
          }
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          jFreeChart.draw(graphics, new Rectangle2D.Double(1 * WIDTH, 0, WIDTH, 200));
        }
        {
          VisualSet visualSet = new VisualSet();
          visualSet.setPlotLabel("Factors");
          Tensor domain = Range.of(0, deque.size());
          for (int index = 0; index < levers.length(); ++index) {
            int fi = index;
            visualSet.add(domain, Tensor.of(deque.stream() //
                .map(Evaluation::factors) //
                .map(tensor -> tensor.Get(fi))));
          }
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          jFreeChart.draw(graphics, new Rectangle2D.Double(0 * WIDTH, 0, WIDTH, 200));
        }
      }
    }
    {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderWeights(genesis.origin(levers));
    }
  }

  public static void main(String[] args) {
    new TangentSpaceDemo().setVisible(1300, 900);
  }
}
