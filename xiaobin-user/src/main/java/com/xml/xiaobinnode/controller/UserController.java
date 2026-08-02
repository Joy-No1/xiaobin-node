package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.dto.UserDTO;
import com.xml.xiaobinnode.entity.User;
import com.xml.xiaobinnode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口", description = "个人信息管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息（含地址）")
    public Result<UserDTO> getCurrentUser() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.getCurrentUserDTO(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新个人信息", description = "同时更新用户信息和地址，地址数据放在 location 字段中")
    public Result<User> updateUser(@RequestBody UserDTO userDTO) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.updateUser(userId, userDTO.getUser(), userDTO.getLocation()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查看用户主页（含地址）")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        com.xml.xiaobinnode.dto.UserDTO dto = userService.getUserDTOById(id);
        if (dto == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(dto);
    }

    @GetMapping("/search")
    @Operation(summary = "根据手机号查找用户")
    public Result<User> searchByPhone(@RequestParam String phone) {
        User user = userService.getUserByPhone(phone);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }
}
