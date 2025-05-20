package com.dissertation.scs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dissertation.scs_backend.entity.FileShare;

@Repository
public interface FileShareRepository extends CrudRepository<FileShare,Integer>{

    Optional<FileShare> findByShareId(Integer shareId);

    List<FileShare> findBySender(Integer sharedBy);

    List<FileShare> findByReceiver(Integer sharedBy);

    List<FileShare> findAllByFileId(Integer fileId);
}
