package gon.cue.basefullstack.repository.perfin;

import gon.cue.basefullstack.entities.perfin.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
