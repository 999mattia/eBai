package ch.bbcag.ebai.controllers;

import ch.bbcag.ebai.models.Bid;
import ch.bbcag.ebai.models.User;
import ch.bbcag.ebai.repositories.BidRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/bids")
public class BidController {

    @Autowired
    private BidRepository bidRepository;

    @Operation(summary = "Find a bid using the value of the bid. If no value is given, all bids will be returned")
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
    public Iterable<Bid> findByValue(@Parameter(description = "Value of the bid") @RequestParam(required = false) Integer value) {
        if (value == null) {
            try {
                return bidRepository.findAll();
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid not found");
            }
        } else {
            return bidRepository.findByValue(value);
        }
    }

    @Operation(summary = "Find a bid using the id")
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
    @GetMapping("{id}")
    public Optional<Bid> findById(@Parameter(description = "Id of the bid") @PathVariable Integer id) {
        try {
            return bidRepository.findById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid not found");
        }

    }

    @Operation(summary = "Create a new bid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bid created succesfully",
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
    public void insert(@Parameter(description = "The new bid") @Valid @RequestBody Bid newBid) {
        try {
            bidRepository.save(newBid);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Update a bid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid updated successfully",
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
    public void update(@Parameter(description = "The updated bid") @Valid @RequestBody Bid updatedBid) {
        try {
            bidRepository.save(updatedBid);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Delete a bid using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bid deleted succesfull",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Bid not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server Error",
                    content = @Content)
    })
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id) {
        try {
            bidRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bid could not be deleted");
        }
    }
}
