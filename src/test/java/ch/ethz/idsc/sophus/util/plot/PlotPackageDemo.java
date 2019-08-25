// code by gjoel, jph
package ch.ethz.idsc.sophus.util.plot;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum PlotPackageDemo {
  ;
  public static void main(String[] args) throws IOException {
    String name = PlotPackageDemo.class.getSimpleName();
    TestHelper.cascade(HomeDirectory.Pictures(name, "labels_0"), false);
    TestHelper.cascade(HomeDirectory.Pictures(name, "labels_1"), true);
  }
}
