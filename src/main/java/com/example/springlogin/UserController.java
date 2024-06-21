package com.example.springlogin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/")
    public String loginPage(){
        return "index";
    }
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {


            if(email.equals("")&&password.equals("")){
                model.addAttribute("error", "Please Provide email address and password");
                return "index";
            }else{

                if(isValidEmailAddress(email)){

                    User user = userRepository.findByEmail(email);
                    if (user != null && user.getPassword().equals(password)) {
                        session.setAttribute("user", user);

                        String token = jwtUtil.generateToken(user.getEmail());
                        session.setAttribute("token", token);
                        return "redirect:/dashboard";
                    } else {
                        model.addAttribute("error", "Invalid email or password");
                        return "index";
                    }



                }else{

                    model.addAttribute("error", "Please Provide Correct email address");
                    return "index";
                }


            }






    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }





    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        String token = String.valueOf(session.getAttribute("token"));
        if (user != null && jwtUtil.validateToken(token) ) {
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }


    @GetMapping("/signup")
    public String signup() {

        return "signup";
    }

    @GetMapping("/setting")
    public String setting(HttpSession session, Model model) {


        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "setting";
        } else {
            return "redirect:/";
        }
    }



}
