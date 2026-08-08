package com.xml.xiaobinnode.controller;

import com.xml.xiaobinnode.common.dto.Result;
import com.xml.xiaobinnode.common.dto.UserVO;
import com.xml.xiaobinnode.common.util.UserContext;
import com.xml.xiaobinnode.dto.UserDTO;
import com.xml.xiaobinnode.dto.UserProfileVO;
import com.xml.xiaobinnode.entity.User;
import com.xml.xiaobinnode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "用户接口", description = "个人信息管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息（含地址）")
    public Result<UserVO> getCurrentUser() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.getUserVOById(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新个人信息", description = "同时更新用户信息和地址，地址数据放在 location 字段中")
    public Result<User> updateUser(@RequestBody UserDTO userDTO) {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userService.updateUser(userId, userDTO.getUser(), userDTO.getLocation()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查看用户主页（含地址、关注状态）")
    public Result<UserProfileVO> getUserById(@PathVariable Long id) {
        Long currentUserId = Long.valueOf(UserContext.getUserId());
        UserProfileVO vo = userService.getUserProfileVO(currentUserId, id);
        if (vo == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/search")
    @Operation(summary = "根据手机号查找用户")
    public Result<UserVO> searchByPhone(@RequestParam String phone) {
        User user = userService.getUserByPhone(phone);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(userService.getUserVOById(user.getId()));
    }

    @GetMapping("/batch")
    @Operation(summary = "批量获取用户信息", description = "根据用户ID批量获取用户信息，ids用逗号分隔")
    public Result<List<UserVO>> getUsersByIds(@RequestParam String ids) {
        List<Long> userIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return Result.success(userService.getUserVOsByIds(userIds));
    }
}
