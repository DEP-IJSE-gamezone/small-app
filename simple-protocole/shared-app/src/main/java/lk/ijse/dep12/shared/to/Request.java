package lk.ijse.dep12.shared.to;

import lk.ijse.dep12.shared.util.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// we use serializable to go through the network
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {
    Type type;
    private Object body;
}
