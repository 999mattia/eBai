package ch.bbcag.ebai.repositories;

import ch.bbcag.ebai.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("SELECT i FROM User i WHERE i.name LIKE CONCAT('%', :name, '%')")
    Iterable<User> findByName(@Param("name") String name);
}
