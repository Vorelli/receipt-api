package com.example.api.config

import jakarta.ws.rs.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class ErrorHandler {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<String> {
        log.warn("Not found: ${ex.message}")
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }
}
