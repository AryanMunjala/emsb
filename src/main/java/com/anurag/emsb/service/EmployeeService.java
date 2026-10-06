package com.anurag.emsb.service;

import com.anurag.emsb.entity.Employee;
import com.anurag.emsb.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public Employee create(Employee employee) {
        employee.setId(null);
        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
        return employeeRepository.save(employee);
    }

    public Employee update(int id, Employee employee) {
        Employee existing = findById(id);
        existing.setName(employee.getName());
        existing.setEmail(employee.getEmail());
        existing.setRole(employee.getRole());
        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
        return employeeRepository.save(existing);
    }

    public void delete(int id) {
        employeeRepository.delete(findById(id));
    }
}