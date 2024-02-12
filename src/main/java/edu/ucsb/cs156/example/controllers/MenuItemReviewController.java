package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Slf4j
@Tag(name = " UCSB MenuItemReviews")
@RequestMapping("/api/menuitemreview")
@RestController
public class MenuItemReviewController extends ApiController{
    
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MenuItemReviewRepository repo;
    
    @Operation(summary= "Post a review from inputs of menu items in the header")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    MenuItemReview postMenuItemReview( @Parameter(name = "itemId") @RequestParam Long itemId, 
                                        @Parameter(name = "email") @RequestParam String email,
                                        @Parameter(name = "stars") @RequestParam int stars,
                                        @Parameter(name = "comments") @RequestParam String comments,
                                        @Parameter(name = "timestamp") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp
                                        )throws JsonProcessingException
                                        {
        MenuItemReview newReview = MenuItemReview.builder()
                                                    .itemId(itemId)
                                                    .reviewerEmail(email)
                                                    .stars(stars)
                                                    .comments(comments)
                                                    .dateReviewed(timestamp)
                                                    .build();
        
        MenuItemReview postedObj =  repo.save(newReview);
        return postedObj;
    }
    
    @Operation(summary= "Get a single review given an id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    MenuItemReview getMenuItemReview(@Parameter(name = "id") @RequestParam Long id){
        MenuItemReview returnedItem = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
        return returnedItem;
    }
    @Operation(summary= "Get all reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    Iterable<MenuItemReview> getAllReviews(){
        Iterable<MenuItemReview> allItems = repo.findAll();
        return allItems;
    }

    @Operation(summary= "Delete a single review by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    Object deleteItem(@Parameter(name = "id") @RequestParam Long id){
        MenuItemReview deletedItem = repo.findById(id)
                                            .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
        repo.delete(deletedItem);
        return genericMessage("MenuItemReview with id %s deleted".formatted(id));
    }
    
    @Operation(summary= "Update a single review by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    MenuItemReview updateReview(@Parameter(name = "id") @RequestParam Long id, 
                                 @RequestBody @Valid MenuItemReview newItem
                                ){
        MenuItemReview updatedItem = repo.findById(id)
                                            .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
        updatedItem.setItemId(newItem.getItemId());
        updatedItem.setReviewerEmail(newItem.getReviewerEmail());
        updatedItem.setStars(newItem.getStars());
        updatedItem.setDateReviewed(newItem.getDateReviewed());
        updatedItem.setComments(newItem.getComments());

        repo.save(updatedItem);
        return updatedItem;
    }
    
}
