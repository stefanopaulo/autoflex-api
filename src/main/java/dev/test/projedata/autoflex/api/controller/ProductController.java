package dev.test.projedata.autoflex.api.controller;

import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialUpdateRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductMaterialResponse;
import dev.test.projedata.autoflex.api.dtos.response.ProductProductionResponse;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import dev.test.projedata.autoflex.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "Endpoint for product management")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Insert Product", description = "Register a new product in the application")
    
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

    @Operation(summary = "FindAll Product", description = "List all products available for use")
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok().body(productService.findAll(pageable));
    }

    @Operation(summary = "FindById Product", description = "Search for a product by ID and display its information. Throw an exception if the given ID does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @Operation(summary = "Update Product", description = "Updates the data for a product. Throws an exception if the provided ID does not exist")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.update(id, request));
    }

    @Operation(summary = "Delete Product", description = "Deletes a product from the database. Throws an exception if the given ID does not exist")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add Material to Product", description = "Links a raw material to a specific product, defining the required quantity for production")
    @PostMapping("/{productId}/materials")
    public ResponseEntity<ProductMaterialResponse> addMaterial(@PathVariable Long productId, @Valid @RequestBody ProductMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addMaterial(productId, request));
    }

    @Operation(summary = "Update Material Quantity", description = "Updates the required quantity of a specific raw material linked to a product")
    @PatchMapping("/{productId}/materials/{materialId}")
    public ResponseEntity<ProductMaterialResponse> updateQuantityMaterial(@PathVariable Long productId, @PathVariable Long materialId, @Valid @RequestBody ProductMaterialUpdateRequest request) {
        return ResponseEntity.ok().body(productService.updateQuantityMaterial(productId, materialId, request));
    }

    @Operation(summary = "Remove Material from Product", description = "Removes the link between a raw material and a product")
    @DeleteMapping("/{productId}/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long productId, @PathVariable Long materialId) {
        productService.deleteMaterial(productId, materialId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calculate Available Production", description = "Analyzes current stock levels of all materials to calculate how many units of each product can be manufactured")
    @GetMapping("/availableProduction")
    public ResponseEntity<List<ProductProductionResponse>> getAvailableProduction() {
        return ResponseEntity.ok().body(productService.getAvailableProduction());
    }
}
