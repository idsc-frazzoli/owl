// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.surf.CatmullClarkSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class CatmullClarkSubdivisionDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  CatmullClarkSubdivisionDemo() {
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControl(Tensors.fromString("{{0,0,0},{1,0,0},{2,0,0},{0,1,0},{1,1,0},{2,1,0}}").multiply(RealScalar.of(2)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = controlSe2();
    for (Tensor point : control) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
      path2d.closePath();
      graphics.setColor(new Color(255, 128, 128, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(255, 128, 128, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(Se2CoveringGeodesic.INSTANCE);
    Tensor refined = Nest.of( //
        catmullClarkSubdivision::refine, //
        ArrayReshape.of(control, 2, 3, 3), //
        spinnerRefine.getValue());
    for (Tensor points : refined)
      for (Tensor point : points) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
        geometricLayer.popMatrix();
        int rgb = 128 + 32;
        path2d.closePath();
        graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
      }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new CatmullClarkSubdivisionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
