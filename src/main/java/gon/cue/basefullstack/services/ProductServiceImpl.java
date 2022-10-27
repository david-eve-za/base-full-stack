package gon.cue.basefullstack.services;

import gon.cue.basefullstack.dao.ICategoryDao;
import gon.cue.basefullstack.dao.IProductDao;
import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.model.Product;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final IProductDao productDao;
    private final ICategoryDao categoryDao;

    public ProductServiceImpl(IProductDao productDao, ICategoryDao categoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseRest<Product>> save(Product product, Long idCategory) {
        ResponseRest<Product> responseRest = new ResponseRest<>();
        List<Product> products = new ArrayList<>();

        try {
            Optional<Category> category = categoryDao.findById(idCategory);
            if (category.isPresent()) {
                product.setCategory(category.get());
                products.add(productDao.save(product));
                responseRest.setMetadata("Ok","200","Product saved successfully");
                responseRest.setData(products);
            } else {
                responseRest.setMetadata("Error","400","Category not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }

        return ResponseEntity.ok(responseRest);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseRest<Product>> update(Product product, Long idCategory,Long idProduct) {
        ResponseRest<Product> responseRest = new ResponseRest<>();
        List<Product> products = new ArrayList<>();

        try {
            Optional<Category> category = categoryDao.findById(idCategory);

            if (category.isPresent()) {
                product.setCategory(category.get());
            } else {
                responseRest.setMetadata("Error","400","Category not found");
                return ResponseEntity.badRequest().body(responseRest);
            }

            Optional<Product> productOptional = productDao.findById(idProduct);

            if (productOptional.isPresent()) {
                Product productUpdate = productOptional.get();

                productUpdate.setName(product.getName());
                productUpdate.setPrice(product.getPrice());
                productUpdate.setCategory(product.getCategory());
                productUpdate.setStock(product.getStock());
                productUpdate.setImage(product.getImage());

                products.add(productDao.save(productUpdate));
                responseRest.setMetadata("Ok","200","Product updated successfully");
                responseRest.setData(products);
            } else {
                responseRest.setMetadata("Error","400","Product not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseRest<Product>> delete(Long idProduct) {
        ResponseRest<Product> responseRest = new ResponseRest<>();

        try {
            Optional<Product> product = productDao.findById(idProduct);
            if (product.isPresent()) {
                productDao.delete(product.get());
                responseRest.setMetadata("Ok","200","Product deleted successfully");
            } else {
                responseRest.setMetadata("Error","400","Product not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseRest<Product>> findById(Long idProduct) {
        ResponseRest<Product> responseRest = new ResponseRest<>();
        List<Product> products = new ArrayList<>();

        try {
            Optional<Product> product = productDao.findById(idProduct);
            if (product.isPresent()) {
                byte[] image = Util.decompressZLib(product.get().getImage());
                product.get().setImage(image);
                products.add(product.get());
                responseRest.setMetadata("Ok","200","Product found successfully");
                responseRest.setData(products);
            } else {
                responseRest.setMetadata("Error","400","Product not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseRest<Product>> findAll() {
        ResponseRest<Product> responseRest = new ResponseRest<>();
        List<Product> products;

        try {
            products = (List<Product>) productDao.findAll();
            if (products.size() > 0) {
                products.stream().forEach(product -> {
                    byte[] image = Util.decompressZLib(product.getImage());
                    product.setImage(image);
                });
                responseRest.setMetadata("Ok","200","Products found successfully");
                responseRest.setData(products);
            } else {
                responseRest.setMetadata("Error","400","Products not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseRest<Product>> findByName(String name) {
        ResponseRest<Product> responseRest = new ResponseRest<>();
        List<Product> products;

        try {
            products = productDao.findByNameContainingIgnoreCase(name);
            if (products.size() > 0) {
                products.stream().forEach(product -> {
                    byte[] image = Util.decompressZLib(product.getImage());
                    product.setImage(image);
                });
                responseRest.setMetadata("Ok","200","Products found successfully");
                responseRest.setData(products);
            } else {
                responseRest.setMetadata("Error","400","Products not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            responseRest.setMetadata("Error","500","Internal server error");
            return ResponseEntity.badRequest().body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }
}
