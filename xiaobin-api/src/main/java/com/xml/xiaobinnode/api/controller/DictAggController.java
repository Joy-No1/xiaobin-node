package com.xml.xiaobinnode.api.controller;

import com.xml.xiaobinnode.api.feign.dict.DictFeignClient;
import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.SysDictItemDTO;
import com.xml.xiaobinnode.common.dto.SysDictTypeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聚合-字典
 */
@Tag(name = "聚合-字典")
@RestController
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class DictAggController {

    private final DictFeignClient dictFeignClient;

    @GetMapping("/types")
    @Operation(summary = "字典类型列表")
    public Result<List<SysDictTypeDTO>> getTypes() {
        return Result.success(dictFeignClient.getTypes());
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "字典类型详情")
    public Result<SysDictTypeDTO> getTypeById(@PathVariable Integer id) {
        return Result.success(dictFeignClient.getTypeById(id));
    }

    @PostMapping("/types")
    @Operation(summary = "新增字典类型")
    public Result<SysDictTypeDTO> createType(@RequestBody SysDictTypeDTO dto) {
        return Result.success(dictFeignClient.createType(dto));
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "修改字典类型")
    public Result<SysDictTypeDTO> updateType(@PathVariable Integer id, @RequestBody SysDictTypeDTO dto) {
        return Result.success(dictFeignClient.updateType(id, dto));
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除字典类型")
    public Result<Void> deleteType(@PathVariable Integer id) {
        dictFeignClient.deleteType(id);
        return Result.success();
    }

    @GetMapping("/items")
    @Operation(summary = "字典项列表")
    public Result<List<SysDictItemDTO>> getItems(@RequestParam(required = false) String typeCode) {
        return Result.success(dictFeignClient.getItems(typeCode));
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "字典项详情")
    public Result<SysDictItemDTO> getItemById(@PathVariable Integer id) {
        return Result.success(dictFeignClient.getItemById(id));
    }

    @PostMapping("/items")
    @Operation(summary = "新增字典项")
    public Result<SysDictItemDTO> createItem(@RequestBody SysDictItemDTO dto) {
        return Result.success(dictFeignClient.createItem(dto));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "修改字典项")
    public Result<SysDictItemDTO> updateItem(@PathVariable Integer id, @RequestBody SysDictItemDTO dto) {
        return Result.success(dictFeignClient.updateItem(id, dto));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除字典项")
    public Result<Void> deleteItem(@PathVariable Integer id) {
        dictFeignClient.deleteItem(id);
        return Result.success();
    }
}
