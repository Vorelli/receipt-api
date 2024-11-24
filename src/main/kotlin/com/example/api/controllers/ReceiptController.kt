package com.example.api.controllers

import com.example.api.controller.ReceiptsApi
import com.example.api.dto.ReceiptDto
import com.example.api.dto.ReceiptsIdPointsGet200ResponseDto
import com.example.api.dto.ReceiptsProcessPost200ResponseDto
import com.example.api.services.ReceiptService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ReceiptController(private val receiptService: ReceiptService) : ReceiptsApi {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun receiptsIdPointsGet(id: String): ResponseEntity<ReceiptsIdPointsGet200ResponseDto> {
        val pointsDto = receiptService.receiptsIdPointsGet(id)
        log.info("Returning points for receipt with id $id: ${pointsDto.points}")
        return ResponseEntity.ok(pointsDto)
    }

    override fun receiptsProcessPost(
        receiptDto: ReceiptDto
    ): ResponseEntity<ReceiptsProcessPost200ResponseDto> {
        val receiptId = receiptService.receiptsProcessPost(receiptDto)
        log.info("Returning receipt ID from retailer (${receiptDto.retailer}): ${receiptId.id}")
        return ResponseEntity.status(201).body(receiptId)
    }
}
