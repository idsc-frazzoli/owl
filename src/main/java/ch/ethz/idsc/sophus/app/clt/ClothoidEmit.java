// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilderImpl;
import ch.ethz.idsc.sophus.clt.ClothoidContext;
import ch.ethz.idsc.sophus.clt.mid.ClothoidQuadratic;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegration;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegrations;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidEmit {
  ;
  public static Stream<Clothoid> stream(ClothoidContext clothoidContext, Tensor lambdas) {
    Builder<Clothoid> builder = Stream.builder();
    for (Tensor _lambda : lambdas) {
      ClothoidQuadratic clothoidQuadratic = CustomClothoidQuadratic.of(_lambda.Get());
      ClothoidIntegration clothoidIntegration = ClothoidIntegrations.ANALYTIC;
      ClothoidBuilderImpl clothoidBuilderImpl = new ClothoidBuilderImpl(clothoidQuadratic, clothoidIntegration);
      builder.accept(clothoidBuilderImpl.from(clothoidContext));
    }
    return builder.build();
  }
}
