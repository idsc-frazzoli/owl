// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/* package */ class ClassificationDemo extends AbstractHoverDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_O = ColorDataLists._097.cyclic();
  private static final ColorDataIndexed COLOR_DATA_INDEXED_T = COLOR_DATA_INDEXED_O.deriveWithAlpha(128);
  // ---
  private final SpinnerLabel<Labels> spinnerLabels = SpinnerLabel.of(Labels.values());
  private Tensor vector;

  public ClassificationDemo() {
    {
      spinnerLabels.setValue(Labels.ARG_MAX);
      spinnerLabels.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "label");
    }
  }

  @Override
  void shuffle(int n) {
    super.shuffle(n);
    // assignment of random labels to points
    vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), RANDOM, n);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeverRender leverRender) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = leverRender.getSequence();
    Tensor geodesicMouse = leverRender.getOrigin();
    // ---
    leverRender.renderLevers();
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
    LabelInterface labelInterface = spinnerLabels.getValue().apply(vector);
    int bestLabel = labelInterface.label(leverRender.getWeights());
    geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicMouse));
    Path2D path2d = geometricLayer.toPath2D(shape, true);
    graphics.setColor(COLOR_DATA_INDEXED_T.getColor(bestLabel));
    graphics.fill(path2d);
    graphics.setColor(COLOR_DATA_INDEXED_O.getColor(bestLabel));
    graphics.draw(path2d);
    geometricLayer.popMatrix();
  }

  public static void main(String[] args) {
    new ClassificationDemo().setVisible(1200, 900);
  }
}
