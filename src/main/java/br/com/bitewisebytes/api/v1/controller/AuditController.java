package br.com.bitewisebytes.api.v1.controller;


import br.com.bitewisebytes.audit.dto.AuditTransactionDto;
import br.com.bitewisebytes.audit.repository.AuditTransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kashflow/api/v1/audit")
public class AuditController {

    private final AuditTransactionRepository repository;

    public AuditController(AuditTransactionRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "Retrieve all audit transaction logs",
            description = "This endpoint fetches all transaction logs from the audit repository, providing details like transaction ID, wallet ID, type, amount, status, timestamp, and source wallet information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving the audit logs")
    })
    @GetMapping
    public List<AuditTransactionDto> getAllAuditLogs() {
        return repository.findAll().stream().map(tx -> {
            AuditTransactionDto dto = new AuditTransactionDto();
            dto.setTransactionId(tx.getTransactionId());
            dto.setWalletId(tx.getWalletId());
            dto.setType(tx.getType());
            dto.setAmount(tx.getAmount());
            dto.setStatus(tx.getStatus());
            dto.setTimestamp(tx.getTimestamp());
            dto.setFromWalletFrom(tx.getWalletIdFrom());
            dto.setTransactionId(tx.getTransactionId());
            return dto;
        }).collect(Collectors.toList());
    }


    @Operation(
            summary = "Retrieve audit transaction logs by wallet ID",
            description = "This endpoint retrieves all audit transaction logs associated with a specific wallet ID, providing details like transaction ID, wallet ID, type, amount, status, timestamp, and wallet source information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit transaction logs retrieved successfully for the provided wallet ID"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid wallet ID format"),
            @ApiResponse(responseCode = "404", description = "No audit logs found for the provided wallet ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error while retrieving the audit logs")
    })
    @GetMapping("/{walletId}")
    public List<AuditTransactionDto> getByWalletId(@PathVariable String walletId) {
        List<AuditTransactionDto> collectDto = repository.findAllByWalletIdAsDto(walletId).stream()
                .map(tx -> {
                    AuditTransactionDto dto = new AuditTransactionDto();
                    dto.setTransactionId(tx.getTransactionId());
                    dto.setWalletId(tx.getWalletId());
                    dto.setType(tx.getType());
                    dto.setAmount(tx.getAmount());
                    dto.setStatus(tx.getStatus());
                    dto.setTimestamp(tx.getTimestamp());
                    dto.setToWalletFrom(tx.getToWalletFrom());
                    dto.setFromWalletFrom(tx.getFromWalletFrom());
                    return dto;
                })
                .collect(Collectors.toList());
        return collectDto;
    }

}
