package essentialmonolith.model;

import javax.persistence.*;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
public class Client extends Dimension {
}
