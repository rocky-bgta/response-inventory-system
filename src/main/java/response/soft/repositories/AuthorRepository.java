package response.soft.repositories;


import org.springframework.data.repository.CrudRepository;
import response.soft.entities.Author;

public interface AuthorRepository extends CrudRepository<Author, Integer> {

}
