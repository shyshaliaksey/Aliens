package by.shyshaliaksey.webproject.model.dao.impl;

import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_ID;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_EMAIL;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_LOGIN_NAME;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_STATUS;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_PASSWORD_HASH;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_BANNED_TO_DATE;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_IMAGE_URL;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USER_ROLE_TYPE;
import static by.shyshaliaksey.webproject.model.dao.ColumnName.USERS_COUNT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import by.shyshaliaksey.webproject.exception.DaoException;
import by.shyshaliaksey.webproject.exception.ServiceException;
import by.shyshaliaksey.webproject.model.connection.ConnectionPool;
import by.shyshaliaksey.webproject.model.dao.UserDao;
import by.shyshaliaksey.webproject.model.entity.User;
import by.shyshaliaksey.webproject.model.entity.UserStatus;
import by.shyshaliaksey.webproject.model.service.ServiceProvider;
import by.shyshaliaksey.webproject.model.service.TimeService;
import by.shyshaliaksey.webproject.model.entity.Comment;
import by.shyshaliaksey.webproject.model.entity.Role;

public class UserDaoImpl implements UserDao {

	private static final Logger logger = LogManager.getRootLogger();
	private static final UserDaoImpl instance = new UserDaoImpl();
	private static final String SPACE = " ";
	private static final String FIND_ALL = "SELECT user_id, email, login_name, image_url, role_type, _status, banned_to_datetime FROM users";
	private static final String FIND_BY_ID = String.join(SPACE, FIND_ALL, "WHERE users.user_id=?");
	private static final String FIND_BY_LOGIN = String.join(SPACE, FIND_ALL, "WHERE users.login_name=?");
	private static final String FIND_BY_EMAIL = String.join(SPACE, FIND_ALL, "WHERE users.email=?");
	private static final String LOGIN_AND_PASSWORD_CHECK = "SELECT count(*) as usersCount FROM users WHERE email=? AND password_hash=?";
	private static final String REGISTER = "INSERT INTO users (email, login_name, password_hash, image_url, role_type, _status) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_EMAIL = "UPDATE users SET email = ? WHERE user_id = ?";
	private static final String UPDATE_LOGIN = "UPDATE users SET login_name = ? WHERE user_id = ?";
	private static final String UPDATE_PASSWORD = "UPDATE users SET password_hash = ? WHERE user_id = ?";
	private static final String UPDATE_PROFILE_IMAGE = "UPDATE users SET image_url = ? WHERE user_id = ?";
	private static final String BAN_UNBAN = "UPDATE users SET _status = ?, banned_to_datetime = ? WHERE login_name = ?";
	private static final String PROMOTE_DEMOTE = "UPDATE users SET role_type = ? WHERE login_name = ?";
	private static final String ADD_NEW_COMMENT = "INSERT INTO comments (user_id, alien_id, comment, comment_status) VALUES (?, ?, ?, ?)";
	private static final String CHANGE_COMMENT_STATUS = "UPDATE comments SET comment_status = ? WHERE comment_id = ?";

	public static UserDaoImpl getInstance() {
		return instance;
	}

	@Override
	public List<User> findAll() throws DaoException {
		List<User> users = new ArrayList<>();
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(FIND_ALL);
			while (resultSet.next()) {
				int id = resultSet.getInt(USER_ID);
				String email = resultSet.getString(USER_EMAIL);
				String loginName = resultSet.getString(USER_LOGIN_NAME);
				String imageUrl = resultSet.getString(USER_IMAGE_URL);
				Role role = Role.valueOf(resultSet.getString(USER_ROLE_TYPE));
				UserStatus userStatus = UserStatus.valueOf(resultSet.getString(USER_STATUS));
				Date bannedToDate = resultSet.getDate(USER_BANNED_TO_DATE);
				User user = new User(id, email, loginName, imageUrl, role, userStatus, bannedToDate);
				users.add(user);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", FIND_ALL, e.getMessage());
			throw new DaoException("Can not proceed request: " + FIND_ALL, e);
		}
		return users;
	}

