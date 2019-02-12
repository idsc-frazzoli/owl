// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;

import javax.swing.JSlider;

import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** visualization of geodesic average along geodesics */
/* package */ class BezierFunctionSplitsDemo extends GeodesicSplitsDemo {
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  BezierFunctionSplitsDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    setControl(Tensors.fromString("{{0,0,0},{2,2,1},{5,0,2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    int n = vector.length();
    Scalar parameter = n <= 1 //
        ? RealScalar.ZERO
        : RationalScalar.of(n, n - 1);
    parameter = parameter.multiply(RationalScalar.of(jSlider.getValue(), 1000));
    return (SymScalar) scalarTensorFunction.apply(parameter);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BezierFunctionSplitsDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
