// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

/** class design stolen from java.util.Collections */
public enum Regions {
  ;
  private static class EmptyRegion<T> implements Region<T>, Serializable {
    @Override // from Region
    public boolean isMember(T type) {
      return false;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final Region EMPTY_REGION = new EmptyRegion<>();

  @SuppressWarnings("unchecked")
  public static final <T> Region<T> emptyRegion() {
    return EMPTY_REGION;
  }

  /***************************************************/
  private static class CompleteRegion<T> implements Region<T>, Serializable {
    @Override
    public boolean isMember(T type) {
      return true;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final Region COMPLETE_REGION = new CompleteRegion<>();

  @SuppressWarnings("unchecked")
  public static final <T> Region<T> completeRegion() {
    return COMPLETE_REGION;
  }
}
