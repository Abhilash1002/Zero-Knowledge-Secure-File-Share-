package com.dissertation.scs_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dissertation.scs_backend.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    Optional<User> findByEmailOrUserName(String identifier1, String identifier2);
}