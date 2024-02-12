package edu.ucsb.cs156.example.repositories;

import org.springframework.data.repository.CrudRepository;

import edu.ucsb.cs156.example.entities.MenuItemReview;

import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemReviewRepository extends CrudRepository<MenuItemReview, Long>{
    Iterable<MenuItemReview> findAllBydateReviewed(String dateReviewed);
}
