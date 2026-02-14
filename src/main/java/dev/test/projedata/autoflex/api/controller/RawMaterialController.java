package dev.test.projedata.autoflex.api.controller;

import dev.test.projedata.autoflex.api.dtos.request.RawMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.response.RawMaterialResponse;
import dev.test.projedata.autoflex.api.service.RawMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/rawMaterials")
@Tag(name = "RawMaterial", description = "Endpoint for raw material management")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @Operation(summary = "Insert RawMaterial", description = "Register a new raw material in the application")
    @PostMapping
    public ResponseEntity<RawMaterialResponse> insert(@Valid @RequestBody RawMaterialRequest request) {
        RawMaterialResponse resp = rawMaterialService.insert(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.id())
                .toUri();

        return ResponseEntity.created(uri).body(resp);
    }

    @Operation(summary = "FindAll RawMaterial", description = "List all raw materials available for use")
    @GetMapping
    public ResponseEntity<Page<RawMaterialResponse>> findAll(
           @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok().body(rawMaterialService.findAll(pageable));
    }

    @Operation(summary = "FindById RawMaterial", description = "Search for a raw material by ID and display its information. Throw an exception if the given ID does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(rawMaterialService.findById(id));
    }

    @Operation(summary = "Update RawMaterial", description = "Updates the data for a raw material. Throws an exception if the provided ID does not exist")
    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> update(@PathVariable Long id, @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.ok().body(rawMaterialService.update(id, request));
    }

    @Operation(summary = "Delete RawMaterial", description = "Deletes a raw material from the database. Throws an exception if the given ID does not exist")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
