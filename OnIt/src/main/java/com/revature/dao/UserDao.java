package com.revature.dao;



import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.revature.model.User;



@EnableTransactionManagement
@Repository("UserDao")
public class UserDao implements IUserDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public void saveUser(User u)
	{
		
		sessionFactory.getCurrentSession().save(u);
	}

	@Override
	public boolean insert(User user) {
		
		sessionFactory.getCurrentSession().save(user);
			
		return true;
	}

	@Override
	public User select(User user) {
		
		User returned = sessionFactory.getCurrentSession().get(User.class, user.getID());
		return returned;
	}

	@Override
	public boolean delete(String email, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEmailReminders(int reminderPeriod) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateGoal(int numDesired) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
