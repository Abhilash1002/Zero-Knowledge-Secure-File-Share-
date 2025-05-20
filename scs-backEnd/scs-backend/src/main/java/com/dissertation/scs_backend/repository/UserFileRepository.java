package com.dissertation.scs_backend.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dissertation.scs_backend.entity.UserFile;

@Repository
public interface UserFileRepository extends CrudRepository<UserFile,Integer>{
    List<UserFile> findByOwnerId(Integer ownerId);

    UserFile findByFileId(Integer fileId);
}
