package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.dto.Result;
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
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class DictController {

    private final SysDictTypeService sysDictTypeService;
    private final SysDictItemService sysDictItemService;

    // ==================== 字典类型 CRUD ====================

    @GetMapping("/types")
    @Operation(summary = "获取所有字典类型")
    public Result<List<SysDictType>> getAllTypes() {
        return Result.success(sysDictTypeService.list());
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "根据ID获取字典类型")
    public Result<SysDictType> getTypeById(@PathVariable Integer id) {
        SysDictType type = sysDictTypeService.getById(id);
        if (type == null) {
            return Result.notFound("字典类型不存在");
        }
        return Result.success(type);
    }

    @PostMapping("/types")
    @Operation(summary = "创建字典类型")
    public Result<SysDictType> createType(@RequestBody SysDictType sysDictType) {
        if (StringUtils.isBlank(sysDictType.getTypeCode())) {
            return Result.badRequest("字典类型编码不能为空");
        }
        SysDictType existing = sysDictTypeService.getByTypeCode(sysDictType.getTypeCode());
        if (existing != null) {
            return Result.badRequest("字典类型编码已存在: " + sysDictType.getTypeCode());
        }
        sysDictTypeService.save(sysDictType);
        return Result.success(sysDictType);
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "更新字典类型")
    public Result<SysDictType> updateType(@PathVariable Integer id, @RequestBody SysDictType sysDictType) {
        SysDictType existing = sysDictTypeService.getById(id);
        if (existing == null) {
            return Result.notFound("字典类型不存在");
        }
        SysDictType byCode = sysDictTypeService.getByTypeCode(sysDictType.getTypeCode());
        if (byCode != null && !byCode.getId().equals(id)) {
            return Result.badRequest("字典类型编码已存在: " + sysDictType.getTypeCode());
        }
        sysDictType.setId(id);
        sysDictTypeService.updateById(sysDictType);
        return Result.success(sysDictTypeService.getById(id));
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除字典类型")
    public Result<Void> deleteType(@PathVariable Integer id) {
        SysDictType existing = sysDictTypeService.getById(id);
        if (existing == null) {
            return Result.notFound("字典类型不存在");
        }
        sysDictTypeService.removeById(id);
        return Result.success();
    }

    // ==================== 字典项 CRUD ====================

    @GetMapping("/items")
    @Operation(summary = "获取字典项", description = "传入typeCode时返回该类型下启用的字典项（按sort_order升序），不传则返回全部")
    public Result<List<SysDictItem>> getItems(@RequestParam(required = false) String typeCode) {
        if (StringUtils.isNotBlank(typeCode)) {
            return Result.success(sysDictItemService.getActiveItemsByTypeCode(typeCode));
        }
        return Result.success(sysDictItemService.list());
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "根据ID获取字典项")
    public Result<SysDictItem> getItemById(@PathVariable Integer id) {
        SysDictItem item = sysDictItemService.getById(id);
        if (item == null) {
            return Result.notFound("字典项不存在");
        }
        return Result.success(item);
    }

    @PostMapping("/items")
    @Operation(summary = "创建字典项")
    public Result<SysDictItem> createItem(@RequestBody SysDictItem sysDictItem) {
        if (StringUtils.isBlank(sysDictItem.getTypeCode())) {
            return Result.badRequest("字典类型编码不能为空");
        }
        SysDictType type = sysDictTypeService.getByTypeCode(sysDictItem.getTypeCode());
        if (type == null) {
            return Result.badRequest("字典类型编码不存在: " + sysDictItem.getTypeCode());
        }
        boolean duplicate = sysDictItemService.getAllItemsByTypeCode(sysDictItem.getTypeCode()).stream()
                .anyMatch(i -> i.getItemCode().equals(sysDictItem.getItemCode()));
        if (duplicate) {
            return Result.badRequest("该类型下字典项编码已存在: " + sysDictItem.getItemCode());
        }
        sysDictItemService.save(sysDictItem);
        return Result.success(sysDictItem);
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "更新字典项")
    public Result<SysDictItem> updateItem(@PathVariable Integer id, @RequestBody SysDictItem sysDictItem) {
        SysDictItem existing = sysDictItemService.getById(id);
        if (existing == null) {
            return Result.notFound("字典项不存在");
        }
        SysDictType type = sysDictTypeService.getByTypeCode(sysDictItem.getTypeCode());
        if (type == null) {
            return Result.badRequest("字典类型编码不存在: " + sysDictItem.getTypeCode());
        }
        boolean duplicate = sysDictItemService.getAllItemsByTypeCode(sysDictItem.getTypeCode()).stream()
                .anyMatch(i -> i.getItemCode().equals(sysDictItem.getItemCode()) && !i.getId().equals(id));
        if (duplicate) {
            return Result.badRequest("该类型下字典项编码已存在: " + sysDictItem.getItemCode());
        }
        sysDictItem.setId(id);
        sysDictItemService.updateById(sysDictItem);
        return Result.success(sysDictItemService.getById(id));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除字典项")
    public Result<Void> deleteItem(@PathVariable Integer id) {
        SysDictItem existing = sysDictItemService.getById(id);
        if (existing == null) {
            return Result.notFound("字典项不存在");
        }
        sysDictItemService.removeById(id);
        return Result.success();
    }
}
