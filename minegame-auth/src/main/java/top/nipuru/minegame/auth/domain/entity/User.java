package top.nipuru.minegame.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TableName("tb_user")
public class User extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -4779621439334893491L;

    /** 主键id */
    @TableId(type = IdType.AUTO)
    Integer playerId;

    /** 数据库id */
    Integer dbId;

    /** uuid */
    String uniqueId;

    /** 最后登录ip */
    String lastIp;


}
