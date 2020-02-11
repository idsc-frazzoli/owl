// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class InverseDistanceDemo extends ControlPointsDemo {
  final JSlider jSlider = new JSlider(0, 100);

  public InverseDistanceDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    // ---
    {
      timerFrame.jToolBar.add(jSlider);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, 0, 0}, {3, 0, 0}, {2, 3, 1}, {5, -1, 2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsSe2 = getControlPointsSe2();
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    Scalar exponent = RationalScalar.of(jSlider.getValue() * 3, jSlider.getMaximum());
    graphics.drawString(exponent.map(Round._3).toString(), 0, 30);
    ScalarUnaryOperator power = Power.function(exponent);
    TensorMetric tensorMetric = new TensorMetric() {
      @Override
      public Scalar distance(Tensor p, Tensor q) {
        Scalar distance = geodesicDisplay.parametricDistance(p, q);
        return power.apply(distance);
      }
    };
    InverseDistanceWeighting inverseDistanceWeighting = new InverseDistanceWeighting(tensorMetric);
    Tensor domain = Tensor.of(controlPointsSe2.stream().map(geodesicDisplay::project));
    TensorUnaryOperator tuo = inverseDistanceWeighting.of(domain);
    Tensor point = geodesicDisplay.project(geometricLayer.getMouseSe2State());
    if (0 < controlPointsSe2.length()) {
      Tensor weights = tuo.apply(point);
      AffineQ.require(weights);
      Tensor mean = biinvariantMean.mean(getGeodesicControlPoints(), weights);
      Tensor matrix = geodesicDisplay.matrixLift(mean);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.7)));
      graphics.setColor(Color.GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new InverseDistanceDemo().setVisible(1200, 600);
  }
}
