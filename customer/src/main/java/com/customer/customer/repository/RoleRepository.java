package com.customer.customer.repository;

import com.customer.customer.entities.users.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findByAuthority(String authority);
}
