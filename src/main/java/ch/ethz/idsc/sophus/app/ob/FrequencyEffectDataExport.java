// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterTangentSpace;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class FrequencyEffectDataExport {
  private static final SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;

  private static void export(Tensor tensor, String name, int index, int radius) throws IOException {
    FileWriter writer = new FileWriter("200519" + "GCF" + radius + name + index + ".csv");
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

  public static void process(Tensor control, int index, int radius) {
    // process data and map to se2
    // TensorUnaryOperator geodesicCenter = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
    TensorUnaryOperator geodesicCenterTangentSpace = GeodesicCenterTangentSpace.of(Se2CoveringExponential.INSTANCE, smoothingKernel);
    Tensor groupSmoothed = GeodesicCenterFilter.of(geodesicCenterTangentSpace, radius).apply(control);
    Tensor groupRaw = control;
    Tensor algebraSmoothed = Tensor.of(groupSmoothed.stream().map(xya -> Se2CoveringExponential.INSTANCE.log(xya)));
    Tensor algebraRaw = Tensor.of(control.stream().map(xya -> Se2CoveringExponential.INSTANCE.log(xya)));
    // export data to .txt
    try {
      // lie Algebra
      export(algebraSmoothed, "AlgebraSmoothed", index, radius);
      export(algebraRaw, "AlgebraRaw", index, radius);
      // lie Group
      export(groupSmoothed, "GroupSmoothed", index, radius);
      export(groupRaw, "GroupRaw", index, radius);
    } catch (IOException e) {
      // // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    for (int radius = 1; radius < 11; ++radius) {
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.vector");
      Iterator<String> iterator = list.iterator();
      int index = 0;
      while (iterator.hasNext() && index < 120) {
        Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
            iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
        System.out.println(radius + " " + index);
        index++;
        process(control, index, radius);
      }
    }
  }
}