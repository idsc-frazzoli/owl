// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class LieGroupFiltersTest extends TestCase {
  public void testSimple() {
    List<String> lines = GokartPoseData.INSTANCE.list();
    Tensor control = GokartPoseData.getPose(lines.get(0), 250);
    GeodesicDisplay geodesicDisplay = Se2GeodesicDisplay.INSTANCE;
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieExponential lieExponential = geodesicDisplay.lieExponential();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    int radius = 7;
    Map<LieGroupFilters, Tensor> map = new EnumMap<>(LieGroupFilters.class);
    for (LieGroupFilters lieGroupFilters : LieGroupFilters.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          lieGroupFilters.supply(geodesicInterface, smoothingKernel, lieGroup, lieExponential, biinvariantMean);
      Tensor filtered = CenterFilter.of(tensorUnaryOperator, radius).apply(control);
      map.put(lieGroupFilters, filtered);
    }
    for (LieGroupFilters lieGroupFilters : LieGroupFilters.values()) {
      Tensor diff = map.get(lieGroupFilters).subtract(map.get(LieGroupFilters.BIINVARIANT_MEAN));
      diff.set(So2.MOD, Tensor.ALL, 2);
      Scalar norm = Norm.INFINITY.ofMatrix(diff);
      assertTrue(Chop._02.allZero(norm));
    }
  }
}
