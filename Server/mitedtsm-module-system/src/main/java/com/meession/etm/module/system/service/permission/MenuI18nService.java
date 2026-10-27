package com.meession.etm.module.system.service.permission;

import com.meession.etm.module.system.dal.dataobject.permission.MenuI18nDO;

import java.util.List;
import java.util.Map;

/**
 * 菜单国际化 Service 接口
 *
 * @author system
 */
public interface MenuI18nService {

    /**
     * 根据菜单ID列表获取国际化数据
     *
     * @param menuIds 菜单ID列表
     * @return 菜单ID -> 国际化数据列表 的映射
     */
    Map<Long, List<MenuI18nDO>> getMenuI18nMap(List<Long> menuIds);

    /**
     * 根据菜单ID获取国际化数据
     *
     * @param menuId 菜单ID
     * @return 国际化数据列表
     */
    List<MenuI18nDO> getMenuI18nList(Long menuId);

    /**
     * 根据菜单ID和语言获取菜单名称
     *
     * @param menuId 菜单ID
     * @param language 语言代码
     * @param defaultName 默认名称（找不到时返回）
     * @return 菜单名称
     */
    String getMenuName(Long menuId, String language, String defaultName);

    /**
     * 保存菜单国际化数据
     *
     * @param menuId 菜单ID
     * @param i18nList 国际化数据列表
     */
    void saveMenuI18n(Long menuId, List<MenuI18nDO> i18nList);

    /**
     * 删除菜单国际化数据
     *
     * @param menuId 菜单ID
     */
    void deleteMenuI18n(Long menuId);

    /**
     * 批量获取菜单名称（根据语言）
     *
     * @param menuIds 菜单ID列表
     * @param language 语言代码
     * @return 菜单ID -> 名称 的映射
     */
    Map<Long, String> getMenuNameMap(List<Long> menuIds, String language);

}
