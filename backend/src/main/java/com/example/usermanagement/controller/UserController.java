package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private JsonFileUtil jsonFileUtil;

    @GetMapping
    public List<User> getAllUsers() {
        return jsonFileUtil.readUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        List<User> users = jsonFileUtil.readUsers();
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String name) {
        List<User> users = jsonFileUtil.readUsers();
        return users.stream()
                .filter(u -> u.getName().contains(name))
                .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        List<User> users = jsonFileUtil.readUsers();
        Long maxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        user.setId(maxId + 1);
        users.add(user);
        jsonFileUtil.writeUsers(users);
        return user;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        if (!isValidEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        List<User> users = jsonFileUtil.readUsers();
        Optional<User> userOptional = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userDetails.getName());
            user.setAge(userDetails.getAge());
            user.setEmail(userDetails.getEmail());
            jsonFileUtil.writeUsers(users);
            return user;
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        List<User> users = jsonFileUtil.readUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            jsonFileUtil.writeUsers(users);
        }
        return removed;
    }
}
