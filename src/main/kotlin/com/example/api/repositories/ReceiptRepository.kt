package com.example.api.repositories

import com.example.api.dto.ItemDto
import com.example.api.dto.ReceiptDto
import com.example.api.dto.ReceiptsProcessPost200ResponseDto
import jakarta.ws.rs.NotFoundException
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

data class ReceiptWithId(
    val id: String,
    private val retailer: String,
    private val purchaseDate: LocalDate,
    private val purchaseTime: String,
    private val items: List<ItemDto>,
    private val total: String
) : ReceiptDto(retailer, purchaseDate, purchaseTime, items, total)

@Repository
class ReceiptRepository {
    val receipts = mutableMapOf<String, ReceiptWithId>()

    fun receiptsIdGet(id: String): ReceiptDto {
        return receipts[id] ?: throw NotFoundException("Receipt with id $id not found")
    }

    fun receiptsProcessPost(receiptDto: ReceiptDto): ReceiptsProcessPost200ResponseDto {
        val id = UUID.randomUUID().toString()
        val newReceipt =
            ReceiptWithId(
                id,
                receiptDto.retailer,
                receiptDto.purchaseDate,
                receiptDto.purchaseTime,
                receiptDto.items,
                receiptDto.total
            )
        receipts[id] = newReceipt
        return ReceiptsProcessPost200ResponseDto().id(id)
    }
}
