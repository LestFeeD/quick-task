package com.quick_task.service;

import com.quick_task.dao.WebUserDAO;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.DaoFactory;
import com.quick_task.security.security_config.MyUserDetails;
import com.quick_task.utils.DBService;
import org.hibernate.Transaction;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Transaction transaction = DBService.getTransaction();
        try{
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            if(webUserDAO.findByEmail(username) == null) {
                throw new UsernameNotFoundException("The user not found.");
            } else {
                WebUser webUser = webUserDAO.findUserByEmail(username);
                transaction.commit();
                return MyUserDetails.buildUserDetails(webUser);
            }
        } catch (SQLException | UsernameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