	@Override
	public Optional<User> findById(int userId) throws DaoException {
		Optional<User> user = Optional.empty();
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				String email = resultSet.getString(USER_EMAIL);
				String loginName = resultSet.getString(USER_LOGIN_NAME);
				String imageUrl = resultSet.getString(USER_IMAGE_URL);
				Role role = Role.valueOf(resultSet.getString(USER_ROLE_TYPE));
				UserStatus userStatus = UserStatus.valueOf(resultSet.getString(USER_STATUS));
				Date bannedToDate = resultSet.getDate(USER_BANNED_TO_DATE);
				user = Optional.of(new User(userId, email, loginName, imageUrl, role, userStatus, bannedToDate));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", FIND_BY_ID, e.getMessage());
			throw new DaoException("Can not proceed request: " + FIND_BY_ID, e);
		}
		return user;
	}

	@Override
	public Optional<User> findByLogin(String userLogin) throws DaoException {
		Optional<User> user = Optional.empty();
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_LOGIN)) {
			statement.setString(1, userLogin);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int userId = resultSet.getInt(USER_ID);
				String email = resultSet.getString(USER_EMAIL);
				String loginName = resultSet.getString(USER_LOGIN_NAME);
				String imageUrl = resultSet.getString(USER_IMAGE_URL);
				Role role = Role.valueOf(resultSet.getString(USER_ROLE_TYPE));
				UserStatus userStatus = UserStatus.valueOf(resultSet.getString(USER_STATUS));
				Date bannedToDate = resultSet.getDate(USER_BANNED_TO_DATE);
				user = Optional.of(new User(userId, email, loginName, imageUrl, role, userStatus, bannedToDate));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", FIND_BY_LOGIN, e.getMessage());
			throw new DaoException("Can not proceed request: " + FIND_BY_LOGIN, e);
		}
		return user;
	}

	@Override
	public Optional<User> findByEmail(String email) throws DaoException {
		Optional<User> user = Optional.empty();
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {
			statement.setString(1, email);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int userId = resultSet.getInt(USER_ID);
				String loginName = resultSet.getString(USER_LOGIN_NAME);
				String imageUrl = resultSet.getString(USER_IMAGE_URL);
				Role role = Role.valueOf(resultSet.getString(USER_ROLE_TYPE));
				UserStatus userStatus = UserStatus.valueOf(resultSet.getString(USER_STATUS));
				Date bannedToDate = resultSet.getDate(USER_BANNED_TO_DATE);
				user = Optional.of(new User(userId, email, loginName, imageUrl, role, userStatus, bannedToDate));
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", FIND_BY_EMAIL, e.getMessage());
			throw new DaoException("Can not proceed request: " + FIND_BY_EMAIL, e);
		}
		return user;
	}

	@Override
	public boolean registerUser(String email, String login, String passwordHash, String imagePath, Role role)
			throws DaoException {
		int rowsAdded = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(REGISTER)) {
			statement.setString(1, email);
			statement.setString(2, login);
			statement.setString(3, passwordHash);
			statement.setString(4, imagePath);
			statement.setString(5, role.getValue());
			statement.setString(6, UserStatus.NORMAL.getValue());
			rowsAdded = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", REGISTER, e.getMessage());
			throw new DaoException("Can not proceed request: " + REGISTER, e);
		}
		return rowsAdded == 1;
	}

	@Override
	public boolean loginUser(String email, String passwordHash) throws DaoException {
		int usersCount = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(LOGIN_AND_PASSWORD_CHECK)) {
			statement.setString(1, email);
			statement.setString(2, passwordHash);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				usersCount = resultSet.getInt(USERS_COUNT);
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", LOGIN_AND_PASSWORD_CHECK, e.getMessage());
			throw new DaoException("Can not proceed request: " + LOGIN_AND_PASSWORD_CHECK, e);
		}
		return usersCount == 1;
	}

	@Override
	public boolean updateUserEmail(String email, int userId) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_EMAIL)) {
			statement.setString(1, email);
			statement.setInt(2, userId);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", UPDATE_EMAIL, e.getMessage());
			throw new DaoException("Can not proceed request: " + UPDATE_EMAIL, e);
		}
		return result == 1;
	}

	@Override
	public boolean updateUserLogin(String login, int userId) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_LOGIN)) {
			statement.setString(1, login);
			statement.setInt(2, userId);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", UPDATE_LOGIN, e.getMessage());
			throw new DaoException("Can not proceed request: " + UPDATE_LOGIN, e);
		}
		return result == 1;
	}

	@Override
	public boolean updateUserPassword(String hashedPassword, int userId) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_PASSWORD)) {
			statement.setString(1, hashedPassword);
			statement.setInt(2, userId);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", UPDATE_PASSWORD, e.getMessage());
			throw new DaoException("Can not proceed request: " + UPDATE_PASSWORD, e);
		}
		return result == 1;
	}

	@Override
	public boolean updateProfileImage(String newFileName, int userId) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_PROFILE_IMAGE)) {
			statement.setString(1, newFileName);
			statement.setInt(2, userId);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", UPDATE_PROFILE_IMAGE, e.getMessage());
			throw new DaoException("Can not proceed request: " + UPDATE_PROFILE_IMAGE, e);
		}
		return result == 1;
	}

	@Override
	public boolean banUser(String userLogin, String banDate) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(BAN_UNBAN)) {
			statement.setString(1, UserStatus.BANNED.getValue());
			statement.setString(2, banDate);
			statement.setString(3, userLogin);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", BAN_UNBAN, e.getMessage());
			throw new DaoException("Can not proceed request: " + BAN_UNBAN, e);
		}
		return result == 1;
	}

	@Override
	public boolean unbanUser(String userLogin, String unbanDate) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(BAN_UNBAN)) {
			statement.setString(1, UserStatus.NORMAL.getValue());
			statement.setString(2, unbanDate);
			statement.setString(3, userLogin);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", BAN_UNBAN, e.getMessage());
			throw new DaoException("Can not proceed request: " + BAN_UNBAN, e);
		}
		return result == 1;
	}

	@Override
	public boolean promoteUser(String userLogin) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(PROMOTE_DEMOTE)) {
			statement.setString(1, Role.ADMIN.getValue());
			statement.setString(2, userLogin);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", PROMOTE_DEMOTE, e.getMessage());
			throw new DaoException("Can not proceed request: " + PROMOTE_DEMOTE, e);
		}
		return result == 1;
	}

	@Override
	public boolean demoteAdmin(String adminLogin) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(PROMOTE_DEMOTE)) {
			statement.setString(1, Role.USER.getValue());
			statement.setString(2, adminLogin);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", PROMOTE_DEMOTE, e.getMessage());
			throw new DaoException("Can not proceed request: " + PROMOTE_DEMOTE, e);
		}
		return result == 1;
	}

	@Override
	public boolean addNewComment(int userId, int alienId, String newComment) throws DaoException {
		int rowsAdded = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(ADD_NEW_COMMENT)) {
			statement.setInt(1, userId);
			statement.setInt(2, alienId);
			statement.setString(3, newComment);
			statement.setString(4, Comment.CommentStatus.NORMAL.toString());
			rowsAdded = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", ADD_NEW_COMMENT, e.getMessage());
			throw new DaoException("Can not proceed request: " + ADD_NEW_COMMENT, e);
		}
		return rowsAdded == 1;
	}

	@Override
	public boolean deleteComment(int commentId) throws DaoException {
		int result = 0;
		try (Connection connection = ConnectionPool.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement(CHANGE_COMMENT_STATUS)) {
			statement.setString(1, Comment.CommentStatus.DELETED.toString());
			statement.setInt(2, commentId);
			result = statement.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.ERROR, "Can not proceed `{}` request: {}", CHANGE_COMMENT_STATUS, e.getMessage());
			throw new DaoException("Can not proceed request: " + CHANGE_COMMENT_STATUS, e);
		}
		return result == 1;
	}
	
	
	
	

}
