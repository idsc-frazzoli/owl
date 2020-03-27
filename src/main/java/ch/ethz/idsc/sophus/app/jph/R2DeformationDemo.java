// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/** moving least squares */
/* package */ class R2DeformationDemo extends DeformationDemo {
  private static final int EXTENT = 5;
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.1));
  private final JToggleButton jToggleRigidMotionFit = new JToggleButton("MLS");
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      Tensor box = Tensors.fromString("{{0, 0}, {1, 0}, {1, 1}, {0, 1}}").multiply(RealScalar.of(EXTENT));
      Path2D path2d = geometricLayer.toPath2D(box, true);
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(path2d);
    }
  };

  R2DeformationDemo() {
    super(GeodesicDisplays.R2_ONLY, RnBarycentricCoordinates.SCATTERED);
    // ---
    {
      jToggleRigidMotionFit.addActionListener(l -> recomputeMD2D());
      timerFrame.jToolBar.add(jToggleRigidMotionFit);
    }
    // Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    // timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(2, 2, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(300, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distribution = UniformDistribution.of(0, EXTENT);
    return Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(0, EXTENT, res - 1);
    Tensor dy = Subdivide.of(0, EXTENT, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
    return jToggleRigidMotionFit.isSelected() //
        ? new LSMovingDomain2D(movingOrigin, weightingInterface(), domain)
        : new MovingDomain2D(movingOrigin, weightingInterface(), domain);
  }

  @Override
  Tensor shapeOrigin() {
    return ORIGIN;
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return geodesicDisplay().biinvariantMean();
  }

  public static void main(String[] args) {
    new R2DeformationDemo().setVisible(1000, 800);
  }
}
