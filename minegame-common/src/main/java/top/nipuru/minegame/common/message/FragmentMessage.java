package top.nipuru.minegame.common.message;

import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
public class FragmentMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 8318529216702482329L;
    public final int formatterIdx;
    public final Serializable[] args;
}