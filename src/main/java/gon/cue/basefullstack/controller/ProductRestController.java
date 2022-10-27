package gon.cue.basefullstack.controller;

import gon.cue.basefullstack.model.Product;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.services.IProductService;
import gon.cue.basefullstack.utils.Util;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/v1")
public class ProductRestController {

    private final IProductService productService;

    public ProductRestController(IProductService productService) {
        this.productService = productService;
    }

    /**
     * @param image
     * @param name
     * @param price
     * @param stock
     * @param idCategory
     * @return ResponseEntity<ResponseRest < Product>>
     * ResponseEntity.ok(responseRest);
     */
    @PostMapping("/product")
    public ResponseEntity<ResponseRest<Product>> save(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("stock") int stock,
            @RequestParam("idCategory") Long idCategory) throws IOException {

        ResponseRest<Product> responseRest = new ResponseRest<>();
        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(Util.compressZLib(image.getBytes()));

        return productService.save(product, idCategory);
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseRest<Product>> findAll() {
        return productService.findAll();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ResponseRest<Product>> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<ResponseRest<Product>> update(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("stock") int stock,
            @RequestParam("idCategory") Long idCategory) throws IOException {

        ResponseRest<Product> responseRest = new ResponseRest<>();
        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(Util.compressZLib(image.getBytes()));

        return productService.update(product, idCategory, id);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<ResponseRest<Product>> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
