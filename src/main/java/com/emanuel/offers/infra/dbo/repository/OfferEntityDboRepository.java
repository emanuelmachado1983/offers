package com.emanuel.offers.infra.dbo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emanuel.offers.infra.dbo.model.OfferEntity;

public interface OfferEntityDboRepository extends JpaRepository<OfferEntity, Long> {
    @Query("SELECT o FROM OfferEntity o WHERE " +
            "(:importance IS NULL OR o.importance = :importance) " +
            "AND (:urgency IS NULL OR o.urgency = :urgency) " +
            "AND (:category IS NULL OR o.category = :category)")
    List<OfferEntity> findOffersByFilters(@Param("importance") Integer importance,
                                         @Param("urgency") Integer urgency,
                                         @Param("category") String category);
    

    
    
}
