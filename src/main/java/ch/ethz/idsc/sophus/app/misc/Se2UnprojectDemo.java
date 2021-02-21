// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Orthogonalize;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class Se2UnprojectDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.5);

  public Se2UnprojectDemo() {
    super(false, GeodesicDisplays.SE2C_SE2);
    Tensor tensor = Tensors.fromString("{{0, 0, 0}, {5, 0, 1}}");
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    HsManifold hsManifold = LieExponential.of(geodesicDisplay.lieGroup(), Se2CoveringExponential.INSTANCE);
    // ---
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor p = sequence.get(0);
    Tensor q = sequence.get(1);
    {
      ScalarTensorFunction curve = geodesicInterface.curve(p, q);
      Tensor tensor = Subdivide.of(-0.05, 1.05, 25).map(curve);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(geodesicDisplay::toPoint)));
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
    }
    Exponential exponential = hsManifold.exponential(p);
    Tensor log = exponential.log(q);
    Tensor matrix = Join.of(Tensors.of(log), IdentityMatrix.of(3));
    Tensor tensor = Orthogonalize.of(matrix).extract(0, 3);
    graphics.setColor(new Color(192, 192, 192, 64));
    Scalar nl = Vector2Norm.of(log);
    Scalar un = RealScalar.of(0.2).divide(Sqrt.FUNCTION.apply(nl));
    for (Tensor x : Subdivide.of(nl.zero(), nl, 11))
      for (Tensor y : Subdivide.of(un.negate(), un, 5))
        for (Tensor z : Subdivide.of(un.negate(), un, 5)) {
          Tensor px = Tensors.of(x, y, z);
          Tensor coord = px.dot(tensor);
          Tensor exp = exponential.exp(coord);
          // ---
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(exp));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
          path2d.closePath();
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
  }

  public static void main(String[] args) {
    new Se2UnprojectDemo().setVisible(1200, 600);
  }
}
