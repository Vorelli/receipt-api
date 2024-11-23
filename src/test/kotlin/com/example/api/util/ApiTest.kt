package com.example.api.util

import com.example.api.ApiApplication
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [ApiApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ApiTest {
    @LocalServerPort private lateinit var apiPort: Number

    @BeforeEach
    fun setup() {
        RestAssured.port = this.apiPort.toInt()
        RestAssured.baseURI = "http://localhost:" + RestAssured.port
        // Add any additional RestAssured configurations here
    }
}
