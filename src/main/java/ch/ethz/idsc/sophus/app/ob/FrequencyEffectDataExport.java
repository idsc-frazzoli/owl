// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV1;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.opt.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum FrequencyEffectDataExport {
  ;
  private static void export(Tensor tensor, String filterType, int index, int radius, SmoothingKernel smoothingKernel) throws IOException {
    String name = "030619_" + filterType + "_" + smoothingKernel.toString() + "_" + radius + "_" + index + ".csv";
    Export.of(new File(name), tensor);
  }

  public static void processFilterComparison(Tensor control, int index, int radius, SmoothingKernel smoothingKernel) {
    // ==================== GeodesicCenter(Lefteeded) (Normal) ===========
    TensorUnaryOperator geodesicLeftSeeded = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor groupSmoothedGCL = CenterFilter.of(geodesicLeftSeeded, radius).apply(control);
    // ==================== GeodesicCenter(MidSeeded) ====================
    TensorUnaryOperator geodesicMidSeeded = GeodesicCenterMidSeeded.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor groupSmoothedGCM = CenterFilter.of(geodesicMidSeeded, radius).apply(control);
    // // ==================== BiinvariantMeanFilter =================
    // TensorUnaryOperator geodesicBiinvariantMean = Se2BiinvariantMeanCenter.of(smoothingKernel);
    // Tensor groupSmoothedGBM = Se2BiinvariantMeanFilter.of(geodesicBiinvariantMean, radius).apply(control);
    try {
      export(groupSmoothedGCL, "GCLeftSeeded", index, radius, smoothingKernel);
      export(groupSmoothedGCM, "GCMidSeeded", index, radius, smoothingKernel);
      // export(groupSmoothedGBM, "Biinvariant", index, radius, smoothingKernel);
      // export(control, "GroupRaw", index, radius, smoothingKernel);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    for (int radius = 1; radius < 20; ++radius) {
      List<String> list = GokartPoseDataV1.INSTANCE.list();
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