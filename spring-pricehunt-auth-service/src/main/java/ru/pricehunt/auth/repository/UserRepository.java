package ru.pricehunt.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pricehunt.auth.model.Person;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

}
