// code by ob
package ch.ethz.idsc.sophus.filter;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.app.ob.GeodesicCausalFilteringIIR;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;

public class GeodesicEvaluation {
  
  private final LieDifferences lieDifferences;
  public static final File ROOT = new File("C:/Users/Oliver/Desktop/MA/owl_export");

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
    WindowCenterSampler centerWindowSampler = new WindowCenterSampler(SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator CenterFilter = GeodesicCenter.of(Se2Geodesic.INSTANCE, centerWindowSampler);
    Tensor refinedCenter = GeodesicCenterFilter.of(CenterFilter, 6).apply(control);
   
    
    Tensor alpharange = Subdivide.of(0.1, 1, 12);
    for (int index = 0; index < alpharange.length(); index++) {
      Tensor mask = Normalize.with(Norm._1).apply(centerWindowSampler.apply(width).extract(0, width+1));
      mask.append(alpharange.get(index));
      TensorUnaryOperator CausalFilter = new GeodesicIIRnFilter(Se2Geodesic.INSTANCE, mask);
      Tensor refinedCausal = Tensor.of(control.stream().map(CausalFilter));
       Tensor row = Tensors.of(alpharange.Get(index), evaluate0ErrorSeperated(refinedCausal, refinedCenter), //
           evaluate1ErrorSeperated(refinedCausal, refinedCenter));
       tableBuilder.appendRow(row);
    }
    Tensor log = tableBuilder.toTable();
    System.out.println(Pretty.of(log.map(Round._4)));
    return log;
  }

  public static void main(String[] args) throws IOException {    
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_1" + ".csv").stream() //
        .limit(300) //
        .map(row -> row.extract(1, 4)));;
    GeodesicEvaluation geodesicEvaluation = new GeodesicEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    System.out.println(control.length());
    
    String dataname = "0w/20180702T133612_1";
    for (int width = 1; width < 12; width++) {
      Tensor log = geodesicEvaluation.processErrors(control, width);
      Export.of(new File(ROOT, dataname.replace('/', '_') + "_" + width + ".csv"), log);
    }
  }
}
