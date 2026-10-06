package com.anurag.emsb;

import com.anurag.emsb.entity.Employee;
import com.anurag.emsb.repository.EmployeeRepository;
import com.anurag.emsb.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EmsbApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@Test
	void contextLoads() {
	}

	@Test
	void updateChangesEmployeeSelectedByPrimaryKey() {
		employeeRepository.deleteAll();
		Employee target = employeeRepository.save(new Employee(null, "Target", "target@example.com", "Staff"));
		Employee other = employeeRepository.save(new Employee(null, "Other", "other@example.com", "Lead"));

		Employee update = new Employee(other.getId(), "Updated", "updated@example.com", "Manager");
		Employee result = employeeService.update(target.getId(), update);

		assertEquals(target.getId(), result.getId());
		assertEquals("Updated", employeeRepository.findById(target.getId()).orElseThrow().getName());
		assertEquals("Other", employeeRepository.findById(other.getId()).orElseThrow().getName());
	}

	@Test
	void missingEmployeeReturnsPlainTextNotFoundMessage() throws Exception {
		mockMvc.perform(get("/api/employees/987654321"))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Employee not found"));
	}

	@Test
	void homeRedirectsToRegistrationPage() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/index.html"));
	}

	@Test
	void deleteEmployeeReturnsConfirmationAndRemovesEmployee() throws Exception {
		Employee employee = employeeRepository.save(
				new Employee(null, "Delete Me", "delete@example.com", "Staff"));

		mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string("Employee deleted successfully"));

		assertEquals(false, employeeRepository.existsById(employee.getId()));
	}

	@Test
	void createEmployeeStoresEncodedPassword() {
		Employee employee = new Employee(null, "New Faculty", "new.faculty@example.com", "Faculty");
		employee.setPassword("registration-secret");

		Employee created = employeeService.create(employee);

		assertEquals(false, "registration-secret".equals(created.getPassword()));
		assertEquals(true, new BCryptPasswordEncoder().matches("registration-secret", created.getPassword()));
	}

}
