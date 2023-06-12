package gon.cue.basefullstack.repository.perfin;

import gon.cue.basefullstack.entities.perfin.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
