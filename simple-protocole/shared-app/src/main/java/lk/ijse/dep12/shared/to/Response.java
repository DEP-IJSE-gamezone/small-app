package lk.ijse.dep12.shared.to;

import lk.ijse.dep12.shared.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response implements Serializable {
    private Status status;
    private String msg;
}
