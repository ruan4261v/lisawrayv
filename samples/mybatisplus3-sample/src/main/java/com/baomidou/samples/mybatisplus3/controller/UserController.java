package com.baomidou.samples.mybatisplus3.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.samples.mybatisplus3.entity.User;
import com.baomidou.samples.mybatisplus3.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/users")
@Api(tags = "用户列表")
public class UserController {

    private static final Random RANDOM = new Random();

    @Autowired
    private UserService userService;

    @Resource(name = "userSlaveServiceImpl")
    UserService userSlaveService;

    @GetMapping("lambda/master/getOne")
    public User lambdaMasterGetOne() {
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
    }

    @GetMapping("lambda/slave/getOne")
    public User lambdaSlaveGetOne() {
        return userSlaveService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
    }

    @GetMapping("master")
    public List<User> users1() {
        return userService.selectUsers();
    }

    @GetMapping("slave")
    public List<User> users2() {
        return userSlaveService.selectUsers();
    }

    @PostMapping
    public User addUser() {
        User user = new User();
        user.setName("测试用户" + RANDOM.nextInt());
        user.setAge(RANDOM.nextInt(100));
        userService.addUser(user);
        return user;
    }

    @DeleteMapping("{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "成功删除用户" + id;
    }
}
