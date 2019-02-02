// code by ob
package ch.ethz.idsc.sophus.filter;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

public class GeodesicEvaluation {
  public static final File ROOT = new File("C:/Users/Oliver/Desktop/MA/owl_export");
  // ---
  private final LieDifferences lieDifferences;

  GeodesicEvaluation(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieDifferences = new LieDifferences(lieGroup, lieExponential);
  }

  public Tensor evaluate0ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 0; i < causal.length(); ++i) {
      Tensor difference = lieDifferences.pair(causal.get(i), center.get(i));
      Scalar scalar1 = Norm._2.ofVector(difference.extract(0, 2));
      Scalar scalar2 = Norm._2.ofVector(difference.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  public Tensor evaluate1ErrorSeperated(Tensor causal, Tensor center) {
    Tensor errors = Tensors.empty();
    for (int i = 1; i < causal.length(); ++i) {
      Tensor pair1 = lieDifferences.pair(causal.get(i - 1), causal.get(i));
      Tensor pair2 = lieDifferences.pair(center.get(i - 1), center.get(i));
      Scalar scalar1 = Norm._2.between(pair1.extract(0, 2), pair2.extract(0, 2));
      Scalar scalar2 = Norm._2.between(pair1.extract(2, 3), pair2.extract(2, 3));
      errors.append(Tensors.of(scalar1, scalar2));
    }
    return Total.of(errors);
  }

  public Tensor processErrors(Tensor control, int width) {
    TableBuilder tableBuilder = new TableBuilder();
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    TensorUnaryOperator CenterFilter = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
    Tensor refinedCenter = GeodesicCenterFilter.of(CenterFilter, 6).apply(control);
    Tensor alpharange = Subdivide.of(0.1, 1, 40);
    WindowSideSampler windowSideSampler = new WindowSideSampler(smoothingKernel);
    for (int index = 0; index < alpharange.length(); index++) {
      Tensor refinedCausal = Tensors.empty();
      Tensor mask = windowSideSampler.apply(width).extract(0, width + 1);
      mask.append(alpharange.get(index));
      TensorUnaryOperator causalFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
      refinedCausal = Tensor.of(control.stream().map(causalFilter));
      Tensor row = Tensors.of(alpharange.Get(index), evaluate0ErrorSeperated(refinedCausal, refinedCenter), //
          evaluate1ErrorSeperated(refinedCausal, refinedCenter));
      tableBuilder.appendRow(row);
    }
    Tensor log = tableBuilder.toTable();
    return log;
  }

  public static void main(String[] args) throws IOException {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_2" + ".csv").stream() //
        .limit(300) //
        .map(row -> row.extract(1, 4)));
    ;
    GeodesicEvaluation geodesicEvaluation = new GeodesicEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    String dataname = "0w/20180702T133612_2";
    for (int width = 1; width < 12; width++) {
      Tensor log = geodesicEvaluation.processErrors(control, width);
      // Export.of(new File(ROOT, dataname.replace('/', '_') + "_" + width + ".csv"), log);
    }
  }
}
