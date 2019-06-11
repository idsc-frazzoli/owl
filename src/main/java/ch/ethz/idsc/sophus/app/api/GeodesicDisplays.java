// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.filter.LieGroupFilters;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicDisplays {
  ;
  public static final List<GeodesicDisplay> ALL = Arrays.asList( //
      ClothoidCurveDisplay.INSTANCE, //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE, //
      H2GeodesicDisplay.INSTANCE, //
      He1GeodesicDisplay.INSTANCE, //
      St1GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> R2_ONLY = Arrays.asList( //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_ONLY = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2_ONLY = Arrays.asList( //
      Se2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2_R2 = Arrays.asList( //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> SE2C_SE2_R2 = Arrays.asList( //
      Se2CoveringGeodesicDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);
  // ---
  public static final List<GeodesicDisplay> CLOTH_SE2_R2 = Arrays.asList( //
      ClothoidCurveDisplay.INSTANCE, //
      Se2GeodesicDisplay.INSTANCE, //
      R2GeodesicDisplay.INSTANCE);

  public static TensorUnaryOperator filter( //
      GeodesicDisplay geodesicDisplay, //
      SmoothingKernel smoothingKernel, //
      LieGroupFilters lieGroupFilters) {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieExponential lieExponential = geodesicDisplay.lieExponential();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    return lieGroupFilters.supply(geodesicInterface, smoothingKernel, lieGroup, lieExponential, biinvariantMean);
  }
}
