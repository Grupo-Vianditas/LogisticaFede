package ar.edu.utn.dds.k3003.model.logistica;

import ar.edu.utn.dds.k3003.model.exceptions.SomeDomainException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SomeDomainObject {
  private String anAttribute;
  private Long otherAttribute;

  public SomeDomainObject sum(SomeDomainObject other) {
    if (Objects.isNull(other.getAnAttribute())) {
        try {
            throw new SomeDomainException("anAttribute is null", other);
        } catch (SomeDomainException e) {
            throw new RuntimeException(e);
        }
    }
    return new SomeDomainObject(
        anAttribute + other.getAnAttribute(), otherAttribute + other.getOtherAttribute());
  }
}
