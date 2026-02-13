package dev.test.projedata.autoflex.api.controller;

import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialUpdateRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductMaterialResponse;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import dev.test.projedata.autoflex.api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> insert(@Valid @RequestBody ProductRequest request) {
        ProductResponse resp = productService.insert(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.id())
                .toUri();

        return ResponseEntity.created(uri).body(resp);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
           @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok().body(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/materials")
    public ResponseEntity<ProductMaterialResponse> addMaterial(@PathVariable Long productId, @Valid @RequestBody ProductMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addMaterial(productId, request));
    }

    @PatchMapping("/{productId}/materials/{materialId}")
    public ResponseEntity<ProductMaterialResponse> updateQuantityMaterial(@PathVariable Long productId, @PathVariable Long materialId, @Valid @RequestBody ProductMaterialUpdateRequest request) {
        return ResponseEntity.ok().body(productService.updateQuantityMaterial(productId, materialId, request));
    }

    @DeleteMapping("/{productId}/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long productId, @PathVariable Long materialId) {
        productService.deleteMaterial(productId, materialId);
        return ResponseEntity.noContent().build();
    }
}
