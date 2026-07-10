package com.example.demo.controller.admin;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final List<Role> STAFF_ROLES = List.of(Role.SUPER_ADMIN, Role.MANAGER, Role.STAFF, Role.PARTNER);

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Quản lý nhân viên / Admin");
        model.addAttribute("activeMenu", "staff");
        model.addAttribute("staffList", userRepository.findAll().stream()
                .filter(u -> STAFF_ROLES.contains(u.getRole()))
                .collect(Collectors.toList()));
        return "admin/staff/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        prepareForm(model, new User(), "Tạo tài khoản admin");
        return "admin/staff/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, userRepository.findById(id).orElseThrow(), "Sửa tài khoản");
        return "admin/staff/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String fullName,
                       @RequestParam String email,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) String password,
                       @RequestParam Role role,
                       RedirectAttributes ra) {
        User user = id != null ? userRepository.findById(id).orElse(new User()) : new User();
        if (id == null && userRepository.existsByEmail(email)) {
            ra.addFlashAttribute("error", "Email đã tồn tại");
            return "redirect:/admin/staff/new";
        }
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        } else if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode("123456"));
        }
        userRepository.save(user);
        ra.addFlashAttribute("success", "Đã lưu tài khoản");
        return "redirect:/admin/staff";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.deleteById(id);
        ra.addFlashAttribute("success", "Đã xóa tài khoản");
        return "redirect:/admin/staff";
    }

    private void prepareForm(Model model, User user, String title) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("activeMenu", "staff");
        model.addAttribute("staff", user);
        model.addAttribute("roles", STAFF_ROLES);
    }
}
