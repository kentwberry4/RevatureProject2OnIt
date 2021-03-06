package com.revature.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.dto.DtoInteger;
import com.revature.dto.DtoLoginUser;
import com.revature.dto.DtoRegisterUser;
import com.revature.dto.DtoString;
import com.revature.dto.DtoTask;
import com.revature.dto.DtoUpdatedTask;
import com.revature.dto.DtoUpdatedUser;
import com.revature.dto.DtoUser;
import com.revature.dto.DtoUserPasswordSessionKey;
import com.revature.dto.DtoUserSessionKey;
import com.revature.model.Task;
import com.revature.model.User;
import com.revature.service.IUserService;
import com.revature.service.UserService;

@Configuration
@CrossOrigin(origins = {"http://onitp2.s3-website.us-east-2.amazonaws.com", "http://localhost:4200",
		"http://localhost:4200/tasks", "http://localhost:4200/taskstats", "http://localhost:4200/home"}, allowCredentials = "true")
@RestController
public class UserController  {

	//Method for Hashing password
	protected String hashPass(String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] salt = new byte[16];
		KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] hash = f.generateSecret(spec).getEncoded();
		Base64.Encoder enc = Base64.getEncoder();
		return enc.encodeToString(hash);
	}
		
	
	
	@Autowired
	private IUserService userservice = new UserService();
	
	
	@GetMapping(value = "/checkActiveSession")
	//@RequestMapping(method=RequestMethod.GET, value="/checkActiveSession/{sessionToken}")
	public  @ResponseBody User checkActiveSession(@RequestParam String sessionToken) {
		
		User authorizedUser = userservice.getUserFromSessionToken(sessionToken);
		
		if(authorizedUser != null) {
			return authorizedUser;
		} else {
			return null;
		}
	}
	
	@PostMapping(value = "/register")
	public  @ResponseBody User register(@RequestBody DtoRegisterUser dtoRegisterUser) {
		String hashedPass = "";
		try {
			hashedPass = hashPass(dtoRegisterUser.getPassword());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
		
		//Check if user is already registered by trying to login
		if(userservice.login(dtoRegisterUser.getEmail(), hashedPass) == null) {
			User newUser = new User(dtoRegisterUser.getFirstname(), dtoRegisterUser.getLastname(), dtoRegisterUser.getEmail(), hashedPass);
			userservice.register(newUser);
			return newUser;
		} else {
			return null;
		}
	}

	@PostMapping(value = "/getUserById")
	public @ResponseBody User getUserById(@RequestBody DtoString dtoString) {
		User loggingUser = userservice.getUserById(dtoString.getFormString());
		return loggingUser;
	}
	
	
	/*
	 * Login --> If credentials match, generate a unique sessionToken for a User
	 * 				return this User (which also contains the sessionToken)
	 */
	
	@PostMapping(value = "/login")
	public @ResponseBody User login(@RequestBody DtoLoginUser dtoLoginUser) {
		String hashedPass = "";
		try {
			hashedPass = hashPass(dtoLoginUser.getPassword());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
		
		
		User loggingUser = userservice.login(dtoLoginUser.getEmail(), hashedPass);
		if (loggingUser != null) {
			User userWithSessionToken = userservice.generateSessionToken(loggingUser);
			// This value will be null if after x attempts, a unique session token could not be generated
			return userWithSessionToken;             
		}
		return null;
	}
	
	@PostMapping(value = "/viewTasks")
	public @ResponseBody List<Task> viewTasks(@RequestBody DtoUserSessionKey dtoKey) { 
		if(dtoKey.getSessionToken() != null) {
			DtoUserSessionKey loggedinUser = dtoKey;
			return userservice.viewTasks(loggedinUser.getId());
			
		} else {
			//return null;
			return null;
		}
		
	}

	@PostMapping(value = "/logout")
	public User logout(@RequestBody DtoUser dtoUser) {
		
		User user = new User(dtoUser.getId(),
				dtoUser.getFirstName(), dtoUser.getLastName(), dtoUser.getEmail(), dtoUser.getPassword(),
				LocalDate.of(dtoUser.getAccountCreatedYear(), dtoUser.getAccountCreatedMonth(), dtoUser.getAccountCreatedDay()),
				dtoUser.getReceiveEmailReminders(), dtoUser.getGoal());
				
				
		userservice.deleteSessionToken(user);
		return new User();
		
	}

	@PostMapping(value = "/deleteAccount")
	public @ResponseBody User unregister(@RequestBody DtoUserPasswordSessionKey dtoUserPasswordSessionKey) {
		if (dtoUserPasswordSessionKey.getSessionToken() != null) {
			DtoUserPasswordSessionKey loggedinUser = dtoUserPasswordSessionKey;
			
			
			
			if (userservice.unregister(loggedinUser.getEmail(), loggedinUser.getPassword())) {
				return new User();
			}
				
			
		}
		return null;
	}

	@PostMapping(value = "/downloadMyData")
	public @ResponseBody DtoString downloadMyData(@RequestBody DtoUser dtoUser) {
		if(dtoUser.getSessionToken() != null) {
			
			DtoUser loggedinUser = dtoUser;
			String userData = userservice.downloadMyData(loggedinUser.getEmail(), loggedinUser.getPassword(), loggedinUser.getId());
			
			DtoString returnString = new DtoString();
			returnString.setFormString(userData);
			return returnString;
			
		} else {
			
			DtoString returnString = new DtoString();
			returnString.setFormString("No user data can be retreived, make sure user is registered and loggedin");
			return returnString;
			
		}
	}

	
	@PostMapping(value = "/updateEmailReminders")
	public @ResponseBody boolean receiveEmailReminders(@RequestBody DtoInteger dtoInteger, @RequestBody User u) {
		if(u.getSessionToken() != null) {
			User loggedinUser = u;
			loggedinUser.setReceiveEmailReminders(dtoInteger.getFormInteger());
			return userservice.receiveEmailReminders(loggedinUser);
		} else {
			return false;
		}
	}
	
	@PostMapping(value = "/updateDailyGoals")
	public @ResponseBody boolean setDailyGoals(@RequestBody DtoInteger dtoInteger, @RequestBody User u) {
		if(u.getSessionToken() != null) {
			User loggedinUser = u;
			loggedinUser.setGoal(dtoInteger.getFormInteger());
			return userservice.setDailyGoals(loggedinUser);
		} else {
			return false;
		}
	}

	@PostMapping(value = "/updateUserInfo")
	public @ResponseBody boolean updateUserInfo(@RequestBody DtoUpdatedUser dtoUpdatedUser, @RequestBody User u) {
		if(u.getSessionToken() != null) {			
			//First convert password into hashed password only is isPasswordChanging.equals("y")
			if(dtoUpdatedUser.getIsPasswordChanging().equals("y")) {
				String hashedPass = "";
				try {
					hashedPass = hashPass(dtoUpdatedUser.getPassword());
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
					return false;
				}
				
				User updatedUser = new User(dtoUpdatedUser.getId(), dtoUpdatedUser.getFirstname(), dtoUpdatedUser.getLastname(),
						dtoUpdatedUser.getEmail(), hashedPass, LocalDate.parse(dtoUpdatedUser.getAccountCreated()), 
						dtoUpdatedUser.getReceiveEmailReminders(), dtoUpdatedUser.getGoal());
				
				return userservice.updateUserInfo(updatedUser);
			} else {
				User updatedUser = new User(dtoUpdatedUser.getId(), dtoUpdatedUser.getFirstname(), dtoUpdatedUser.getLastname(),
						dtoUpdatedUser.getEmail(), dtoUpdatedUser.getPassword(), LocalDate.parse(dtoUpdatedUser.getAccountCreated()), 
						dtoUpdatedUser.getReceiveEmailReminders(), dtoUpdatedUser.getGoal());
				

				return userservice.updateUserInfo(updatedUser);
			}
		} else {
			return false;
		}
	}
	
	
	@PostMapping(value = "/addTask")
	public @ResponseBody Task createTask(@RequestBody DtoTask dtoTask) {
		// Create task out of the request, will use save() in dao
		if(dtoTask.getSessionToken() != null) {

			Task newTask = new Task(dtoTask.getUserId(), dtoTask.getTaskName(), dtoTask.getNotes(), 
					LocalDate.of(dtoTask.getDueDateYear(), dtoTask.getDueDateMonth(), dtoTask.getDueDateDay()), 
					dtoTask.getReminder(), dtoTask.isRepeatable(),
					dtoTask.getTaskLabel(), dtoTask.getLatitude(), dtoTask.getLongitude());
			
					userservice.createTask(newTask);
					
					return newTask;
		} else {
			return null;
		}
	}

	@PostMapping(value = "/updateTask")
	public @ResponseBody List<Task> updateTask(@RequestBody DtoUpdatedTask dtoUpdatedTask) {
		// we receive an updated task from the frontend, it should have the id of the task
		if(dtoUpdatedTask.getSessionToken() != null) {
			//We convert from DtoUpdatedTask to Task
			
			
		
			LocalDate dueDate = null;
			if(dtoUpdatedTask.getDueDateYear() != null) {
				dueDate = LocalDate.of(dtoUpdatedTask.getDueDateYear(), dtoUpdatedTask.getDueDateMonth(), dtoUpdatedTask.getDueDateDay());
			} 
			
			LocalDate dateCompleted = null;
			if(dtoUpdatedTask.getCompletedYear() != null) {
				dateCompleted = LocalDate.of(dtoUpdatedTask.getCompletedYear(), dtoUpdatedTask.getCompletedMonth(), dtoUpdatedTask.getCompletedDay());
			}
			
			LocalDate dateCreated = null;
			if(dtoUpdatedTask.getCreatedYear() != null) {
				dateCreated = LocalDate.of(dtoUpdatedTask.getCreatedYear(), dtoUpdatedTask.getCreatedMonth(), dtoUpdatedTask.getCreatedDay());
			} 
			
			Task updatedTask = new Task(dtoUpdatedTask.getId(), dtoUpdatedTask.getUserId(),
										dtoUpdatedTask.getTaskName(), dtoUpdatedTask.getNotes(),
										dateCreated, dueDate, dateCompleted,
										dtoUpdatedTask.getReminder(), dtoUpdatedTask.isRepeatable(),
										dtoUpdatedTask.getTaskLabel_fk(), dtoUpdatedTask.getLatitude(), dtoUpdatedTask.getLongitude());
			
			boolean couldUpdate = userservice.updateTask(updatedTask);
			
			if (couldUpdate) {
				return userservice.viewTasks(dtoUpdatedTask.getUserId());
			} else {
				return null;
			}
		} else {
			return null;
		}
		
	}
	
	@PostMapping(value = "/deleteTask")
	public @ResponseBody List<Task> deleteTask(@RequestBody DtoUpdatedTask dtoTask) { //dtoString is taskId from the frontend
		if(dtoTask.getSessionToken() != null) {
			if (userservice.deleteTask(dtoTask.getId())) {
				return userservice.viewTasks(dtoTask.getUserId());
			} else {
				return null;
			}
		} else {
			return null;
		}
		
	}

	

	@PostMapping(value = "/completeTask")
	public @ResponseBody List<Task> completeTask(@RequestBody DtoUpdatedTask dtoUpdatedTask) {
		
		LocalDate nowTime = LocalDate.now();
		
		if(dtoUpdatedTask.getSessionToken() != null) {
			dtoUpdatedTask.setCompletedYear(nowTime.getYear());
			dtoUpdatedTask.setCompletedMonth(nowTime.getMonthValue());
			dtoUpdatedTask.setCompletedDay(nowTime.getDayOfMonth());
			return updateTask(dtoUpdatedTask);
		} else {
			return null;
		}
	}

	@GetMapping(value = "/viewCompleted")
	public @ResponseBody List<Task> viewCompleted(@RequestBody User u) {
		if(u.getSessionToken() != null) {
			User loggedinUser = u;
			return userservice.viewCompleted(loggedinUser.getId());
		} else {
			return null;
		}
	}

	
	@PostMapping(value = "/duedateTask")
	public @ResponseBody List<Task> duedateTask(@RequestBody DtoUpdatedTask dtoUpdatedTask) { 
		if(dtoUpdatedTask.getSessionToken() != null) {
			return updateTask(dtoUpdatedTask);
		} else {
			return null;
		}
	}


	@PostMapping(value = "/viewDuedate")
	public @ResponseBody List<Task> viewDuedate(@RequestBody DtoString upperBoundDate, @RequestBody User u) {
		if(u.getSessionToken() != null) {
			User loggedinUser = u;
			return userservice.viewDuedate(loggedinUser.getId(), upperBoundDate.getFormString());
		} else {
			return null;
		}
	}

	@PostMapping(value = "/setRepeatableTask")
	public @ResponseBody List<Task> setRepeatableTask(@RequestBody DtoUpdatedTask dtoUpdatedTask) {
		if(userservice.getUserFromSessionToken(dtoUpdatedTask.getSessionToken()).getId()  .  equals(dtoUpdatedTask.getUserId())) {
			return updateTask(dtoUpdatedTask);
		} else {
			return null;
		}
		
	}
	
	
	
	
	
	
	
	
	public int getMonthNumFromString(String monthString)  {
		 Date date;
		try {
			date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthString);
			Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    int month = cal.get(Calendar.MONTH);
		    return month + 1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 1;
			
		}
		    
	}

}