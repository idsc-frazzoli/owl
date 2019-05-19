// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class FrequencyEffectDataExport {
  private static final SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
  private static final int radius = 6;

  private static void export(Tensor tensor, String name, int index) throws IOException {
    FileWriter writer = new FileWriter("190514" + name + index + ".csv");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tensor.length(); i++) {
      sb.append(tensor.get(i).Get(0).toString());
      sb.append(",");
      sb.append(tensor.get(i).Get(1).toString());
      sb.append(",");
      sb.append(tensor.get(i).Get(2).toString());
      sb.append("\n");
    }
    writer.write(sb.toString());
    writer.close();
  }

  public static void process(Tensor control, int index) {
    // process data and map to se2
    TensorUnaryOperator geodesicCenter = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor groupSmoothed = GeodesicCenterFilter.of(geodesicCenter, radius).apply(control);
    Tensor groupRaw = control;
    Tensor algebraSmoothed = Tensor.of(groupSmoothed.stream().map(xya -> Se2CoveringExponential.INSTANCE.log(xya)));
    Tensor algebraRaw = Tensor.of(control.stream().map(xya -> Se2CoveringExponential.INSTANCE.log(xya)));
    // export data to .txt
    try {
      // lie Algebra
      export(algebraSmoothed, "AlgebraSmoothed", index);
      export(algebraRaw, "AlgebraRaw", index);
      // lie Group
      export(groupSmoothed, "GroupSmoothed", index);
      export(groupRaw, "GroupRaw", index);
    } catch (IOException e) {
      // // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    List<String> list = ResourceData.lines("/dubilab/app/pose/index.vector");
    Iterator<String> iterator = list.iterator();
    int index = 0;
    while (iterator.hasNext() && index < 120) {
      Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
          iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
      index++;
      process(control, index);
    }
  }
}