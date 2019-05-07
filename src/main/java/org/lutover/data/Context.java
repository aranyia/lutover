package org.lutover.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class Context {

    private String idempotenceId;

    private String agentId;

}
