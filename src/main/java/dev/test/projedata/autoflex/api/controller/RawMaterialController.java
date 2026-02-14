package dev.test.projedata.autoflex.api.controller;

import dev.test.projedata.autoflex.api.dtos.request.RawMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.response.RawMaterialResponse;
import dev.test.projedata.autoflex.api.service.RawMaterialService;
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
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

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

    @GetMapping
    public ResponseEntity<Page<RawMaterialResponse>> findAll(
           @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok().body(rawMaterialService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(rawMaterialService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponse> update(@PathVariable Long id, @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.ok().body(rawMaterialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rawMaterialService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
