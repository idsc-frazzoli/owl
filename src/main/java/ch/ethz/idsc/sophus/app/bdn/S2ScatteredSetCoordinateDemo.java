// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2ScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo {
  private final JToggleButton jToggleLower = new JToggleButton("lower");

  public S2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.S2_ONLY, LogWeightings.list());
    {
      jToggleLower.setSelected(true);
      timerFrame.jToolBar.add(jToggleLower);
    }
    // example in pdf
    setControlPointsSe2(Tensors.fromString( //
        "{{-0.293, 0.473, 0.000}, {0.613, 0.703, 0.000}, {0.490, -0.287, 0.000}, {-0.023, -0.693, 0.000}, {-0.713, 0.127, -0.524}, {0.407, 0.357, -0.524}, {0.000, -0.030, -0.524}, {0.233, -0.443, -0.524}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-0.423, 0.823, 0}, {0.823, 0.450, 0}, {-0.450, 0.330, 0}, {0.007, 0.003, 0}, {-0.487, -0.240, 0}, {0.583, -0.490, 0}, {-0.003, -0.753, 0}}"));
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override // from ExportCoordinateDemo
  public Tensor compute(TensorUnaryOperator weightingInterface, int refinement) {
    Tensor sX = Subdivide.of(-1.0, +1.0, refinement);
    Tensor sY = Subdivide.of(+1.0, -1.0, refinement);
    int n = sX.length();
    boolean lower = jToggleLower.isSelected();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Optional<Tensor> optionalP = S2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
        if (optionalP.isPresent()) {
          Tensor point = optionalP.get();
          wgs.set(weightingInterface.apply(point), c1, c0);
          if (lower) {
            point.set(Scalar::negate, 2);
            wgs.set(weightingInterface.apply(point), n + c1, c0);
          }
        }
        ++c1;
      }
    });
    return wgs;
  }

  public static void main(String[] args) {
    new S2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
