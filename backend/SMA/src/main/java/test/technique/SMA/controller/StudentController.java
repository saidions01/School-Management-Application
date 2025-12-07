package test.technique.SMA.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import test.technique.SMA.dto.ApiResponse;
import test.technique.SMA.dto.StudentDTO;
import test.technique.SMA.entity.StudentLevel;
import test.technique.SMA.service.StudentService;
import test.technique.SMA.service.CsvService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "API for student management")
@SecurityRequirement(name = "Bearer Token")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CsvService csvService;

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve all students with pagination")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> getAllStudents(Pageable pageable) {
        Page<StudentDTO> students = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve a specific student by their ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
    }

    @PostMapping
    @Operation(summary = "Create a new student", description = "Create a new student record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Student already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Student created successfully", createdStudent, 201));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student", description = "Update an existing student record")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updatedStudent));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Delete a student record by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by username", description = "Search for students by username pattern")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> searchStudents(@RequestParam String username, Pageable pageable) {
        Page<StudentDTO> students = studentService.searchStudentsByUsername(username, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", students));
    }

    @GetMapping("/filter/level")
    @Operation(summary = "Filter students by level", description = "Filter students by their study level")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Filter completed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> filterByLevel(@RequestParam StudentLevel level, Pageable pageable) {
        Page<StudentDTO> students = studentService.filterStudentsByLevel(level, pageable);
        return ResponseEntity.ok(ApiResponse.success("Filter completed successfully", students));
    }

    @GetMapping("/export")
    @Operation(summary = "Export students to CSV", description = "Export all students data to a CSV file")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV exported successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        byte[] csvData = csvService.exportStudents();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(csvData);
    }

    @PostMapping("/import")
    @Operation(summary = "Import students from CSV", description = "Import students data from a CSV file")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV imported successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid CSV file"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<?>> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        List<StudentDTO> students = csvService.importStudents(file);
        return ResponseEntity.ok(ApiResponse.success("CSV imported successfully", students));
    }
}
