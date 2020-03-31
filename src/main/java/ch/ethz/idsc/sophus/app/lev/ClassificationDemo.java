// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/* package */ class ClassificationDemo extends AbstractHoverDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_O = ColorDataLists._097.cyclic();
  private static final ColorDataIndexed COLOR_DATA_INDEXED_T = COLOR_DATA_INDEXED_O.deriveWithAlpha(128);
  // ---
  private Classification classification;

  @Override
  void shuffle(int n) {
    super.shuffle(n);
    // assignment of random labels to points
    Tensor vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), RANDOM, n);
    classification = new Classification(vector);
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
    for (int label = 0; label < classification.size(); ++label) {
      Tensor sequence = Tensor.of(classification.labelIndices(label).mapToObj(controlPoints::get));
      PointsRender pointsRender = new PointsRender( //
          COLOR_DATA_INDEXED_T.getColor(label), //
          COLOR_DATA_INDEXED_O.getColor(label));
      pointsRender.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
      int bestLabel = classification.getArgMax(leverRender.getWeights());
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicMouse));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(COLOR_DATA_INDEXED_T.getColor(bestLabel));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_O.getColor(bestLabel));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new ClassificationDemo().setVisible(1200, 900);
  }
}
