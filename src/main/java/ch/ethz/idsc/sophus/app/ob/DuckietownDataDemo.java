// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.sophus.app.filter.DuckietownSmoothingDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum DuckietownDataDemo {
  ;
  public static void main(String[] args) {
    for (String string : DuckietownSmoothingDemo.LIST) {
      Tensor tensor = ResourceData.of("/autolab/localization/2018/" + string);
      System.out.println(Pretty.of(tensor));
    }
  }
}
