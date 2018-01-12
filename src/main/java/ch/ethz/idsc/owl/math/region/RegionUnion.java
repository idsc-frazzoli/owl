// code by jph and jl
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;

/** RegionUnion is a region that defines membership
 * to be member in either of a collection of {@link Region}s
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RegionUnion.html">RegionUnion</a> */
public class RegionUnion<T> implements Region<T>, Serializable {
  /** combines a collection of {@link Region}s into one Region.
   * Membership is defined as membership in any of the regions in the collection.
   * The input collection is not copied but used by reference.
   * Modification to outside collection have effect on this region.
   * 
   * The function name is inspired by {@link ByteBuffer#wrap(byte[])}.
   * 
   * @param collection collection of Regions
   * @return the combined Regions */
  public static <T> Region<T> wrap(Collection<Region<T>> collection) {
    return new RegionUnion<>(collection);
  }

  // ---
  private final Collection<Region<T>> collection;

  private RegionUnion(Collection<Region<T>> collection) {
    this.collection = collection;
  }

  @Override // from Region
  public boolean isMember(T type) {
    return collection.stream().parallel().anyMatch(region -> region.isMember(type));
  }
}
