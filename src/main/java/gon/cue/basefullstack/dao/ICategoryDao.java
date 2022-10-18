package gon.cue.basefullstack.dao;

import gon.cue.basefullstack.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface ICategoryDao extends CrudRepository<Category, Long> {

}
