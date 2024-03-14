package com.example.moim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/users-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    @PostMapping("signup")
    public String signup(@RequestBody UserDTO user){
        System.out.println(user);
        System.out.println("email!!!!" + user.getEmail());
        int result = 0;
        if(result == 1) {
            System.out.println("들어오면 안됨");
            return "redirect:http://localhost:5173/main";
        } else {
            System.out.println("들오이간 하나");
            return "redirect:http://localhost:5173";
        }
    }
    @PostMapping("signin")
    public void signin(@RequestBody UserDTO user) {
        System.out.println(user);
    }

}
