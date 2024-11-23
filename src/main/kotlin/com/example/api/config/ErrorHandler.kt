package com.example.api.config

import jakarta.ws.rs.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class ErrorHandler {
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }
}
