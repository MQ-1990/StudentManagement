package vn.edu.hcmut.cse.adse.lab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.hcmut.cse.adse.lab.entity.Student;
import vn.edu.hcmut.cse.adse.lab.service.StudentService;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentWebController {

    @Autowired
    private StudentService service;

    // Route: GET http://localhost:8080/students
    @GetMapping
    public String getAllStudents(@RequestParam(required = false) String keyword, Model model) {
        List<Student> students;

        // Advanced 7.1: Search implementation
        if (keyword != null && !keyword.isEmpty()) {
            students = service.searchByName(keyword);
        } else {
            students = service.getAll();
        }

        // Document model for View
        model.addAttribute("dsSinhVien", students);

        return "students"; // refers to resources/templates/students.html
    }

    // Route: GET http://localhost:8080/students/{id}
    @GetMapping("/{id}")
    public String getStudentDetail(@org.springframework.web.bind.annotation.PathVariable String id, Model model) {
        Student student = service.getById(id);
        if (student == null) {
            return "redirect:/students"; // Redirect back if not found
        }
        model.addAttribute("student", student);
        return "student-detail";
    }

    // Route: GET http://localhost:8080/students/new
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("action", "/students");
        model.addAttribute("isEdit", false);
        return "student-form";
    }

    // Route: POST http://localhost:8080/students
    @org.springframework.web.bind.annotation.PostMapping
    public String saveStudent(@org.springframework.web.bind.annotation.ModelAttribute("student") Student student,
            Model model) {
        // This is the Create route. So the student ID must NOT exist yet.
        Student existingStudentById = service.getById(student.getId());
        if (existingStudentById != null) {
            model.addAttribute("errorMessage",
                    "Mã số sinh viên " + student.getId() + " đã tồn tại trong hệ thống. Vui lòng nhập ID khác.");
            model.addAttribute("action", "/students");
            model.addAttribute("isEdit", false);
            return "student-form";
        }

        // Also check if email exists
        Student existingStudentByEmail = service.getByEmail(student.getEmail());
        if (existingStudentByEmail != null) {
            model.addAttribute("errorMessage",
                    "Email " + student.getEmail() + " đã được sử dụng. Vui lòng nhập Email khác.");
            model.addAttribute("action", "/students");
            model.addAttribute("isEdit", false);
            return "student-form";
        }

        service.saveStudent(student);
        return "redirect:/students";
    }

    // Route: GET http://localhost:8080/students/{id}/edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable String id, Model model) {
        Student student = service.getById(id);
        if (student == null) {
            return "redirect:/students";
        }
        model.addAttribute("student", student);
        model.addAttribute("action", "/students/" + id);
        model.addAttribute("isEdit", true);
        return "student-form";
    }

    // Route: POST http://localhost:8080/students/{id}
    @org.springframework.web.bind.annotation.PostMapping("/{id}")
    public String updateStudent(@org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.ModelAttribute("student") Student student,
            Model model) {

        // Check if email exists for ANOTHER student (not the one being updated)
        Student existingStudentByEmail = service.getByEmail(student.getEmail());
        if (existingStudentByEmail != null && !existingStudentByEmail.getId().equals(id)) {
            model.addAttribute("errorMessage",
                    "Email " + student.getEmail() + " đã được sử dụng bởi sinh viên khác. Vui lòng nhập Email khác.");
            model.addAttribute("action", "/students/" + id);
            model.addAttribute("isEdit", true);
            return "student-form";
        }

        student.setId(id);
        service.saveStudent(student);
        return "redirect:/students";
    }

    // Route: POST http://localhost:8080/students/{id}/delete
    @org.springframework.web.bind.annotation.PostMapping("/{id}/delete")
    public String deleteStudent(@org.springframework.web.bind.annotation.PathVariable String id) {
        service.deleteStudent(id);
        return "redirect:/students";
    }
}
