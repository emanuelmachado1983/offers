package com.emanuel.offers.infra.dbo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emanuel.offers.infra.dbo.model.OfferUserEntity;

public interface OfferUserDboRepository extends JpaRepository<OfferUserEntity, Long> {
    
    @Query("SELECT o FROM OfferUserEntity o WHERE " +
            "o.userCode = :userCode " +
            "AND o.continueTrying = true ")
    List<OfferUserEntity> getActiveChosenOffersByUser(@Param("userCode") String userCode);
    
    
    @Query("SELECT o FROM OfferUserEntity o WHERE " +
            "o.continueTrying = true " +
            "AND o.date <= date")
    List<OfferUserEntity> getOffersThatAreToMuchTimeTrying(
    		@Param("date") LocalDateTime date);
    
    @Query("SELECT o FROM OfferUserEntity o WHERE " +
            "o.continueTrying = true " +
            "order by o.userCode ")
    List<OfferUserEntity> getOffersToTryAgain();
}
