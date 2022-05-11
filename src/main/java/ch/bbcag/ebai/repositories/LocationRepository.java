package ch.bbcag.ebai.repositories;

import ch.bbcag.ebai.models.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends CrudRepository<Location, Integer> {

    @Query("SELECT i FROM Location i WHERE i.location LIKE CONCAT('%', :location, '%') ")
    Iterable<Location> findByName(@Param("location") String location);

    @Query("SELECT i FROM Location i WHERE i.plz = :plz")
    Iterable<Location> findByPlz(@Param("plz") Integer plz);

    @Query("SELECT i FROM Location i WHERE i.location LIKE CONCAT('%', :location, '%') AND i.plz = :plz")
    Iterable<Location> findByNameAndPlz(@Param("location") String location, @Param("plz") Integer plz);
}
