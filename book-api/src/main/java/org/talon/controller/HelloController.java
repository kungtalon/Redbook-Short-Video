package org.talon.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@RestController
public class HelloController {

    @ApiOperation(value = "hello - test api")
    @GetMapping("/hello")
    public Object Hello(){
        return "Hello Spring Boot!";
    }
}
