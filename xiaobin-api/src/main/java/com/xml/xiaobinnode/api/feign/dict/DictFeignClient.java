package com.xml.xiaobinnode.api.feign.dict;

import com.xml.xiaobinnode.common.dto.SysDictItemDTO;
import com.xml.xiaobinnode.common.dto.SysDictTypeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典 Feign（转发 user 模块 /v1/dict）
 */
@FeignClient(name = "xiaobin-user", contextId = "userDictFeignClient", path = "/dict")
public interface DictFeignClient {

    @GetMapping("/types")
    List<SysDictTypeDTO> getTypes();

    @GetMapping("/types/{id}")
    SysDictTypeDTO getTypeById(@PathVariable("id") Integer id);

    @PostMapping("/types")
    SysDictTypeDTO createType(@RequestBody SysDictTypeDTO dto);

    @PutMapping("/types/{id}")
    SysDictTypeDTO updateType(@PathVariable("id") Integer id, @RequestBody SysDictTypeDTO dto);

    @DeleteMapping("/types/{id}")
    void deleteType(@PathVariable("id") Integer id);

    @GetMapping("/items")
    List<SysDictItemDTO> getItems(@RequestParam(value = "typeCode", required = false) String typeCode);

    @GetMapping("/items/{id}")
    SysDictItemDTO getItemById(@PathVariable("id") Integer id);

    @PostMapping("/items")
    SysDictItemDTO createItem(@RequestBody SysDictItemDTO dto);

    @PutMapping("/items/{id}")
    SysDictItemDTO updateItem(@PathVariable("id") Integer id, @RequestBody SysDictItemDTO dto);

    @DeleteMapping("/items/{id}")
    void deleteItem(@PathVariable("id") Integer id);
}
