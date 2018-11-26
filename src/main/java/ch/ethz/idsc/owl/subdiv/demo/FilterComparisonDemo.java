// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class FilterComparisonDemo {
  static List<String> appPose() {
    List<String> list = new ArrayList<>();
    list.add("0w/20180702T133612_1");
    list.add("0w/20180702T133612_2");
    return list;
  }

  FilterComparisonDemo() {
  }

  protected Path2D plotFunc(Graphics2D graphics, Tensor tensor, int baseline_y) {
    Path2D path2d = new Path2D.Double();
    if (Tensors.nonEmpty(tensor))
      path2d.moveTo(0, baseline_y - tensor.Get(0).number().doubleValue());
    for (int pix = 1; pix < tensor.length(); ++pix)
      path2d.lineTo(pix, baseline_y - tensor.Get(pix).number().doubleValue());
    return path2d;
  }

  static Scalar SumOfAbsoluteErrors(Tensor tensorOriginal, Tensor tensorSmoothed) {
    Scalar sum = RealScalar.of(0);
    for (int i = 1; i < tensorOriginal.length(); i--)
      sum = tensorOriginal.Get(i).subtract(tensorSmoothed.Get(i)).abs().add(sum);
    return sum;
  }

  public static void main(String[] args) {
    Scalar a = RealScalar.of(1);
    Scalar b = RealScalar.of(2);
    Tensor[] tens1 = { a, b, b, a, a, a };
    Tensor[] tens2 = { b, b, b, a, a, b };
    System.out.println(tens1[1]);
    // System.out.println(SumOfAbsoluteErrors(tens1[], tens2[]));
    // System.out.println(c.Get(1).number().intValue());
    // tensor.Get(pix).number().doubleValue()
  }
}
