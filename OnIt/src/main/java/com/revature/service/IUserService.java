package com.revature.service;

import java.util.List;
import java.time.LocalDate;
import com.revature.model.Task;
import com.revature.model.User;

public interface IUserService {
	
	// Authentication, deletion, download
    boolean register(User user);
	User login(User user);

	boolean unregister(String email, String password); //account deletion, requires providing password to delete
	String downloadMyData(String email, String password);
	
	
	// Creating, deleteing, and viewing tasks
	boolean createTask(Task task);
	boolean updateTask(Task task);
	boolean deleteTask(String taskId);
	List<Task> viewTasks (); //view all  
	
	// Completing a task, filtering based of completion
	boolean completeTask(String taskId);
	List<Task> viewCompleted(); //view completed
	
	
	// Labelling, filtering based on label
	boolean labelTask(String taskId, String labelId);
	List<Task> viewLabel(String labelId);
	
	// Adding due date, filtering based on duedate
	boolean duedateTask(String taskId, LocalDate dueDate);
	boolean viewDuedate(LocalDate dueDate);
	
	// receiving email reminders
	boolean receiveEmailReminders(int reminderPeriod); //0, 1, 2 days before
	
	// Assign repeatable/non repeatable
	boolean SetRepeatableTask(String taskId, boolean repeatable); //true or false
	
	//Setting daily goals
	boolean setDailyGoals(int numDesired);
	Object viewProgress();
	
	//viewing graph
	Object viewPastProgressGraph();
}