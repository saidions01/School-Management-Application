package test.technique.SMA.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technique.SMA.entity.Student;
import test.technique.SMA.entity.StudentLevel;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Page<Student> findByLevel(StudentLevel level, Pageable pageable);
    Page<Student> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
