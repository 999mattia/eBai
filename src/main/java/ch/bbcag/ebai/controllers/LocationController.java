package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Location;
import ch.bbcag.ebai.models.User;
import ch.bbcag.ebai.repositories.LocationRepository;
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
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;


    @Operation(summary = "Find a location using the name and/or the plz. If non of them is given, all locations will be returned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @GetMapping
    public Iterable<Location> findByNameOrPlz(@Parameter(description = "Name of the location") @RequestParam(required = false) String location, @Parameter(description = "Plz of the location") @RequestParam(required = false) Integer plz) {
        if (Strings.isNotBlank(location) && plz != null) {
            try {
                return locationRepository.findByNameAndPlz(location, plz);
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
            }
        } else if (Strings.isNotBlank(location) && plz == null) {
            try {
                return locationRepository.findByName(location);
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
            }
        } else if (Strings.isBlank(location) && plz != null) {
            try {
                return locationRepository.findByPlz(plz);
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
            }
        } else {
            return locationRepository.findAll();
        }
    }

    @Operation(summary = "Find a location using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @GetMapping("{id}")
    public Optional<Location> findById(@Parameter(description = "Id of the location") @PathVariable Integer id) {
        try {
            return locationRepository.findById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }

    }

    @Operation(summary = "Create a new location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created succesfully",
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
    public void insert(@Parameter(description = "The new Location") @Valid @RequestBody Location newLocation) {
        try {
            locationRepository.save(newLocation);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Update a location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully",
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
    public void update(@Parameter(description = "The updated location") @Valid @RequestBody Location updatedLocation) {
        try {
            locationRepository.save(updatedLocation);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Delete a location using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location deleted succesfull",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id) {
        try {
            locationRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location could not be deleted");
        }
    }
}
