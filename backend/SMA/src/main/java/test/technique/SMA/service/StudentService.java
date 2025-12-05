package test.technique.SMA.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import test.technique.SMA.dto.StudentDTO;
import test.technique.SMA.entity.Student;
import test.technique.SMA.entity.StudentLevel;
import test.technique.SMA.exception.ResourceAlreadyExistsException;
import test.technique.SMA.exception.ResourceNotFoundException;
import test.technique.SMA.repository.StudentRepository;
import java.util.List;

@Service
@Slf4j
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (studentRepository.findByUsername(studentDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Student with username " + studentDTO.getUsername() + " already exists");
        }

        Student student = Student.builder()
            .username(studentDTO.getUsername())
            .level(studentDTO.getLevel())
            .build();

        Student savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student with id " + id + " not found"));
        return mapToDTO(student);
    }

    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
            .map(this::mapToDTO);
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student with id " + id + " not found"));

        // Check if new username already exists and is different from current
        if (!student.getUsername().equals(studentDTO.getUsername()) &&
            studentRepository.findByUsername(studentDTO.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username " + studentDTO.getUsername() + " already exists");
        }

        student.setUsername(studentDTO.getUsername());
        student.setLevel(studentDTO.getLevel());

        Student updatedStudent = studentRepository.save(student);
        return mapToDTO(updatedStudent);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student with id " + id + " not found");
        }
        studentRepository.deleteById(id);
    }

    public Page<StudentDTO> searchStudentsByUsername(String username, Pageable pageable) {
        return studentRepository.findByUsernameContainingIgnoreCase(username, pageable)
            .map(this::mapToDTO);
    }

    public Page<StudentDTO> filterStudentsByLevel(StudentLevel level, Pageable pageable) {
        return studentRepository.findByLevel(level, pageable)
            .map(this::mapToDTO);
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
            .map(this::mapToDTO)
            .toList();
    }

    private StudentDTO mapToDTO(Student student) {
        return StudentDTO.builder()
            .id(student.getId())
            .username(student.getUsername())
            .level(student.getLevel())
            .build();
    }
}
