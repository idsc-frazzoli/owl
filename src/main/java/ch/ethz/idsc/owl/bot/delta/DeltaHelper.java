// code by jph and jl
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ enum DeltaHelper {
  ;
  // ---
  public static VectorFieldRender vectorFieldRender(StateSpaceModel stateSpaceModel, Tensor range, Region<Tensor> region, Scalar factor) {
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface sampler = BoxRandomSample.of(Tensors.vector(0, 0), range);
    Tensor points = Tensor.of(RandomSample.of(sampler, 1000).stream().filter(p -> !region.isMember(p)));
    vectorFieldRender.uv_pairs = //
        VectorFields.of(stateSpaceModel, points, Array.zeros(2), factor);
    return vectorFieldRender;
  }
}
