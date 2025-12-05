package test.technique.SMA.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import test.technique.SMA.dto.StudentDTO;
import test.technique.SMA.entity.Student;
import test.technique.SMA.entity.StudentLevel;
import test.technique.SMA.exception.ResourceAlreadyExistsException;
import test.technique.SMA.exception.ResourceNotFoundException;
import test.technique.SMA.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private StudentDTO testStudentDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        testStudent = Student.builder()
            .id(1L)
            .username("john_doe")
            .level(StudentLevel.FIRST_YEAR)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        testStudentDTO = StudentDTO.builder()
            .id(1L)
            .username("john_doe")
            .level(StudentLevel.FIRST_YEAR)
            .build();
    }

    @Test
    void testCreateStudentSuccess() {
        // Arrange
        when(studentRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        StudentDTO result = studentService.createStudent(testStudentDTO);

        // Assert
        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        assertEquals(StudentLevel.FIRST_YEAR, result.getLevel());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testCreateStudentAlreadyExists() {
        // Arrange
        when(studentRepository.findByUsername("john_doe")).thenReturn(Optional.of(testStudent));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            studentService.createStudent(testStudentDTO);
        });
    }

    @Test
    void testGetStudentByIdSuccess() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // Act
        StudentDTO result = studentService.getStudentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    void testGetStudentByIdNotFound() {
        // Arrange
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(1L);
        });
    }

    @Test
    void testGetAllStudentsWithPagination() {
        // Arrange
        Page<Student> studentPage = new PageImpl<>(Collections.singletonList(testStudent), pageable, 1);
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        // Act
        Page<StudentDTO> result = studentService.getAllStudents(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("john_doe", result.getContent().get(0).getUsername());
    }

    @Test
    void testUpdateStudentSuccess() {
        // Arrange
        StudentDTO updateDTO = StudentDTO.builder()
            .username("jane_doe")
            .level(StudentLevel.SECOND_YEAR)
            .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.findByUsername("jane_doe")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        StudentDTO result = studentService.updateStudent(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testUpdateStudentNotFound() {
        // Arrange
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.updateStudent(1L, testStudentDTO);
        });
    }

    @Test
    void testDeleteStudentSuccess() {
        // Arrange
        when(studentRepository.existsById(1L)).thenReturn(true);

        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void testDeleteStudentNotFound() {
        // Arrange
        when(studentRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.deleteStudent(1L);
        });
    }

    @Test
    void testSearchStudentsByUsername() {
        // Arrange
        Page<Student> studentPage = new PageImpl<>(Collections.singletonList(testStudent), pageable, 1);
        when(studentRepository.findByUsernameContainingIgnoreCase("john", pageable))
            .thenReturn(studentPage);

        // Act
        Page<StudentDTO> result = studentService.searchStudentsByUsername("john", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFilterStudentsByLevel() {
        // Arrange
        Page<Student> studentPage = new PageImpl<>(Collections.singletonList(testStudent), pageable, 1);
        when(studentRepository.findByLevel(StudentLevel.FIRST_YEAR, pageable))
            .thenReturn(studentPage);

        // Act
        Page<StudentDTO> result = studentService.filterStudentsByLevel(StudentLevel.FIRST_YEAR, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
