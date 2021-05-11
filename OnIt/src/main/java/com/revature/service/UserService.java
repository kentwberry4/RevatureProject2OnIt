package com.revature.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.revature.dao.*;
import com.revature.model.*;


@Service
@Component
public class UserService implements IUserService {

	@Autowired
	private IUserDao userdao = new UserDao();
	
	@Autowired
	private ITaskDao taskdao = new TaskDao();
	
	@Override
	public boolean register(String firstName, String lastName, String email, String password) {
		return userdao.insert(firstName, lastName, email, password);
	}

	@Override
	public User login(String email, String password) {
		return userdao.select(email, password);
	}


	@Override
	public boolean unregister(String email, String password) {
		return userdao.delete(email, password);
	}

	@Override
	public String downloadMyData(String email , String password) {
		return userdao.select(email, password).toString();
	}

	@Override
	public boolean createTask(Task task) {
		return taskdao.insert(task);
	}

	@Override
	public boolean updateTask(Task task) {
		return taskdao.update(task);
	}

	@Override
	public boolean deleteTask(String taskId) {
		return taskdao.delete(taskId);
	}

	@Override
	public List<Task> viewTasks() {
		return taskdao.selectTasks();
	}

	@Override
	public boolean completeTask(String taskId) {
		return taskdao.updateCompleteTask(taskId);
	}

	@Override
	public List<Task> viewCompleted() {
		return taskdao.selectCompleted();
	}

	@Override
	public boolean labelTask(String taskId, String labelId) {
		return taskdao.updateLabelTask(taskId, labelId);
	}

	@Override
	public List<Task> viewLabel(String labelId) {
		return taskdao.selectLabel(labelId);
	}

	@Override
	public boolean duedateTask(String taskId, LocalDate dueDate) {
		return taskdao.duedateTask(taskId, dueDate);
	}

	@Override
	public boolean viewDuedate(LocalDate dueDate) {
		return taskdao.selectDuedate(dueDate);
	}

	@Override
	public boolean receiveEmailReminders(int reminderPeriod) {
		return userdao.updateEmailReminders(reminderPeriod);
	}

	@Override
	public boolean SetRepeatableTask(String taskId, boolean repeatable) {
		return taskdao.updateRepeatableTask(taskId, repeatable);
	}

	@Override
	public boolean setDailyGoals(int numDesired) {
		return userdao.updateGoal(numDesired);
	}

	@Override
	public Object viewProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object viewPastProgressGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}