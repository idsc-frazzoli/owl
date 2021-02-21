// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class ClassificationDemo extends AbstractHoverDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_O = ColorDataLists._097.cyclic();
  private static final ColorDataIndexed COLOR_DATA_INDEXED_T = COLOR_DATA_INDEXED_O.deriveWithAlpha(128);
  // ---
  private final SpinnerLabel<Labels> spinnerLabels = SpinnerLabel.of(Labels.values());
  private Tensor vector;

  public ClassificationDemo() {
    { // code redundant with R2ClassificationDemo
      SpinnerListener<LogWeighting> spinnerListener = new SpinnerListener<LogWeighting>() {
        @Override
        public void actionPerformed(LogWeighting logWeighting) {
          if (logWeighting.equals(LogWeightings.DISTANCES))
            spinnerLabels.setValue(Labels.ARG_MIN);
          else //
          if ( //
          logWeighting.equals(LogWeightings.WEIGHTING) || //
          logWeighting.equals(LogWeightings.COORDINATE))
            spinnerLabels.setValue(Labels.ARG_MAX);
        }
      };
      spinnerLogWeighting.addSpinnerListener(spinnerListener);
      spinnerListener.actionPerformed(logWeighting());
    }
    spinnerLabels.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "label");
  }

  @Override
  void shuffle(int n) {
    super.shuffle(n);
    // assignment of random labels to points
    vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), n);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    // GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor controlPoints = leversRender.getSequence();
    Tensor geodesicMouse = leversRender.getOrigin();
    // ---
    Tensor control = leversRender.getSequence();
    Tensor weights = operator(control).apply(leversRender.getOrigin());
    // leversRender.renderInfluenceX(ColorDataGradients.JET);
    // Tensor influence = new HsInfluence( //
    // geodesicDisplay.vectorLogManifold().logAt(leversRender.getOrigin()), //
    // control).matrix();
    // List<Edge> list = PrimAlgorithm.of(influence);
    // graphics.setColor(Color.BLACK);
    // Tensor domain = Subdivide.of(0.0, 1.0, 10);
    // for (Edge edge : list) {
    // Tensor p = control.get(edge.i);
    // Tensor q = control.get(edge.j);
    // ScalarTensorFunction curve = geodesicInterface.curve(p, q);
    // Path2D line = geometricLayer.toPath2D(domain.map(curve));
    // graphics.draw(line);
    // }
    leversRender.renderLevers(spinnerLabels.getValue().equals(Labels.ARG_MIN) //
        ? Sqrt.of(weights).negate()
        : weights);
    // ---
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.4));
    int index = 0;
    for (Tensor point : controlPoints) {
      int label = vector.Get(index).number().intValue();
      PointsRender pointsRender = new PointsRender( //
          COLOR_DATA_INDEXED_T.getColor(label), //
          COLOR_DATA_INDEXED_O.getColor(label));
      pointsRender.show(geodesicDisplay::matrixLift, shape, Tensors.of(point)).render(geometricLayer, graphics);
      ++index;
    }
    // ---
    Classification classification = spinnerLabels.getValue().apply(vector);
    int bestLabel = classification.result(weights).getLabel();
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicMouse));
    Path2D path2d = geometricLayer.toPath2D(shape, true);
    graphics.setColor(COLOR_DATA_INDEXED_T.getColor(bestLabel));
    graphics.fill(path2d);
    // ---
    graphics.setStroke(new BasicStroke(1.5f));
    graphics.setColor(Color.RED);
    graphics.draw(path2d);
    geometricLayer.popMatrix();
  }

  public static void main(String[] args) {
    new ClassificationDemo().setVisible(1200, 900);
  }
}
