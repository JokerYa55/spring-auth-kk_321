package app.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Vasiliy.Andricov
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RestResult {

    private int status;
    private Object data;

}
