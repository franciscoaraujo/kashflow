package br.com.bitewisebytes.api.v1.controller;

import br.com.bitewisebytes.model.requestDto.WalletDepositDto;
import br.com.bitewisebytes.model.requestDto.WalletRequestDto;
import br.com.bitewisebytes.model.requestDto.WalletTransferDto;
import br.com.bitewisebytes.model.requestDto.WalletWithdrawDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import br.com.bitewisebytes.model.validation.WalletRequestValidator;
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

    public KashFlowController(WalletService walletService) {
        this.walletService = walletService;
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
    public ResponseEntity<WalletResponseDto>  getBalance(@PathVariable String documentoNumber) {
        return ResponseEntity.ok().body(walletService.getBalance(documentoNumber));
    }

    @GetMapping("/balance/detailHistory/documentNumber/{documentNumber}/dateTransaction/{dateTransaction}")
    public ResponseEntity<List<TransactionReponseDto>> getHistoricalBalance(@PathVariable String documentNumber, @PathVariable String dateTransaction) {
        return ResponseEntity.ok().body(walletService.getHistoricalBalance(documentNumber, (dateTransaction)));
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WalletWithdrawDto withdrawDto) {
        walletService.withdraw(withdrawDto);
    }


    @PostMapping("/transfer")
    public void transfer(@RequestBody WalletTransferDto walletTransferDto) {
        walletService.transfer(walletTransferDto);
    }

}


