package com.recurse.portfolio.web;

import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@Log
public class DebuggingController {
    @GetMapping("/debug/headers")
    public ModelAndView showHeaders(
        @RequestHeader HttpHeaders headers
    ) {
        log.info(headers::toString);
        String renderedHeaders = headers.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(value -> entry.getKey() + ": " + value))
            .sorted()
            .collect(Collectors.joining("\n"));
        return new ModelAndView("debug/headers")
            .addObject("headers", renderedHeaders);
    }
}
