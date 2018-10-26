package com.example.python.dao;

import com.example.python.pojo.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryDao extends JpaRepository<Code,Integer> {

}
