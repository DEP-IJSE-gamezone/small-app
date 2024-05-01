package lk.ijse.dep12.shared.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable { // to go through the network, should implement from the serializable class
    private String id;
    private String name;
}
