package br.com.bitewisebytes.api.v1.controller;

import br.com.bitewisebytes.model.requestDto.*;
import br.com.bitewisebytes.model.responseDto.TransactionDetailDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import br.com.bitewisebytes.model.validation.WalletRequestValidator;
import br.com.bitewisebytes.service.TransactionService;
import br.com.bitewisebytes.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/kashflow/api/v1/wallet")
public class KashFlowController {


    private final WalletService walletService;
    private final TransactionService transactionService;

    public KashFlowController(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkHealth(){
        return ResponseEntity.ok("API CKASHFLOW HEALTH - OK");
    }

    @PostMapping("/create")
    public ResponseEntity<WalletResponseDto> createWallet(@RequestBody WalletRequestDto walletRequestDto) {
        WalletRequestValidator.validate(walletRequestDto);
        if (walletRequestDto == null) {
            return ResponseEntity.badRequest().body(null);
        }
        WalletResponseDto walletResponse = walletService.createWallet(walletRequestDto);
        return ResponseEntity.ok().body(walletResponse);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody WalletDepositDto walletDepositDto) {
        walletService.deposit(walletDepositDto);
        return ResponseEntity.ok("Deposit successful");
    }

    @GetMapping("/balance/{documentoNumber}")
    public ResponseEntity<WalletResponseDto>  getBalance(@PathVariable String documentoNumber,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(transactionService.getTransactionBalance(documentoNumber,page, size));
    }

    @GetMapping("/balance/resumeTransaction/documentNumber/{documentNumber}/dateTransaction/{dateTransaction}")
    public ResponseEntity<List<TransactionReponseDto>> getHistoricalBalanceGeneral(
            @PathVariable String documentNumber,
            @PathVariable String dateTransaction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(transactionService.getHistoricalTransactionBalance(documentNumber, dateTransaction, page, size));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WalletWithdrawDto withdrawDto) {
        walletService.withdraw(withdrawDto);
        return ResponseEntity.ok("Withdraw successful");
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody WalletTransferDto walletTransferDto) {
        walletService.transfer(walletTransferDto);
        return ResponseEntity.ok("Transfer successful");
    }

}


