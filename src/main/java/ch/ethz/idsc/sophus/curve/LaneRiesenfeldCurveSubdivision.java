// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

public class LaneRiesenfeldCurveSubdivision implements CurveSubdivision, Serializable {
  public static CurveSubdivision of(GeodesicInterface geodesicInterface, int degree) {
    return new LaneRiesenfeldCurveSubdivision(geodesicInterface, degree, RationalScalar.HALF);
  }

  public static CurveSubdivision numeric(GeodesicInterface geodesicInterface, int degree) {
    return new LaneRiesenfeldCurveSubdivision(geodesicInterface, degree, RealScalar.of(0.5));
  }

  // ---
  private final CurveSubdivision curveSubdivision;
  private final GeodesicInterface geodesicInterface;
  private final int degree;
  private final Scalar half;

  private LaneRiesenfeldCurveSubdivision(GeodesicInterface geodesicInterface, int degree, Scalar half) {
    curveSubdivision = new BSpline1CurveSubdivision(geodesicInterface);
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
    this.half = half;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor value = curveSubdivision.cyclic(tensor);
    for (int count = 2; count <= degree; ++count) {
      Tensor queue = Tensors.empty();
      if (Tensors.isEmpty(value))
        return value;
      boolean odd = count % 2 == 1;
      if (odd) {
        Tensor p = Last.of(value);
        for (int index = 0; index < value.length(); ++index) {
          Tensor q = value.get(index);
          queue.append(center(p, q));
          p = q;
        }
      } else {
        Tensor p = value.get(0);
        for (int index = 1; index <= value.length(); ++index) {
          Tensor q = value.get(index % value.length());
          queue.append(center(p, q));
          p = q;
        }
      }
      tensor = value;
      value = queue;
    }
    return value;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    Tensor value = curveSubdivision.string(tensor);
    for (int count = 2; count <= degree; ++count) {
      Tensor queue = Tensors.empty();
      if (Tensors.isEmpty(value))
        return value;
      Tensor p = value.get(0);
      boolean odd = count % 2 == 1;
      if (odd)
        queue.append(tensor.get(0));
      for (int index = 1; index < value.length(); ++index) {
        // Tensor q = value.get(index);
        queue.append(center(p, p = value.get(index)));
        // p = q;
      }
      if (odd)
        queue.append(Last.of(tensor));
      tensor = value;
      value = queue;
    }
    return value;
  }

  protected final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, half);
  }
}
