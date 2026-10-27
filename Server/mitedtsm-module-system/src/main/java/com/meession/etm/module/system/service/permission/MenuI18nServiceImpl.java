package com.meession.etm.module.system.service.permission;

import com.meession.etm.module.system.dal.dataobject.permission.MenuI18nDO;
import com.meession.etm.module.system.dal.mysql.permission.MenuI18nMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单国际化 Service 实现类
 *
 * @author system
 */
@Service
@Validated
public class MenuI18nServiceImpl implements MenuI18nService {

    @Resource
    private MenuI18nMapper menuI18nMapper;

    @Override
    public Map<Long, List<MenuI18nDO>> getMenuI18nMap(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MenuI18nDO> list = menuI18nMapper.selectListByMenuIds(menuIds);
        return list.stream().collect(Collectors.groupingBy(MenuI18nDO::getMenuId));
    }

    @Override
    public List<MenuI18nDO> getMenuI18nList(Long menuId) {
        if (menuId == null) {
            return Collections.emptyList();
        }
        return menuI18nMapper.selectList(MenuI18nDO::getMenuId, menuId);
    }

    @Override
    public void saveMenuI18n(Long menuId, List<MenuI18nDO> i18nList) {
        // 先删除旧的国际化数据
        menuI18nMapper.deleteByMenuId(menuId);
        
        // 保存新的国际化数据
        if (i18nList != null && !i18nList.isEmpty()) {
            i18nList.forEach(item -> {
                item.setId(null); // 确保是新增
                item.setMenuId(menuId);
            });
            menuI18nMapper.insertBatch(i18nList);
        }
    }

    @Override
    public void deleteMenuI18n(Long menuId) {
        menuI18nMapper.deleteByMenuId(menuId);
    }

    @Override
    public String getMenuName(Long menuId, String language, String defaultName) {
        if (menuId == null || language == null) {
            return defaultName;
        }
        MenuI18nDO i18n = menuI18nMapper.selectByMenuIdAndLanguage(menuId, language);
        return i18n != null ? i18n.getName() : defaultName;
    }

    @Override
    public Map<Long, String> getMenuNameMap(List<Long> menuIds, String language) {
        if (menuIds == null || menuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MenuI18nDO> list = menuI18nMapper.selectListByMenuIds(menuIds);
        
        // 先按语言过滤，再按菜单ID分组
        Map<Long, String> result = new HashMap<>();
        for (MenuI18nDO item : list) {
            if (language.equals(item.getLanguage())) {
                result.put(item.getMenuId(), item.getName());
            }
        }
        return result;
    }

}
