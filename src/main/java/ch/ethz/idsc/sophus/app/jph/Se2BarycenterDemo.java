// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBarycenter;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.Arrowhead;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateRight;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clips;

public class Se2BarycenterDemo extends ControlPointsDemo {
  public Se2BarycenterDemo() {
    super(false, GeodesicDisplays.SE2C_ONLY);
    Tensor tensor = DubinsGenerator.of(Tensors.vector(0, 0, 0), Tensors.fromString("{{5,0,-1}}")) //
        .append(Tensors.vector(0, 1, 0)) //
        .append(Tensors.vector(0, 0, 1));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    if (sequence.length() == 4) {
      Tensor point;
      {
        Tensor mean = geometricLayer.getMouseSe2State();
        Tensor weights = new Se2CoveringBarycenter(sequence).apply(mean);
        Scalar scalar = weights.Get(1);
        point = Se2CoveringGeodesic.INSTANCE.split(sequence.get(0), sequence.get(1), scalar);
      }
      // ---
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      // ---
      {
        GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
        ScalarTensorFunction curve = geodesicInterface.curve(sequence.get(0), sequence.get(1));
        Tensor tensor = Subdivide.increasing(Clips.unit(), 20).map(curve);
        // geodesicDisplay.project(xya)
        Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(geodesicDisplay::toPoint)));
        graphics.setColor(Color.BLUE);
        graphics.draw(path2d);
      }
      {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(.3));
        path2d.closePath();
        graphics.setColor(Color.BLUE);
        graphics.fill(path2d);
        graphics.setColor(Color.RED);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
      // ---
      for (Tensor _x : Subdivide.of(-1, 1, 10))
        for (Tensor _y : Subdivide.of(-1, 1, 10)) {
          Scalar x = _x.Get();
          Scalar y = _y.Get();
          Scalar w = RationalScalar.HALF;
          Tensor weights = Tensors.of(w, x, y);
          weights.append(RealScalar.ONE.subtract(Total.ofVector(weights)));
          weights = RotateRight.of(weights, 1);
          Tensor mean = Se2CoveringBiinvariantMean.INSTANCE.mean(sequence, weights);
          {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
            Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(.1));
            path2d.closePath();
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        }
      // ---
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new Se2BarycenterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
