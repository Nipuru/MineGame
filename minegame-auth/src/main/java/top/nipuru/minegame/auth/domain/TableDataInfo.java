package top.nipuru.minegame.auth.domain;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Nipuru
 * @since 2023/10/26 13:49
 */
@Data
@NoArgsConstructor
public class TableDataInfo<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 每页大小
     */
    private long size;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 当前页数
     */
    private long current;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> TableDataInfo<T> build(IPage<T> page) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("操作成功");
        rspData.setTotal(page.getTotal());
        rspData.setSize(page.getSize());
        rspData.setCurrent(page.getCurrent());
        rspData.setRows(page.getRecords());
        rspData.setPages(page.getPages());
        return rspData;
    }
}
