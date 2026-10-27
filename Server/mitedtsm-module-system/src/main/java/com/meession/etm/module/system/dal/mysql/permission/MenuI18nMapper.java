package com.meession.etm.module.system.dal.mysql.permission;

import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.module.system.dal.dataobject.permission.MenuI18nDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单国际化 Mapper
 *
 * @author system
 */
@Mapper
public interface MenuI18nMapper extends BaseMapperX<MenuI18nDO> {

    /**
     * 根据菜单ID列表查询国际化数据
     *
     * @param menuIds 菜单ID列表
     * @return 国际化数据列表
     */
    default List<MenuI18nDO> selectListByMenuIds(List<Long> menuIds) {
        return selectList(MenuI18nDO::getMenuId, menuIds);
    }

    /**
     * 根据菜单ID查询国际化数据
     *
     * @param menuId 菜单ID
     * @return 国际化数据列表
     */
    default List<MenuI18nDO> selectListByMenuId(Long menuId) {
        return selectList(MenuI18nDO::getMenuId, menuId);
    }

    /**
     * 根据菜单ID和语言查询国际化数据
     *
     * @param menuId 菜单ID
     * @param language 语言代码
     * @return 国际化数据
     */
    default MenuI18nDO selectByMenuIdAndLanguage(Long menuId, String language) {
        return selectOne(MenuI18nDO::getMenuId, menuId, 
                        MenuI18nDO::getLanguage, language);
    }

    /**
     * 根据菜单ID删除国际化数据
     *
     * @param menuId 菜单ID
     */
    default void deleteByMenuId(Long menuId) {
        delete(MenuI18nDO::getMenuId, menuId);
    }

    /**
     * 批量插入国际化数据
     *
     * @param list 国际化数据列表
     */
    default void insertBatch(List<MenuI18nDO> list) {
        for (MenuI18nDO item : list) {
            insert(item);
        }
    }

}
