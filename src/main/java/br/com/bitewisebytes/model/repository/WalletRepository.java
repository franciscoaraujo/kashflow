package br.com.bitewisebytes.model.repository;

import br.com.bitewisebytes.model.entity.Transaction;
import br.com.bitewisebytes.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    public Optional<Wallet> findByDocumentNumber(String documentNumber);
}
