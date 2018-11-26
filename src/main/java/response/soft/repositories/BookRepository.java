package response.soft.repositories;


import org.springframework.data.repository.CrudRepository;
import response.soft.entities.Book;

public interface BookRepository extends CrudRepository<Book, Integer> {

}
