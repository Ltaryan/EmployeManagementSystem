package controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.EmployeeController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import org.example.entity.Employee;
import org.example.service.EmployeeService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = org.example.EmployeeManagementApplication.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee testEmployee;
    private List<Employee> testEmployees;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(1L, "John Doe", "IT");
        testEmployees = Arrays.asList(
                new Employee(1L, "John Doe", "IT"),
                new Employee(2L, "Jane Smith", "IT")
        );
    }

    @Test
    void testGetEmployeeById_WhenEmployeeExists_ShouldReturnValidJsonResponse() throws Exception {
        // Arrange
        Long employeeId = 1L;
        when(employeeService.getEmployeeById(employeeId)).thenReturn(testEmployee);

        // Act & Assert
        mockMvc.perform(get("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testEmployee.getId()))
                .andExpect(jsonPath("$.name").value(testEmployee.getName()))
                .andExpect(jsonPath("$.department").value(testEmployee.getDepartment()));

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeNotFound_ShouldReturnNotFoundError() throws Exception {
        // Arrange
        Long employeeId = 999L;
        when(employeeService.getEmployeeById(employeeId))
                .thenThrow(new RuntimeException("Employee not found with id: " + employeeId));

        // Act & Assert
        mockMvc.perform(get("/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    void testGetEmployeesByDepartment_ShouldReturnCorrectListInJson() throws Exception {
        // Arrange
        String department = "IT";
        when(employeeService.getEmployeesByDepartment(department)).thenReturn(testEmployees);

        // Act & Assert
        mockMvc.perform(get("/employees/department/{dept}", department)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testEmployees.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(testEmployees.get(0).getName()))
                .andExpect(jsonPath("$[0].department").value(testEmployees.get(0).getDepartment()))
                .andExpect(jsonPath("$[1].id").value(testEmployees.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(testEmployees.get(1).getName()))
                .andExpect(jsonPath("$[1].department").value(testEmployees.get(1).getDepartment()));

        verify(employeeService, times(1)).getEmployeesByDepartment(department);
    }
}
