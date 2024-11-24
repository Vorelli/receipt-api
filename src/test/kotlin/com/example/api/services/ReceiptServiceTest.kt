package com.example.api.services

import com.example.api.dto.ItemDto
import com.example.api.dto.ReceiptDto
import com.example.api.repositories.ReceiptRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class ReceiptServiceTest {
    lateinit var receiptRepository: ReceiptRepository
    lateinit var receiptService: ReceiptService

    @BeforeEach
    fun setup() {
        receiptRepository = mock(ReceiptRepository::class.java)
        receiptService = ReceiptService(receiptRepository)
    }

    class ReceiptServicePointArgs : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            return Stream.of(
                // Basic case with single item
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Mountain Dew 12PK"
                                    price = "6.49"
                                }
                            )
                        total = "6.49"
                        purchaseTime = "13:01"
                        retailer = "Target"
                        purchaseDate = LocalDate.parse("2022-01-01")
                    },
                    12L // 6 points for retailer name + 6 points for odd day
                ),

                // Test round dollar amount and multiple of 0.25
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Gum"
                                    price = "2.00"
                                }
                            )
                        total = "2.00"
                        purchaseTime = "13:01"
                        retailer = "CVS"
                        purchaseDate = LocalDate.parse("2022-01-02")
                    },
                    79L // 3 points for retailer + 50 points for round dollar + 25 points for 0.25
                    // multiple + 1 point for "Gum" length 3
                ),

                // Test item pairs and description length multiple of 3
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Pop" // length 3
                                    price = "2.00"
                                },
                                ItemDto().apply {
                                    shortDescription = "Eggs" // length 4
                                    price = "3.00"
                                },
                                ItemDto().apply {
                                    shortDescription = "Milk" // length 4
                                    price = "4.00"
                                },
                                ItemDto().apply {
                                    shortDescription = "Ham" // length 3
                                    price = "5.00"
                                }
                            )
                        total = "14.00"
                        purchaseTime = "13:01"
                        retailer = "Shop"
                        purchaseDate = LocalDate.parse("2022-01-02")
                    },
                    91L // 4 points for retailer + 50 round dollar + 25 multiple of 0.25 + 10 points for 4
                    // items + 2 points for descriptions with length 3 (Pop and Ham each round up to a
                    // point each)
                ),

                // Test afternoon time bonus
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Coffee"
                                    price = "3.50"
                                }
                            )
                        total = "3.50"
                        purchaseTime = "15:00"
                        retailer = "Starbucks"
                        purchaseDate = LocalDate.parse("2022-01-03")
                    },
                    51L // Adding 9 points for retailer name
                    // Adding 25 points for multiple of 0.25
                    // Adding 1 point for item Coffee with length multiple of 3
                    // Adding 6 points for odd day of month
                    // Adding 10 points for purchase between 2:00pm and 4:00pm
                ),

                // Test complex case with multiple rules
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Tea" // length 3
                                    price = "5.00"
                                },
                                ItemDto().apply {
                                    shortDescription = "Cake Mix" // length 8
                                    price = "10.00"
                                },
                                ItemDto().apply {
                                    shortDescription = "Ice" // length 3
                                    price = "5.00"
                                }
                            )
                        total = "20.00"
                        purchaseTime = "15:30"
                        retailer = "Walmart123"
                        purchaseDate = LocalDate.parse("2022-01-05")
                    },
                    108L // Adding 10 points for retailer name
                    // Adding 50 points for round dollar
                    // Adding 25 points for multiple of 0.25
                    // Adding 5 points for 3 items
                    // Adding 1 point for item Tea with length multiple of 3
                    // Adding 1 point for item Ice with length multiple of 3
                    // Adding 6 points for odd day of month
                    // Adding 10 points for purchase between 2:00pm and 4:00pm
                ),

                // Example
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Mountain Dew 12PK"
                                    price = "6.49"
                                },
                                ItemDto().apply {
                                    shortDescription = "Emils Cheese Pizza"
                                    price = "12.25"
                                },
                                ItemDto().apply {
                                    shortDescription = "Knorr Creamy Chicken"
                                    price = "1.26"
                                },
                                ItemDto().apply {
                                    shortDescription = "Doritos Nacho Cheese"
                                    price = "3.35"
                                },
                                ItemDto().apply {
                                    shortDescription = "   Klarbrunn 12-PK 12 FL OZ  "
                                    price = "12.00"
                                }
                            )
                        total = "35.35"
                        purchaseDate = LocalDate.of(2022, 1, 1)
                        purchaseTime = "13:01"
                        retailer = "Target"
                    },
                    28L // Adding 6 points for retailer name
                    // Adding 10 points for 5 items
                    // Adding 3 point for item Emils Cheese Pizza with length multiple of 3
                    // Adding 3 point for item Klarbrunn 12-PK 12 FL OZ with length multiple of 3
                    // Adding 6 points for odd day of month
                ),
                // Example
                Arguments.of(
                    ReceiptDto().apply {
                        items =
                            listOf(
                                ItemDto().apply {
                                    shortDescription = "Gatorade"
                                    price = "2.25"
                                },
                                ItemDto().apply {
                                    shortDescription = "Gatorade"
                                    price = "2.25"
                                },
                                ItemDto().apply {
                                    shortDescription = "Gatorade"
                                    price = "2.25"
                                },
                                ItemDto().apply {
                                    shortDescription = "Gatorade"
                                    price = "2.25"
                                }
                            )
                        total = "9.00"
                        purchaseDate = LocalDate.of(2022, 3, 20)
                        purchaseTime = "14:33"
                        retailer = "M&M Corner Market"
                    },
                    109L // Adding 14 points for retailer name
                    // Adding 50 points for round dollar amount
                    // Adding 25 points for multiple of 0.25
                    // Adding 10 points for 4 items
                    // Adding 10 points for purchase between 2:00pm and 4:00pm
                )
            )
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ReceiptServicePointArgs::class)
    fun testPointCalculation(body: ReceiptDto, expectedPoints: Long) {
        `when`(receiptRepository.receiptsIdGet("testId")).thenReturn(body)

        val actualPoints = receiptService.receiptsIdPointsGet("testId")

        Assertions.assertEquals(expectedPoints, actualPoints.points)
    }
}
