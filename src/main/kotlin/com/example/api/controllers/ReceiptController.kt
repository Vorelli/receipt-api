package com.example.api.controllers

import com.example.api.controller.ReceiptsApi
import com.example.api.dto.ReceiptDto
import com.example.api.dto.ReceiptsIdPointsGet200ResponseDto
import com.example.api.dto.ReceiptsProcessPost200ResponseDto
import com.example.api.services.ReceiptService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ReceiptController(private val receiptService: ReceiptService) : ReceiptsApi {
    override fun receiptsIdPointsGet(id: String): ResponseEntity<ReceiptsIdPointsGet200ResponseDto> {
        return ResponseEntity.ok(receiptService.receiptsIdPointsGet(id))
    }

    override fun receiptsProcessPost(
        receiptDto: ReceiptDto
    ): ResponseEntity<ReceiptsProcessPost200ResponseDto> {
        return ResponseEntity.status(201).body(receiptService.receiptsProcessPost(receiptDto))
    }
}
