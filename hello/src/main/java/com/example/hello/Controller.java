package com.example.hello;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    @Autowired
    StudentRepo studentRepo;

    @GetMapping(value = "/", produces = "application/json")
    public String hello(){
        return "{"+"\"message\""+": "+"\"hello, world!\""+"}";
    }

    @GetMapping(value = "/students")
    public List<Student> getStudents(){
        return studentRepo.findAll();
    }

    @PostMapping(value = "/students", consumes = "application/json" )
    public void addStudent(@RequestBody Student student){
        studentRepo.save(student);
    }
}
