// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.opt.MixedLogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/** moving least squares */
/* package */ class R2DeformationDemo extends AbstractDeformationDemo {
  private static final double EXTENT = 5.0;
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.1));
  // ---
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
    super(GeodesicDisplays.R2_ONLY, MixedLogWeightings.scattered());
    // ---
    {
      jToggleRigidMotionFit.addActionListener(l -> recompute());
      timerFrame.jToolBar.add(jToggleRigidMotionFit);
    }
    timerFrame.geometricComponent.setOffset(300, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    setControlPointsSe2(Tensors.fromString( //
        "{{0.650, 4.183, 0.000}, {3.517, 4.650, 0.000}, {2.233, 2.733, 0.000}, {4.217, 2.917, 0.000}, {1.767, 1.150, 0.000}, {0.600, 0.317, 0.000}, {4.450, 0.550, 0.000}}"));
    // deformed to:
    // "{{1.400, 4.067, 0.000}, {2.867, 4.167, 0.000}, {1.667, 2.283, 0.000}, {3.983, 2.283, 0.000}, {2.617, 1.200, 0.000}, {0.600, 0.350, 0.000}, {3.917,
    // 1.183, 0.000}}"
    snap();
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
    // TODO meshgrid functionality is already(?)/should be generalized
    Tensor dx = Subdivide.of(0.0, EXTENT, res - 1);
    Tensor dy = Subdivide.of(0.0, EXTENT, res - 3);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
    TensorUnaryOperator tensorUnaryOperator = operator(movingOrigin);
    return jToggleRigidMotionFit.isSelected() //
        ? RnFittedMovingDomain2D.of(movingOrigin, tensorUnaryOperator, domain)
        : AveragedMovingDomain2D.of(movingOrigin, tensorUnaryOperator, domain);
  }

  @Override
  Tensor shapeOrigin() {
    return ORIGIN;
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return manifoldDisplay().biinvariantMean();
  }

  public static void main(String[] args) {
    new R2DeformationDemo().setVisible(1300, 800);
  }
}
