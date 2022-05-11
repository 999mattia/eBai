package ch.bbcag.ebai.repositories;

import ch.bbcag.ebai.models.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends CrudRepository<Bid, Integer> {

    @Query("SELECT i FROM Bid i WHERE i.value = :value")
    Iterable<Bid> findByValue(@Param("value") Integer value);
}
