package essentialmonolith.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OlapQuery {
    List<IdPair> whereList;
    List<String> groupByList;
}
