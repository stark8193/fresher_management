package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FresherRepository;
import com.mycompany.myapp.service.FresherService;
import com.mycompany.myapp.service.dto.FresherDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Fresher}.
 */
@RestController
@RequestMapping("/api")
public class FresherResource {

    private final Logger log = LoggerFactory.getLogger(FresherResource.class);

    private static final String ENTITY_NAME = "fresher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FresherService fresherService;

    private final FresherRepository fresherRepository;

    public FresherResource(FresherService fresherService, FresherRepository fresherRepository) {
        this.fresherService = fresherService;
        this.fresherRepository = fresherRepository;
    }

    /**
     * {@code POST  /freshers} : Create a new fresher.
     *
     * @param fresherDTO the fresherDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fresherDTO, or with status {@code 400 (Bad Request)} if the fresher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/freshers")
    public ResponseEntity<FresherDTO> createFresher(@Valid @RequestBody FresherDTO fresherDTO) throws URISyntaxException {
        log.debug("REST request to save Fresher : {}", fresherDTO);
        if (fresherDTO.getId() != null) {
            throw new BadRequestAlertException("A new fresher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FresherDTO result = fresherService.save(fresherDTO);
        return ResponseEntity
            .created(new URI("/api/freshers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /freshers/:id} : Updates an existing fresher.
     *
     * @param id the id of the fresherDTO to save.
     * @param fresherDTO the fresherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fresherDTO,
     * or with status {@code 400 (Bad Request)} if the fresherDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fresherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/freshers/{id}")
    public ResponseEntity<FresherDTO> updateFresher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FresherDTO fresherDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Fresher : {}, {}", id, fresherDTO);
        if (fresherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fresherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fresherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FresherDTO result = fresherService.update(fresherDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fresherDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /freshers/:id} : Partial updates given fields of an existing fresher, field will ignore if it is null
     *
     * @param id the id of the fresherDTO to save.
     * @param fresherDTO the fresherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fresherDTO,
     * or with status {@code 400 (Bad Request)} if the fresherDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fresherDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fresherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/freshers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FresherDTO> partialUpdateFresher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FresherDTO fresherDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Fresher partially : {}, {}", id, fresherDTO);
        if (fresherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fresherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fresherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FresherDTO> result = fresherService.partialUpdate(fresherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fresherDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /freshers} : get all the freshers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of freshers in body.
     */
    @GetMapping("/freshers")
    public ResponseEntity<List<FresherDTO>> getAllFreshers(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Freshers");
        Page<FresherDTO> page;
        if (eagerload) {
            page = fresherService.findAllWithEagerRelationships(pageable);
        } else {
            page = fresherService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /freshers/:id} : get the "id" fresher.
     *
     * @param id the id of the fresherDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fresherDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/freshers/{id}")
    public ResponseEntity<FresherDTO> getFresher(@PathVariable Long id) {
        log.debug("REST request to get Fresher : {}", id);
        Optional<FresherDTO> fresherDTO = fresherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fresherDTO);
    }

    /**
     * {@code DELETE  /freshers/:id} : delete the "id" fresher.
     *
     * @param id the id of the fresherDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/freshers/{id}")
    public ResponseEntity<Void> deleteFresher(@PathVariable Long id) {
        log.debug("REST request to delete Fresher : {}", id);
        fresherService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
