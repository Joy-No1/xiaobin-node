package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.exception.BusinessException;
import com.xml.xiaobinnode.entity.SysDictItem;
import com.xml.xiaobinnode.entity.SysDictType;
import com.xml.xiaobinnode.service.SysDictItemService;
import com.xml.xiaobinnode.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典接口：字典类型与字典项的增删改查
 */
@Tag(name = "字典接口", description = "通用字典类型与字典项的增删改查")
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class DictController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictItemService sysDictItemService;

    // ==================== 字典类型 CRUD ====================

    @GetMapping("/types")
    @Operation(summary = "获取所有字典类型")
    public List<SysDictType> getAllTypes() {
        return sysDictTypeService.list();
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "根据ID获取字典类型")
    public SysDictType getTypeById(@PathVariable Integer id) {
        SysDictType type = sysDictTypeService.getById(id);
        if (type == null) {
            throw new BusinessException(404, "字典类型不存在");
        }
        return type;
    }

    @PostMapping("/types")
    @Operation(summary = "创建字典类型")
    public SysDictType createType(@RequestBody SysDictType sysDictType) {
        if (StringUtils.isBlank(sysDictType.getTypeCode())) {
            throw new BusinessException(400, "字典类型编码不能为空");
        }
        SysDictType existing = sysDictTypeService.getByTypeCode(sysDictType.getTypeCode());
        if (existing != null) {
            throw new BusinessException(400, "字典类型编码已存在: " + sysDictType.getTypeCode());
        }
        sysDictTypeService.save(sysDictType);
        return sysDictType;
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "更新字典类型")
    public SysDictType updateType(@PathVariable Integer id, @RequestBody SysDictType sysDictType) {
        SysDictType existing = sysDictTypeService.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "字典类型不存在");
        }
        SysDictType byCode = sysDictTypeService.getByTypeCode(sysDictType.getTypeCode());
        if (byCode != null && !byCode.getId().equals(id)) {
            throw new BusinessException(400, "字典类型编码已存在: " + sysDictType.getTypeCode());
        }
        sysDictType.setId(id);
        sysDictTypeService.updateById(sysDictType);
        return sysDictTypeService.getById(id);
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除字典类型")
    public void deleteType(@PathVariable Integer id) {
        SysDictType existing = sysDictTypeService.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "字典类型不存在");
        }
        sysDictTypeService.removeById(id);
    }

    // ==================== 字典项 CRUD ====================

    @GetMapping("/items")
    @Operation(summary = "获取字典项", description = "传入typeCode时返回该类型下启用的字典项（按sort_order升序），不传则返回全部")
    public List<SysDictItem> getItems(@RequestParam(required = false) String typeCode) {
        if (StringUtils.isNotBlank(typeCode)) {
            return sysDictItemService.getActiveItemsByTypeCode(typeCode);
        }
        return sysDictItemService.list();
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "根据ID获取字典项")
    public SysDictItem getItemById(@PathVariable Integer id) {
        SysDictItem item = sysDictItemService.getById(id);
        if (item == null) {
            throw new BusinessException(404, "字典项不存在");
        }
        return item;
    }

    @PostMapping("/items")
    @Operation(summary = "创建字典项")
    public SysDictItem createItem(@RequestBody SysDictItem sysDictItem) {
        if (StringUtils.isBlank(sysDictItem.getTypeCode())) {
            throw new BusinessException(400, "字典类型编码不能为空");
        }
        SysDictType type = sysDictTypeService.getByTypeCode(sysDictItem.getTypeCode());
        if (type == null) {
            throw new BusinessException(400, "字典类型编码不存在: " + sysDictItem.getTypeCode());
        }
        boolean duplicate = sysDictItemService.getAllItemsByTypeCode(sysDictItem.getTypeCode()).stream()
                .anyMatch(i -> i.getItemCode().equals(sysDictItem.getItemCode()));
        if (duplicate) {
            throw new BusinessException(400, "该类型下字典项编码已存在: " + sysDictItem.getItemCode());
        }
        sysDictItemService.save(sysDictItem);
        return sysDictItem;
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "更新字典项")
    public SysDictItem updateItem(@PathVariable Integer id, @RequestBody SysDictItem sysDictItem) {
        SysDictItem existing = sysDictItemService.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "字典项不存在");
        }
        SysDictType type = sysDictTypeService.getByTypeCode(sysDictItem.getTypeCode());
        if (type == null) {
            throw new BusinessException(400, "字典类型编码不存在: " + sysDictItem.getTypeCode());
        }
        boolean duplicate = sysDictItemService.getAllItemsByTypeCode(sysDictItem.getTypeCode()).stream()
                .anyMatch(i -> i.getItemCode().equals(sysDictItem.getItemCode()) && !i.getId().equals(id));
        if (duplicate) {
            throw new BusinessException(400, "该类型下字典项编码已存在: " + sysDictItem.getItemCode());
        }
        sysDictItem.setId(id);
        sysDictItemService.updateById(sysDictItem);
        return sysDictItemService.getById(id);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除字典项")
    public void deleteItem(@PathVariable Integer id) {
        SysDictItem existing = sysDictItemService.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "字典项不存在");
        }
        sysDictItemService.removeById(id);
    }
}
