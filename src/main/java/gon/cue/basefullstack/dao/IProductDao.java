package gon.cue.basefullstack.dao;

import gon.cue.basefullstack.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IProductDao extends CrudRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
}
