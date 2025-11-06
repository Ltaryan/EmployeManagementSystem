package serviceTests;

import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;
import org.example.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

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
    void testGetEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));

        // Act
        Employee result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getId(), result.getId());
        assertEquals(testEmployee.getName(), result.getName());
        assertEquals(testEmployee.getDepartment(), result.getDepartment());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeNotFound_ShouldThrowException() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.getEmployeeById(employeeId));

        assertEquals("Employee not found with id: " + employeeId, exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void testGetEmployeesByDepartment_ShouldReturnCorrectList() {
        // Arrange
        String department = "IT";
        when(employeeRepository.findByDepartment(department)).thenReturn(testEmployees);

        // Act
        List<Employee> result = employeeService.getEmployeesByDepartment(department);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testEmployees, result);
        verify(employeeRepository, times(1)).findByDepartment(department);
    }
}
