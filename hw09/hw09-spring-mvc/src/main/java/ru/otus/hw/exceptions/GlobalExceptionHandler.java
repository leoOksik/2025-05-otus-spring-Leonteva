package ru.otus.hw.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handeNotFoundException() {
        return new ModelAndView("custom-error",
            "errorText", "Object not found");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
            "You can't delete. Author has books");
        return "redirect:/authors";
    }

}
