// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Optional;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.s2.S2Exponential;
import ch.ethz.idsc.sophus.hs.s2.S2Manifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.red.CopySign;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** symmetric positive definite 2 x 2 matrices */
public class S2GeodesicDisplay extends SnGeodesicDisplay {
  private static final long serialVersionUID = 1259749711079115640L;
  private static final TensorUnaryOperator PAD_RIGHT = PadRight.zeros(3, 3);
  // ---
  public static final GeodesicDisplay INSTANCE = new S2GeodesicDisplay();

  /***************************************************/
  private S2GeodesicDisplay() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xyz = xya.copy();
    Optional<Tensor> optional = optionalZ(xyz);
    if (optional.isPresent())
      return optional.get();
    xyz.set(RealScalar.ZERO, 2);
    // intersection of front and back hemisphere
    return Normalize.with(Norm._2).apply(xyz);
  }

  /** @param xyz normalized vector, point on 2-dimensional sphere
   * @return 2 x 3 matrix with rows spanning the space tangent to given xyz */
  /* package */ static Tensor tangentSpace(Tensor xyz) {
    return new S2Exponential(xyz).projection();
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor xyz) {
    return tangentSpace(xyz)::dot;
  }

  public Tensor createTangent(Tensor xya) {
    return createTangent(xya, xya.Get(2));
  }

  public Tensor createTangent(Tensor xya, Scalar angle) {
    Tensor xyz = project(xya);
    return AngleVector.of(angle).dot(tangentSpace(xyz));
  }

  public static Optional<Tensor> optionalZ(Tensor xya) {
    Tensor xy = xya.extract(0, 2);
    Scalar normsq = Norm2Squared.ofVector(xy);
    if (Scalars.lessThan(normsq, RealScalar.ONE)) {
      Scalar z = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(normsq));
      return Optional.of(xy.append(CopySign.of(z, xya.Get(2))));
    }
    return Optional.empty();
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return xyz.extract(0, 2);
  }

  private static final Clip CLIP_Z = Clips.interval(-2.5, 1);

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xyz) {
    Tensor frame = tangentSpace(xyz);
    Tensor skew = PAD_RIGHT.apply(Transpose.of(Tensors.of( //
        frame.get(0).extract(0, 2), //
        frame.get(1).extract(0, 2))));
    skew.set(RealScalar.ONE, 2, 2);
    Scalar r = CLIP_Z.rescale(xyz.Get(2));
    skew = Tensors.of(r, r, RealScalar.ONE).pmul(skew);
    return Se2Matrix.translation(toPoint(xyz)).dot(skew);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return S2ArrayPlot.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return S2Manifold.INSTANCE;
  }
}