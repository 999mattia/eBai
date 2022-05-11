package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Advert;
import ch.bbcag.ebai.models.User;
import ch.bbcag.ebai.repositories.AdvertRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/adverts")
public class AdvertController {

    @Autowired
    private AdvertRepository advertRepository;

    @Operation(summary = "Find a advert using the name. If no name is given, all adverts will be returned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Bid not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @GetMapping
    public Iterable<Advert> findByName(@Parameter(description = "Advert name to search") @RequestParam(required = false) String name) {
        if (Strings.isNotBlank(name)) {
            try {
                return advertRepository.findByName(name);
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            return advertRepository.findAll();
        }
    }

    @Operation(summary = "Find a advert using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advert found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Advert not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @GetMapping("{id}")
    public Advert findById(@Parameter(description = "Id of the advert to get") @RequestParam(required = false) Integer id) {
        try {
            return advertRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advert not found");
        }
    }

    @Operation(summary = "Create a new advert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Advert created succesfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@Parameter(description = "The new advert") @Valid @RequestBody Advert newAdvert) {
        try {
            advertRepository.save(newAdvert);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Update a advert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advert updated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @PutMapping(consumes = "application/json")
    public void update(@Parameter(description = "The updated advert") @Valid @RequestBody Advert updatedAdvert) {
        try {
            advertRepository.save(updatedAdvert);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Delete a advert using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advert deleted succesfull",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Advert not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id) {
        try {
            advertRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advert could not be deleted");
        }
    }
}
