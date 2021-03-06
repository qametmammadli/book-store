package com.qamet.book_store.repository;

import com.qamet.book_store.entity.Role;
import com.qamet.book_store.entity.enumeration.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(RoleName name);
}
