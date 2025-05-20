package com.dissertation.scs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dissertation.scs_backend.entity.PublicShare;

@Repository
public interface PublicShareRepository extends CrudRepository<PublicShare,Integer>{

    List<PublicShare> findByOwnerId(Integer ownerId);

    Optional<PublicShare> findByfileId(Integer fileId);

    Optional<PublicShare> findByPrivateFileId(Integer privateFileId);
}
