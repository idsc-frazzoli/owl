// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class FrequencyEffectDataExport {
  // private final static SmoothingKernel SMOOTHING_KERNEL = SmoothingKernel.GAUSSIAN;
  private static void export(Tensor tensor, String filterType, int index, int radius, SmoothingKernel smoothingKernel) throws IOException {
    // TODO OB use Export
    try (FileWriter writer = new FileWriter("030619_" + filterType + "_" + smoothingKernel.toString() + "_" + radius + "_" + index + ".csv")) {
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
    }
  }

  public static void processFilterComparison(Tensor control, int index, int radius, SmoothingKernel smoothingKernel) {
    // ==================== GeodesicCenter(Lefteeded) (Normal) ===========
    TensorUnaryOperator geodesicLeftSeeded = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor groupSmoothedGCL = CenterFilter.of(geodesicLeftSeeded, radius).apply(control);
    // ==================== GeodesicCenter(MidSeeded) ====================
    TensorUnaryOperator geodesicMidSeeded = GeodesicCenterMidSeeded.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor groupSmoothedGCM = CenterFilter.of(geodesicMidSeeded, radius).apply(control);
    // ==================== TangentSpaceFiltering ====================
    // TensorUnaryOperator geodesicCenterTangentSpace = GeodesicCenterTangentSpace.of( //
    // Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE, smoothingKernel);
    // Tensor groupSmoothedGCTS = GeodesicCenterFilter.of(geodesicCenterTangentSpace, radius).apply(control);
    // // ==================== BiinvariantMeanFilter =================
    // TensorUnaryOperator geodesicBiinvariantMean = Se2BiinvariantMeanCenter.of(smoothingKernel);
    // Tensor groupSmoothedGBM = Se2BiinvariantMeanFilter.of(geodesicBiinvariantMean, radius).apply(control);
    try {
      export(groupSmoothedGCL, "GCLeftSeeded", index, radius, smoothingKernel);
      export(groupSmoothedGCM, "GCMidSeeded", index, radius, smoothingKernel);
      // export(groupSmoothedGCTS, "TangentSpace", index, radius, smoothingKernel);
      // export(groupSmoothedGBM, "Biinvariant", index, radius, smoothingKernel);
      // export(control, "GroupRaw", index, radius, smoothingKernel);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    for (int radius = 1; radius < 20; ++radius) {
      List<String> list = ResourceData.lines("/dubilab/app/pose/index.vector");
      Iterator<String> iterator = list.iterator();
      int index = 0;
      for (SmoothingKernel sk : SmoothingKernel.values()) {
        while (iterator.hasNext() && index < 200) {
          Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
              iterator.next() + ".csv").stream().map(row -> row.extract(1, 4)));
          index++;
          System.out.println(radius + " " + sk + " " + index);
          processFilterComparison(control, index, radius, sk);
        }
      }
    }
  }
}