package com.tecnocampus.LS2.protube_back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @GetMapping("")
    public ModelAndView home() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        // This will forward all routes (except for those with a dot, which are likely static assets) to index.html
        return "forward:/index.html";
    }

}
