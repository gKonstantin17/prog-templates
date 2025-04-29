package micros.plannertodo.repository;

import micros.plannerentity.entity.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    Stat findByUserId(String id); // 1 obj т.к. у юзверя ток 1 стата
}
