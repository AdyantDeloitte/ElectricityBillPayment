package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.HintDto
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.repository.HintRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HintService(private val hintRepository: HintRepository) {

    private val log = logger<HintService>()

    @Transactional(readOnly = true)
    fun getAllHints(): List<HintDto> {
        log.debug("Fetching all security hints")
        
        return try {
            val hints = hintRepository.findAll()
            val hintDtos = hints.map { it.toDto() }
            log.info("Retrieved {} security hints", hintDtos.size)
            hintDtos
        } catch (ex: Exception) {
            log.error("Error while fetching security hints", ex)
            throw ex
        }
    }
}
