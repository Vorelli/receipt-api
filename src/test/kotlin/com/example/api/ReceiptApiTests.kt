package com.example.api

import com.example.api.dto.ItemDto
import com.example.api.dto.ReceiptDto
import com.example.api.dto.ReceiptsIdPointsGet200ResponseDto
import com.example.api.dto.ReceiptsProcessPost200ResponseDto
import com.example.api.util.ApiTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.time.LocalDate
import java.util.stream.Stream

class ReceiptApiTests : ApiTest() {
    @Test
    fun testNotFound() {
        given().pathParam("id", "12345").get("/receipts/{id}/points").then().statusCode(404)
    }

    @ParameterizedTest
    @ArgumentsSource(ReceiptProvider::class)
    fun testReceiptCR(body: ReceiptDto, expectedPoints: Long) {
        val response =
            given()
                .body(body)
                .contentType("application/json")
                .post("/receipts/process")
                .then()
                .body("id", notNullValue())
                .statusCode(201)
                .extract()
                .body()
                .`as`(ReceiptsProcessPost200ResponseDto::class.java)
        Assertions.assertNotNull(response)
        Assertions.assertNotNull(response.id)

        val pointsResponse =
            given()
                .pathParam("id", response.id)
                .get("/receipts/{id}/points")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ReceiptsIdPointsGet200ResponseDto::class.java)
        Assertions.assertNotNull(pointsResponse)
        Assertions.assertEquals(expectedPoints, pointsResponse.points)
    }

    class ReceiptProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
            Stream.of(
                Arguments.of(
                    ReceiptDto()
                        .items(
                            listOf(
                                ItemDto().shortDescription("Mountain Dew 12PK").price("6.49"),
                                ItemDto().shortDescription("Emils Cheese Pizza").price("12.25"),
                                ItemDto().shortDescription("Knorr Creamy Chicken").price("1.26"),
                                ItemDto().shortDescription("Doritos Nacho Cheese").price("3.35"),
                                ItemDto()
                                    .shortDescription("   Klarbrunn 12-PK 12 FL OZ  ")
                                    .price("12.00")
                            )
                        )
                        .total("35.35")
                        .purchaseDate(LocalDate.of(2022, 1, 1))
                        .purchaseTime("13:01")
                        .retailer("Target"),
                    28L
                ),
                Arguments.of(
                    ReceiptDto()
                        .items(
                            listOf(
                                ItemDto().shortDescription("Gatorade").price("2.25"),
                                ItemDto().shortDescription("Gatorade").price("2.25"),
                                ItemDto().shortDescription("Gatorade").price("2.25"),
                                ItemDto().shortDescription("Gatorade").price("2.25")
                            )
                        )
                        .total("9.00")
                        .purchaseDate(LocalDate.of(2022, 3, 20))
                        .purchaseTime("14:33")
                        .retailer("M&M Corner Market"),
                    109L
                )
            )
    }
}
