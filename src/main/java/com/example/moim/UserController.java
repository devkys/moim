package com.example.moim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @GetMapping("api/getList")
    public void signup() {
        System.out.println("getList api");
    }
}
