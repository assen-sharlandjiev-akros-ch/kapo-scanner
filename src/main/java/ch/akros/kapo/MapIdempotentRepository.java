package ch.akros.kapo;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.spi.IdempotentRepository;

public class MapIdempotentRepository extends ConcurrentHashMap<String, Object> implements IdempotentRepository {

  private static final long serialVersionUID = 1L;

  @Override
  public boolean add(final String key) {
    return Objects.isNull(super.put(key, key));
  }

  @Override
  public boolean contains(final String key) {
    return containsKey(key);
  }

  @Override
  public boolean remove(final String key) {
    return super.remove(key) != null;
  }

  @Override
  public boolean confirm(final String key) {
    // NOOP
    return true;
  }

  @Override
  public void start() {
    // NOOP
  }

  @Override
  public void stop() {
    // NOOP
  }

}
