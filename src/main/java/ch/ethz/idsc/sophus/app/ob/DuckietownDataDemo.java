// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;

enum DuckietownDataDemo {
  ;
  static final List<String> LIST = Arrays.asList( //
      "duckie20180713-175124.csv", //
      "duckie20180713-175420.csv", //
      "duckie20180713-175601.csv", //
      "duckie20180901-152902.csv");

  public static void main(String[] args) {
    for (String string : LIST) {
      Tensor tensor = ResourceData.of("/autolab/localization/2018/" + string);
      System.out.println(Pretty.of(tensor));
    }
  }
}
