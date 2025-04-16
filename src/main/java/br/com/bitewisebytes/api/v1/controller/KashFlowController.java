package br.com.bitewisebytes.api.v1.controller;

import br.com.bitewisebytes.model.requestDto.*;
import br.com.bitewisebytes.model.responseDto.TransactionDetailDto;
import br.com.bitewisebytes.model.responseDto.TransactionReponseDto;
import br.com.bitewisebytes.model.responseDto.WalletResponseDto;
import br.com.bitewisebytes.model.validation.WalletRequestValidator;
import br.com.bitewisebytes.service.TransactionService;
import br.com.bitewisebytes.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("API KASHFLOW HEALTH - OK");
    }

    @Operation(
            summary = "Creates a new wallet",
            description = "This endpoint allows you to create a new wallet with an initial balance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/create")
    public ResponseEntity<WalletResponseDto> createWallet(@RequestBody WalletRequestDto walletRequestDto) {
        WalletRequestValidator.validate(walletRequestDto);
        if (walletRequestDto == null) {
            return ResponseEntity.badRequest().body(null);
        }
        WalletResponseDto walletResponse = walletService.createWallet(walletRequestDto);
        return ResponseEntity.ok().body(walletResponse);
    }

    @Operation(
            summary = "Deposits money into a wallet",
            description = "This endpoint allows the user to deposit money into a specified wallet by providing the necessary deposit details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit was successful"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit details provided"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody WalletDepositDto walletDepositDto) {
        walletService.deposit(walletDepositDto);
        return ResponseEntity.ok("Deposit successful");
    }

    @Operation(
            summary = "Gets wallet balance",
            description = "This endpoint retrieves the wallet balance for a specific document number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping("/balance/{documentoNumber}")
    public ResponseEntity<WalletResponseDto> getBalance(
            @PathVariable String documentoNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(transactionService.getTransactionBalance(documentoNumber, page, size));
    }

    @Operation(
            summary = "Gets the historical balance of a transaction",
            description = "This endpoint allows you to retrieve the historical balance summary based on the document number and transaction date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historical balance summary retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided"),
            @ApiResponse(responseCode = "404", description = "Historical balance not found for the provided criteria")
    })
    @GetMapping("/balance/resumeTransaction/documentNumber/{documentNumber}/dateTransaction/{dateTransaction}")
    public ResponseEntity<List<TransactionReponseDto>> getHistoricalBalanceGeneral(
            @PathVariable String documentNumber,
            @PathVariable String dateTransaction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(transactionService.getHistoricalTransactionBalance(documentNumber, dateTransaction, page, size));
    }

    @Operation(
            summary = "Withdraws money from a wallet",
            description = "This endpoint allows the user to withdraw money from a specified wallet by providing the necessary withdrawal details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal was successful"),
            @ApiResponse(responseCode = "400", description = "Invalid withdrawal details provided"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "402", description = "Insufficient funds in the wallet")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WalletWithdrawDto withdrawDto) {
        walletService.withdraw(withdrawDto);
        return ResponseEntity.ok("Withdraw successful");
    }
    
    @Operation(
            summary = "Transfers money between wallets",
            description = "This endpoint allows the user to transfer money from one wallet to another. The transfer details must be provided in the request body."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer was successful"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer details provided"),
            @ApiResponse(responseCode = "404", description = "Source or destination wallet not found"),
            @ApiResponse(responseCode = "402", description = "Insufficient funds in the source wallet")
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody WalletTransferDto walletTransferDto) {
        walletService.transfer(walletTransferDto);
        return ResponseEntity.ok("Transfer successful");
    }
}

