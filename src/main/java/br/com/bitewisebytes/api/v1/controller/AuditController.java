package br.com.bitewisebytes.api.v1.controller;


import br.com.bitewisebytes.audit.AuditTransactionDto;
import br.com.bitewisebytes.audit.AuditTransactionRepository;
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
