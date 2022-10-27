package gon.cue.basefullstack.services;

import gon.cue.basefullstack.model.Product;
import gon.cue.basefullstack.response.ResponseRest;
import org.springframework.http.ResponseEntity;

public interface IProductService {

    public ResponseEntity<ResponseRest<Product>> save(Product product, Long idCategory);

    public ResponseEntity<ResponseRest<Product>> update(Product product, Long idCategory, Long id);

    public ResponseEntity<ResponseRest<Product>> delete(Long idProduct);

    public ResponseEntity<ResponseRest<Product>> findById(Long idProduct);

    public ResponseEntity<ResponseRest<Product>> findAll();

    public ResponseEntity<ResponseRest<Product>> findByName(String name);
}
