package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private JsonFileUtil jsonFileUtil;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(jsonFileUtil.readUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        List<User> users = jsonFileUtil.readUsers();
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = jsonFileUtil.readUsers();
        List<User> result = users.stream()
                .filter(u -> u.getName() != null && u.getName().contains(name))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (!isValidUser(user)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<User> users = jsonFileUtil.readUsers();
        Long maxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        user.setId(maxId + 1);
        users.add(user);
        jsonFileUtil.writeUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    private boolean isValidUser(User user) {
        if (user == null) return false;
        if (user.getName() == null || user.getName().trim().isEmpty()) return false;
        if (user.getAge() == null || user.getAge() < 0) return false;
        return isValidEmail(user.getEmail());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        if (!isValidUser(userDetails)) {
            return ResponseEntity.badRequest().build();
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
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        List<User> users = jsonFileUtil.readUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            jsonFileUtil.writeUsers(users);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
