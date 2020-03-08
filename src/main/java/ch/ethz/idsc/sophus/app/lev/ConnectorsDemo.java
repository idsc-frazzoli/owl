// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

/* package */ class ConnectorsDemo extends AbstractHoverDemo {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeverRender leverRender) {
    leverRender.renderLevers();
    // ---
    leverRender.renderWeights();
    leverRender.renderSequence();
    leverRender.renderOrigin();
    // ---
    Tensor weights = leverRender.getWeights();
    Tensor controlPoints = leverRender.getSequence();
    int length = controlPoints.length();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    graphics.setColor(Color.RED);
    for (int index = 0; index < length; ++index) {
      Tensor blend = UnitVector.of(length, index);
      Interpolation interpolation = LinearInterpolation.of(Tensors.of(weights, blend));
      Tensor map = Tensor.of(Subdivide.of(0.0, 1.0, 20).stream() //
          .map(Scalar.class::cast) //
          .map(value -> interpolation.at(value)) //
          .map(w -> biinvariantMean.mean(controlPoints, w)) //
          .map(geodesicDisplay::toPoint));
      Path2D path2d = geometricLayer.toPath2D(map);
      graphics.draw(path2d);
      // Tensor tensor = weights.get(index);
    }
  }

  public static void main(String[] args) {
    new ConnectorsDemo().setVisible(1200, 900);
  }
}
