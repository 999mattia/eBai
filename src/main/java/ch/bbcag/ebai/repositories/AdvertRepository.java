package ch.bbcag.ebai.repositories;

import ch.bbcag.ebai.models.Advert;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AdvertRepository extends CrudRepository<Advert, Integer> {

    @Query("SELECT i FROM Advert i WHERE i.name LIKE CONCAT('%', :name, '%')")
    Iterable<Advert> findByName(@Param("name") String name);
}
