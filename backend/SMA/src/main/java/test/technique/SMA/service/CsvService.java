package test.technique.SMA.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import test.technique.SMA.dto.StudentDTO;
import test.technique.SMA.entity.StudentLevel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CsvService {

    @Autowired
    private StudentService studentService;

    public byte[] exportStudents() throws IOException {
        List<StudentDTO> students = studentService.getAllStudents();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader("ID", "Username", "Level")
            .build();

        CSVPrinter printer = new CSVPrinter(osw, csvFormat);

        for (StudentDTO student : students) {
            printer.printRecord(student.getId(), student.getUsername(), student.getLevel());
        }

        printer.flush();
        printer.close();

        return out.toByteArray();
    }

    public List<StudentDTO> importStudents(MultipartFile file) throws IOException {
        List<StudentDTO> students = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

        CSVParser csvParser = csvFormat.parse(reader);

        for (CSVRecord record : csvParser) {
            try {
                String username = record.get("Username");
                String level = record.get("Level");

                StudentDTO studentDTO = StudentDTO.builder()
                    .username(username)
                    .level(StudentLevel.valueOf(level))
                    .build();

                students.add(studentDTO);
            } catch (Exception e) {
                log.error("Error parsing CSV record: {}", e.getMessage());
            }
        }

        csvParser.close();
        return students;
    }
}
