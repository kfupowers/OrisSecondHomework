package ru.kpfu.itis.shakirov.repository;

import ru.kpfu.itis.shakirov.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}