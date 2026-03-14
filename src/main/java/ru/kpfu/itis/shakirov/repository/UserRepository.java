package ru.kpfu.itis.shakirov.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import ru.kpfu.itis.shakirov.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    User save(User user);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :username WHERE u.id = :id")
    int updateUsernameById(@Param("username") String username, @Param("id") Long id);

    Optional<User> findByUsername(String username);

}