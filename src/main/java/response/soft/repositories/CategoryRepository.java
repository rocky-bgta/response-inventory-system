package response.soft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import response.soft.entities.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,UUID> {
}
