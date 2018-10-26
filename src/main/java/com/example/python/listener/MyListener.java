package com.example.python.listener;

import com.example.python.dao.CountryDao;
import com.example.python.pojo.Code;
import com.example.python.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyListener implements ServletContextListener {

    @Autowired
    CountryService countryService;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("IndexListener2 contextDestroyed method");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("IndexListener2 contextInitialized method");
        countryService.queryData();
    }
}

