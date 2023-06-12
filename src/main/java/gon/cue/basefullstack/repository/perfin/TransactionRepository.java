package gon.cue.basefullstack.repository.perfin;

import gon.cue.basefullstack.entities.perfin.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
