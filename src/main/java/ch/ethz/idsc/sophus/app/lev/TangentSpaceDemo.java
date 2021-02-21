// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Deque;
import java.util.Optional;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.bd2.GenesisDequeProperties;
import ch.ethz.idsc.sophus.gbc.it.GenesisDeque;
import ch.ethz.idsc.sophus.gbc.it.IterativeAffineCoordinate.Evaluation;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.d2.ConvexHull;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.ref.gui.ConfigPanel;

/* package */ class TangentSpaceDemo extends AbstractPlaceDemo {
  private static final int WIDTH = 300;
  // ---
  private final GenesisDequeProperties iterativeAffineProperties = new GenesisDequeProperties();

  public TangentSpaceDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    ConfigPanel configPanel = ConfigPanel.of(iterativeAffineProperties);
    container.add("West", configPanel.getFields());
    Tensor sequence = Tensor.of(CirclePoints.of(15).multiply(RealScalar.of(2)).stream().skip(5).map(PadRight.zeros(3)));
    sequence.set(Scalar::zero, 0, Tensor.ALL);
    setControlPointsSe2(sequence);
  }

  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 96));

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor origin = optional.get();
      VectorLogManifold vectorLogManifold = manifoldDisplay().hsManifold();
      final Tensor sequence = getSequence();
      HsDesign hsDesign = new HsDesign(vectorLogManifold);
      final Tensor levers2 = hsDesign.matrix(sequence, origin);
      {
        Tensor hull = ConvexHull.of(sequence);
        PathRender pathRender = new PathRender(new Color(0, 0, 255, 128));
        pathRender.setCurve(hull, true);
        pathRender.render(geometricLayer, graphics);
      }
      if (ConvexHull.isInside(levers2)) {
        GenesisDeque dequeGenesis = (GenesisDeque) iterativeAffineProperties.genesis();
        Deque<Evaluation> deque = dequeGenesis.deque(levers2);
        {
          Tensor leversVirtual = deque.peekLast().factors().pmul(levers2);
          geometricLayer.pushMatrix(Se2Matrix.translation(origin));
          {
            graphics.setColor(Color.GRAY);
            for (int index = 0; index < levers2.length(); ++index) {
              Line2D line2d = geometricLayer.toLine2D(levers2.get(index), leversVirtual.get(index));
              graphics.draw(line2d);
            }
          }
          {
            LeversRender leversRender = LeversRender.of( //
                manifoldDisplay(), leversVirtual, origin.map(Scalar::zero), geometricLayer, graphics);
            leversRender.renderSequence(POINTS_RENDER);
            // Tensor weights = iterativeAffineCoordinate.origin(deque, levers2);
            // leversRender.renderWeights(weights);
          }
          geometricLayer.popMatrix();
          {
            VisualSet visualSet = new VisualSet();
            visualSet.setPlotLabel("Weights");
            Tensor domain = Range.of(0, deque.size());
            for (int index = 0; index < levers2.length(); ++index) {
              int fi = index;
              visualSet.add(domain, Tensor.of(deque.stream() //
                  .map(Evaluation::weights) //
                  .map(tensor -> tensor.Get(fi))));
            }
            JFreeChart jFreeChart = ListPlot.of(visualSet);
            jFreeChart.draw(graphics, new Rectangle2D.Double(0 * WIDTH, 0, WIDTH, 200));
          }
          {
            VisualSet visualSet = new VisualSet();
            visualSet.setPlotLabel("Factors");
            Tensor domain = Range.of(0, deque.size());
            for (int index = 0; index < levers2.length(); ++index) {
              int fi = index;
              visualSet.add(domain, Tensor.of(deque.stream() //
                  .map(Evaluation::factors) //
                  .map(tensor -> tensor.Get(fi))));
            }
            JFreeChart jFreeChart = ListPlot.of(visualSet);
            jFreeChart.draw(graphics, new Rectangle2D.Double(1 * WIDTH, 0, WIDTH, 200));
          }
        }
      }
      {
        LeversRender leversRender = LeversRender.of( //
            manifoldDisplay(), sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderOrigin();
      }
    }
  }

  public static void main(String[] args) {
    new TangentSpaceDemo().setVisible(1300, 900);
  }
}
