package com.example.api.services

import com.example.api.dto.ReceiptDto
import com.example.api.dto.ReceiptsIdPointsGet200ResponseDto
import com.example.api.dto.ReceiptsProcessPost200ResponseDto
import com.example.api.repositories.ReceiptRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class ReceiptService(private val receiptRepository: ReceiptRepository) {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    private val pointsCache = mutableMapOf<String, Long>()

    fun receiptsIdPointsGet(id: String): ReceiptsIdPointsGet200ResponseDto {
        return ReceiptsIdPointsGet200ResponseDto()
            .points(pointsCache.getOrPut(id) { calculatePoints(receiptRepository.receiptsIdGet(id)) })
    }

    fun receiptsProcessPost(receiptDto: ReceiptDto): ReceiptsProcessPost200ResponseDto {
        return receiptRepository.receiptsProcessPost(receiptDto)
    }

    private fun calculatePoints(receipt: ReceiptDto): Long {
        var points: Long = 0

        // One point for every alphanumeric character in the retailer name
        val retailerPoints = receipt.retailer.filter { it.isLetterOrDigit() }.count()
        log.debug("Adding $retailerPoints points for retailer name")
        points += retailerPoints

        // 50 points if the total is a round dollar amount with no cents
        if (receipt.total.toDouble() % 1 == 0.0) {
            log.debug("Adding 50 points for round dollar amount")
            points += 50
        }

        // 25 points if the total is a multiple of 0.25
        if (receipt.total.toDouble() % 0.25 == 0.0) {
            log.debug("Adding 25 points for multiple of 0.25")
            points += 25
        }

        // 5 points for every two items on the receipt
        val itemPairPoints = (receipt.items.size / 2) * 5
        log.debug("Adding $itemPairPoints points for ${receipt.items.size} items")
        points += itemPairPoints

        // For every item, if the trimmed length of the item description is a multiple of 3
        // multiply the price by 0.2 and round up to the nearest integer
        for (item in receipt.items) {
            val trimmedLength = item.shortDescription.trim().length
            if (trimmedLength % 3 == 0) {
                log.debug(
                    "Adding ${Math.ceil(item.price.toDouble() * 0.2).toInt()} points for item ${item.shortDescription} with length multiple of 3"
                )
                points += Math.ceil(item.price.toDouble() * 0.2).toInt()
            }
        }

        // 6 points if the day in the purchase date is odd
        val purchaseDate =
            LocalDateTime.of(
                receipt.purchaseDate,
                LocalTime.parse(receipt.purchaseTime, DateTimeFormatter.ofPattern("HH:mm"))
            )
        if (purchaseDate.dayOfMonth % 2 != 0) {
            log.debug("Adding 6 points for odd day of month")
            points += 6
        }

        // 10 points if the time of purchase is after 2:00pm and before 4:00pm
        val purchaseTime = purchaseDate.toLocalTime()
        if (purchaseTime.isAfter(LocalTime.of(14, 0)) && purchaseTime.isBefore(LocalTime.of(16, 0))) {
            log.debug("Adding 10 points for purchase between 2:00pm and 4:00pm")
            points += 10
        }

        return points
    }
}
