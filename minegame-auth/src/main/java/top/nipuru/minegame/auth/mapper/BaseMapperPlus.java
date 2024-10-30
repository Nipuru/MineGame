package top.nipuru.minegame.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import java.util.Collection;
import java.util.List;

/**
 * 自定义 Mapper 接口, 实现 自定义扩展
 *
 * @author Nipuru
 * @since 2024/02/14 12:18
 */
public interface BaseMapperPlus<T> extends BaseMapper<T> {

    default List<T> selectList() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 批量插入
     */
    default boolean insertBatch(Collection<T> entityList) {
        return Db.saveBatch(entityList);
    }

    /**
     * 批量更新
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    /**
     * 批量插入或更新
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return Db.saveOrUpdateBatch(entityList);
    }

    /**
     * 批量插入(包含限制条数)
     */
    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        return Db.saveBatch(entityList, batchSize);
    }

    /**
     * 批量更新(包含限制条数)
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return Db.updateBatchById(entityList, batchSize);
    }

    /**
     * 批量插入或更新(包含限制条数)
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return Db.saveOrUpdateBatch(entityList, batchSize);
    }

    /**
     * 插入或更新
     */
    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

    /**
     * 链式查询 普通
     */
    default QueryChainWrapper<T> select() {
        return ChainWrappers.queryChain(this);
    }

    /**
     * 链式查询 lambda 式
     */
    default LambdaQueryChainWrapper<T> lambdaSelect() {
        return ChainWrappers.lambdaQueryChain(this);
    }


    /**
     * 链式查询 lambda 式
     */
    default LambdaQueryChainWrapper<T> lambdaSelect(T entity) {
        return ChainWrappers.lambdaQueryChain(this, entity);
    }

    /**
     * 链式更改 普通
     */
    default UpdateChainWrapper<T> update() {
        return ChainWrappers.updateChain(this);
    }

    /**
     * 链式更改 lambda 式
     */
    default LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this);
    }

}
