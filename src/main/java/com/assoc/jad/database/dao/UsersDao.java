package com.assoc.jad.database.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assoc.jad.database.test.Users;

public interface UsersDao extends JpaRepository<Users, Integer> { // JpaRepository<Users, UserKeys> {
	
	List<Users> findByEmail(String email);
	List<Users> findByLoginid(String loginid);
	
	@Query(value = "SELECT * from Users where loginid = :loginid", nativeQuery = true)
    List<Users> findByAllOrdered(@Param("loginid") String loginid);
}

