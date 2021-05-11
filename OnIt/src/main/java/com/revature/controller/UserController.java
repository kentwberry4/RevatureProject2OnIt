package com.revature.controller;

import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.revature.model.*;
import com.revature.service.*;


@Controller
public class UserController implements IUserController {


	@Autowired
	private IUserService userservice = new UserService();
	
	@Override
	public boolean register(HttpServletRequest request) {
		return userservice.register(request.getParameter("firstname"), 
				request.getParameter("lastName"), 
				request.getParameter("email"), 
				request.getParameter("password"));
	}

	@Override
	public User login(HttpServletRequest request) {
		return userservice.login(request.getParameter("email"), 
								 request.getParameter("password"));
	}

	@Override
	public boolean logout(HttpServletRequest request) { //done using httpsession
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregister(HttpServletRequest request) {
		return userservice.unregister(request.getParameter("email"), request.getParameter("password"));
	}

	@Override
	public String downloadMyData(HttpServletRequest request) {
		return userservice.downloadMyData(request.getParameter("email"),
										  request.getParameter("password"));
	}

	@Override
	public boolean createTask(HttpServletRequest request) {
		// Create task out of the request
		Task newTask = new Task(request.getParameter("userId"), 
								request.getParameter("labelId"),
								request.getParameter("taskName"),
								request.getParameter("notes"),
								LocalDate.parse(request.getParameter("dueDate")),
								Integer.parseInt(request.getParameter("reminder")),
								Boolean.parseBoolean(request.getParameter("repeatable")));
		
		return userservice.createTask(newTask);
	}

	@Override
	public boolean updateTask(HttpServletRequest request) {
		// Create task out of the request
		Task newTask = new Task(request.getParameter("userId"), 
								request.getParameter("labelId"),
								request.getParameter("taskName"),
								request.getParameter("notes"),
								LocalDate.parse(request.getParameter("dueDate")),
								Integer.parseInt(request.getParameter("reminder")),
								Boolean.parseBoolean(request.getParameter("repeatable")));

		return userservice.updateTask(newTask);
	}

	@Override
	public boolean deleteTask(HttpServletRequest request) {
		return userservice.deleteTask(request.getParameter("taskId"));
	}

	@Override
	public List<Task> viewTasks(HttpServletRequest request) {
		return userservice.viewTasks();
	}

	@Override
	public boolean completeTask(HttpServletRequest request) {
		return userservice.completeTask(request.getParameter("taskId"));
	}

	@Override
	public List<Task> viewCompleted(HttpServletRequest request) {
		return userservice.viewCompleted();
	}

	@Override
	public boolean labelTask(HttpServletRequest request) {
		return userservice.labelTask(request.getParameter("taskId"), request.getParameter("labelId"));
	}

	@Override
	public List<Task> viewLabel(HttpServletRequest request) {
		return userservice.viewLabel(request.getParameter("labelId"));
	}

	@Override
	public boolean duedateTask(HttpServletRequest request) {
		return userservice.duedateTask(request.getParameter("taskId"), 
				                       LocalDate.parse(request.getParameter("dueDate")));
	}

	@Override
	public boolean viewDuedate(HttpServletRequest request) {
		return userservice.viewDuedate(LocalDate.parse(request.getParameter("dueDate")));
	}

	@Override
	public boolean receiveEmailReminders(HttpServletRequest request) {
		return userservice.receiveEmailReminders(Integer.parseInt(request.getParameter("receiveEmailReminders")));
	}

	@Override
	public boolean setRepeatableTask(HttpServletRequest request) {
		return userservice.SetRepeatableTask(request.getParameter("taskId"),
				                             Boolean.parseBoolean(request.getParameter("repeatable")));
	}

	@Override
	public boolean setDailyGoals(HttpServletRequest request) {
		return userservice.setDailyGoals(Integer.parseInt(request.getParameter("numDesired")));
	}

	@Override
	public Object viewProgress(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object viewPastProgressGraph(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}